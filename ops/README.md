# Marketo Operations: Disk Management, Retention, Alerts, and Backups

This document explains the operational changes made to keep Marketo reliable on an Oracle server with a 30 GB root disk. The design reduces temporary build usage, bounds continuously growing data, alerts before the disk is exhausted, and protects persistent application data from cleanup commands.

## Objectives

The changes address four different sources of disk growth:

1. Docker images, intermediate build layers, stopped containers, and build cache.
2. Jenkins workspaces, test output, Angular dependencies, and archived builds.
3. Continuously growing logs, Prometheus metrics, Kafka messages, temporary media, and payment orders.
4. Backups that could accidentally fill the same root disk they are intended to protect.

The cleanup routines intentionally preserve Docker volumes because they contain MongoDB databases, uploaded media, OpenSearch indexes, Prometheus/Grafana state, Caddy data, Jenkins state, and service certificates.

## Selective Deployment

Previously, every deployment rebuilt every service. Multi-stage Java images can temporarily require several gigabytes, so rebuilding unrelated services was the largest avoidable source of disk pressure.

`scripts/ci/deploy_local.sh` now reads the commit stored in `LAST_SUCCESSFUL_COMMIT_FILE` and compares it with `HEAD`. It rebuilds only affected services:

| Changed path | Services rebuilt |
| --- | --- |
| `shared/**` | Eureka, gateway, products, media, users |
| `eureka-server/**` | Eureka |
| `products-service/**` | Products |
| `media-service/**` | Media |
| `users-service/**` | Users |
| `payments-service/**` | Payments |
| `gateway/**` | Gateway |
| `frontend/**` | Frontend |

If the previous successful commit is unavailable, a full deployment runs. A service is also deployed when its expected container is missing, even if no source changes are detected.

Redis, Kafka, OpenSearch, Prometheus, Grafana, exporters, and monitoring infrastructure are started without forced rebuilds. Docker Compose still recreates a container when its configuration changes.

Certificate lifecycle changes force all certificate-consuming services to restart, ensuring new mounted certificates are loaded by their JVMs.

## Free-Space Guard

Before building an application image, `docker_ensure_space` checks the filesystem containing Docker's data root.

The default requirement is 8 GiB:

```bash
CI_DOCKER_MIN_FREE_GB=8
```

When available space is below the threshold, the script:

1. prints host filesystem usage;
2. prints detailed Docker image, container, volume, and build-cache usage;
3. runs aggressive non-volume cleanup;
4. checks available space again;
5. stops deployment before building if space is still insufficient.

Stopping early avoids a partially exported image and the less useful Docker error:

```text
no space left on device
```

The threshold can be overridden, but lowering it below the temporary size of a Java image build can reintroduce export failures.

## Docker Cleanup Levels

The functions live in `scripts/ci/docker_cleanup.sh`.

### Standard cleanup

```bash
source scripts/ci/docker_cleanup.sh
docker_cleanup
```

Standard cleanup removes:

- stopped containers;
- unused networks;
- unused images older than 24 hours by default;
- build cache older than 24 hours.

Configuration variables:

```bash
CI_DOCKER_CLEANUP=true
CI_DOCKER_PRUNE_UNTIL=24h
CI_DOCKER_PRUNE_ALL_IMAGES=true
```

### Aggressive cleanup

```bash
source scripts/ci/docker_cleanup.sh
docker_cleanup_aggressive
```

Aggressive cleanup runs:

```bash
docker system prune -a -f
```

It removes all currently unused images, stopped containers, unused networks, and build cache. It deliberately does not pass `--volumes`, so persistent volumes remain protected.

### Disk report

```bash
source scripts/ci/docker_cleanup.sh
docker_disk_report
```

This prints `df -h` and `docker system df -v` without changing server state.

### Per-build cleanup

After every successful Compose build, `docker_build_cleanup` removes dangling images and all unused builder cache. A failed Compose build triggers aggressive cleanup immediately before rollback.

## Removed Runtime Build Caches

Packaged Spring Boot JAR containers do not run Maven or Gradle. The following cache mounts were therefore removed from runtime containers:

- products-service Maven cache;
- gateway Maven caches;
- media-service Maven bind mount;
- users-service Maven and Gradle caches.

During the first deployment, affected containers are recreated with `--no-build` to detach old cache mounts before any new image is built. The following cache-only volumes are then removed when unused:

```text
products-service_backend_m2
gateway_backend_m2
gateway_maven_cash
users-service_gradle-cache
media-service_media_m2
```

Docker refuses to remove any volume that is still attached, so this cleanup cannot detach or delete an active data volume by mistake.

## Jenkins Retention and Workspace Cleanup

Jenkins retention was reduced from 30 builds and artifact sets to:

```text
10 build records
5 archived artifact sets
```

After Jenkins archives test reports, `scripts/ci/workspace_cleanup.sh` removes transient outputs:

- Maven `target` directories;
- Gradle `build` output;
- Angular `node_modules`;
- Angular distribution and coverage output;
- pipeline log files already archived by Jenkins.

It preserves `.jenkins-state`, dependency caches, and `last_successful_commit`, which is required for selective deployment.

## Container Log Rotation

Docker's default JSON logs can grow without limit and cannot be reclaimed by image pruning while a container is running.

Every Compose service now uses bounded JSON logs:

```yaml
logging:
  driver: json-file
  options:
    max-size: "10m"
    max-file: "3"
```

Each container therefore retains at most approximately 30 MB of Docker JSON logs. The policy takes effect when Compose creates or recreates the container.

## Daily Automatic Cleanup

The repository contains:

```text
ops/systemd/marketo-cleanup.service.in
ops/systemd/marketo-cleanup.timer
scripts/install-cleanup-timer.sh
```

Install once on the Oracle server:

```bash
cd /path/to/market-place
bash scripts/install-cleanup-timer.sh
```

The installer renders the absolute repository path into the service, installs the units under `/etc/systemd/system`, reloads systemd, and enables the timer.

The timer runs daily around 03:00 with a randomized delay of up to 20 minutes. `Persistent=true` means a missed run executes after the server starts again.

Check it with:

```bash
systemctl status marketo-cleanup.timer
systemctl list-timers marketo-cleanup.timer
journalctl -u marketo-cleanup.service --since today
```

Run it immediately with:

```bash
sudo systemctl start marketo-cleanup.service
```

The timer performs standard cleanup and removes only the known legacy build-cache volumes. It does not run aggressive cleanup daily and does not prune data volumes.

## Prometheus Disk Alerts

Alert rules are stored in:

```text
prometheus/rules/disk-alerts.yml
```

`prometheus/prometheus.yml` loads all rule files from `/etc/prometheus/rules`, and the root Compose file mounts that directory read-only.

Two alerts are defined:

| Alert | Condition | Duration |
| --- | --- | --- |
| `HostDiskSpaceWarning` | Less than 20% or 8 GiB available | 10 minutes |
| `HostDiskSpaceCritical` | Less than 10% or 4 GiB available | 5 minutes |

Node Exporter now mounts the Oracle host root at `/host` and runs with `--path.rootfs=/host`. This ensures filesystem alerts measure the VM's root disk instead of the node-exporter container overlay.

After deployment, verify rules in Prometheus:

```text
Status -> Rules
```

The rules become pending/firing inside Prometheus. Sending email, Slack, or webhook notifications additionally requires Alertmanager or Grafana notification-contact configuration and credentials; those secrets are intentionally not committed.

## Prometheus Retention

Prometheus storage is bounded by both time and size:

```text
--storage.tsdb.retention.time=15d
--storage.tsdb.retention.size=2GB
```

Prometheus removes the oldest blocks when either limit is reached. Its persistent volume remains intact.

Payments-service was added to Prometheus scraping over HTTPS/mTLS at `/actuator/prometheus`.

## Kafka Retention

Kafka broker retention is bounded through Compose:

```text
KAFKA_LOG_RETENTION_HOURS=72
KAFKA_LOG_RETENTION_BYTES=1073741824
KAFKA_LOG_SEGMENT_BYTES=134217728
KAFKA_LOG_CLEANUP_POLICY=delete
```

Defaults retain messages for up to 72 hours and approximately 1 GiB per partition. Smaller 128 MB segments allow expired data to be deleted sooner.

These values can be overridden in the Kafka runtime environment. Kafka events are integration messages, not the authoritative database, so long-term domain history remains in the owning services.

## Temporary Media Retention

Media cleanup scheduling was already enabled, but its one-minute retention was too short for a user completing a product form. It is now configurable and safer:

```text
TEMPORARY_MEDIA_RETENTION_MINUTES=60
MEDIA_CLEANUP_INTERVAL_MS=900000
```

Every 15 minutes, media-service deletes at most 100 temporary product images and 100 temporary avatars older than 60 minutes. It removes both metadata and filesystem content.

Only `TEMPORARY` media is selected. `LINKED` product images and avatars are never removed by this job. Retention is clamped to at least five minutes even if an unsafe lower value is configured.

## Payment Order Retention

Payments-service scheduling is enabled, and `PaymentOrderRetentionJob` runs daily at 03:20 UTC.

Default policy:

```text
CANCELLED orders: 30 days
PAID orders:      365 days
PENDING orders:   never deleted by retention
```

Configuration:

```text
CANCELLED_ORDER_RETENTION_DAYS=30
PAID_ORDER_RETENTION_DAYS=365
ORDER_RETENTION_CRON=0 20 3 * * *
```

The repository deletes only records matching the terminal status and an `updatedAt` cutoff. Retention values below one day are rejected at startup.

The cleanup is observed through the Micrometer timer:

```text
marketplace.payment.orders.cleanup
```

## OpenSearch Retention Decision

Product and user OpenSearch indexes are current read models rather than dated historical indexes. Deleting documents by age would remove active products or searchable users, so no unsafe time-based deletion was added.

OpenSearch disk usage should remain proportional to active catalog/user data. Existing maintenance clears flood-stage read-only blocks after host cleanup. Search indexes are derived data and can be reconstructed through the admin reindex endpoints, so they are not included in domain backups.

## External Backups

Backups must not consume the same 30 GB root filesystem. `scripts/backup-domain-data.sh` refuses a root-filesystem destination unless `ALLOW_LOCAL_BACKUPS=true` is explicitly set.

The backup includes:

- users MongoDB;
- products MongoDB;
- media metadata MongoDB;
- payments MongoDB;
- uploaded media filesystem content.

Backups use compressed MongoDB archives and a compressed media archive. Timestamped backup directories older than seven days are removed by default.

Mount Oracle Block Volume or another external filesystem, then install the timer:

```bash
bash scripts/install-backup-timer.sh /mnt/marketo-backups
```

This creates `/etc/marketo-backup.env`, installs the service/timer, and schedules a daily backup around 02:00.

Verify it:

```bash
systemctl status marketo-backup.timer
sudo systemctl start marketo-backup.service
journalctl -u marketo-backup.service -n 100
find /mnt/marketo-backups -maxdepth 2 -type f -ls
```

Change backup retention in `/etc/marketo-backup.env`:

```dotenv
MARKETO_BACKUP_DIR="/mnt/marketo-backups"
BACKUP_RETENTION_DAYS=7
```

## Protected Data

The automated cleanup does not delete:

- MongoDB data volumes;
- uploaded linked media;
- OpenSearch data volumes;
- Prometheus or Grafana volumes directly;
- Caddy state;
- Jenkins state;
- certificate volumes;
- active Docker images used by running containers;
- pending payment orders.

Never run the following on the production host unless you have verified every affected volume and have tested backups:

```bash
docker volume prune
docker system prune --volumes
```

## First-Time Server Setup

After pulling the deployment containing these changes:

```bash
cd /path/to/market-place
git pull origin main

# Install automatic safe cleanup.
bash scripts/install-cleanup-timer.sh

# Mount external storage first, then install backups.
bash scripts/install-backup-timer.sh /mnt/marketo-backups

# Inspect current usage.
source scripts/ci/docker_cleanup.sh
docker_disk_report
```

Trigger one normal Jenkins deployment so containers are recreated with log rotation and obsolete runtime cache mounts are detached.

## Troubleshooting

### Less than 8 GiB remains after cleanup

Run:

```bash
source scripts/ci/docker_cleanup.sh
docker_disk_report
```

If Docker volumes dominate, identify which domain owns them before deleting data. Large MongoDB, OpenSearch, or media volumes contain real application data and require domain-specific archival rather than Docker pruning.

If `/home/ubuntu/.m2` or Jenkins dependency caches dominate, they are reproducible caches and can be removed manually, but the next build will download dependencies again.

### Cleanup timer did not run

```bash
systemctl list-timers marketo-cleanup.timer
systemctl cat marketo-cleanup.service
journalctl -u marketo-cleanup.service -n 100
```

Confirm the repository path embedded in the unit still exists and the Docker service is running.

### Prometheus alerts are absent

```bash
docker compose up -d prometheus node-exporter
docker logs prometheus --tail 100
```

Check Prometheus `Status -> Configuration`, `Status -> Rules`, and the `node-exporter` target. Confirm `node_filesystem_avail_bytes{mountpoint="/"}` returns data.

### Backup timer fails

Confirm the backup directory is a mounted filesystem different from `/`:

```bash
findmnt /mnt/marketo-backups
cat /etc/marketo-backup.env
journalctl -u marketo-backup.service -n 100
```

The script intentionally fails rather than filling the root disk when external storage is not mounted.

