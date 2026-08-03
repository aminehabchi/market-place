# Payments Service — Architecture, Integration, and Troubleshooting

## Purpose

`payments-service` adds server-side Stripe Checkout support to Buy01 Marketplace. The Angular application never calls Stripe with a secret key. Instead, it sends a validated checkout request through Spring Cloud Gateway, the payment service creates a Stripe-hosted Checkout Session, and the frontend redirects the buyer to the returned URL.

The service is intentionally small and stateless. Product and user data remain owned by their existing microservices; payments receives only the fields required to initiate checkout and adds identifiers as Stripe metadata.

## Technology

- Java 21
- Spring Boot 3.2
- Spring Cloud Netflix Eureka Client
- Stripe Java SDK
- Jakarta Bean Validation
- HTTPS and inbound mTLS
- Maven, Surefire, and JaCoCo
- Docker and Docker Compose
- Jenkins Secret file credentials

## Request Flow

1. Angular sends `POST /api/payments/checkout-sessions` to the public marketplace domain.
2. Caddy forwards `/api/**` traffic to Spring Cloud Gateway.
3. The gateway validates the JWT when present, applies Redis rate limiting, and adds trusted identity headers.
4. The gateway matches `/api/payments/**` and resolves `lb://payments` through Eureka.
5. Gateway opens an mTLS connection to payments-service on port `8010`.
6. `PaymentController` validates the JSON request and reads the optional `X-User-Id` header.
7. `StripeCheckoutService` normalizes the currency, converts the decimal price to minor units, and creates a Stripe Checkout Session.
8. Product ID and buyer ID are included as Stripe metadata for later reconciliation and webhook processing.
9. The service returns the Stripe Session ID and hosted checkout URL.
10. Angular redirects the buyer to Stripe. Stripe returns the buyer to the configured success or cancellation URL.

## API Contract

Endpoint:

```http
POST /api/payments/checkout-sessions
```

The request includes the product identifier, display name, amount, currency, and optional image URL. Bean Validation rejects invalid requests before Stripe is called.

Successful response:

```json
{
  "sessionId": "cs_test_...",
  "url": "https://checkout.stripe.com/..."
}
```

The service currently returns:

- `200 OK` when Stripe creates the session;
- `503 Service Unavailable` when the Stripe secret is not configured;
- `502 Bad Gateway` when Stripe rejects the request or cannot be reached;
- a validation error for malformed checkout input.

## Stripe Key Model

Stripe provides different key types:

- `pk_test_...` and `pk_live_...` are publishable keys intended for browser/client use.
- `sk_test_...` and `sk_live_...` are secret keys intended only for trusted server-side code.

`STRIPE_SECRET_KEY` must contain an `sk_test_...` key during development or an `sk_live_...` key in production. It must never contain a publishable key and must never be committed to Git, printed in logs, or embedded in the Angular bundle.

## Environment Configuration

The runtime file is `payments-service/.env.payments`. It is ignored by Git and injected by Jenkins.

Required variables:

```dotenv
DISCOVERY=http://eureka-server:8761/eureka
STRIPE_SECRET_KEY=sk_test_replace_me
APP_PUBLIC_URL=https://marketplace.bouchikhi.com
KEY_STORE_PWD=replace_me
KEY_STORE_TYPE=PKCS12
TRUST_STORE_PWD=replace_me
CERTIFICATE_PATH=file:/app/resources/certs/payments-service.p12
TRUSTSTORE_PATH=file:/app/resources/certs/truststore.p12
```

Docker env files accept `KEY=value`, blank lines, and comments beginning with `#`. They do not accept JavaScript-style `//` comments.

## TLS and mTLS Design

The service has two different TLS responsibilities that must remain separate.

### Inbound internal traffic

Payments serves HTTPS and requires a trusted client certificate:

```properties
server.ssl.enabled=true
server.ssl.client-auth=need
server.ssl.key-store=${CERTIFICATE_PATH}
server.ssl.trust-store=${TRUSTSTORE_PATH}
```

`payments-service.p12` identifies the service. The private `truststore.p12` trusts the project CA that issued the gateway and service certificates. Both files are mounted read-only from the external `payments-service-certs` Docker volume.

### Outbound Stripe traffic

Stripe uses a publicly trusted certificate. Outbound calls to `api.stripe.com` must therefore use Java's standard public CA bundle.

The internal truststore contains only the project CA. Setting it globally with `-Djavax.net.ssl.trustStore=...` replaces Java's public roots and breaks Stripe certificate validation. For this reason, payments Compose clears `JAVA_TOOL_OPTIONS`; Spring's `server.ssl.*` configuration continues to protect inbound traffic without changing the global outbound trust manager.

## Certificate Lifecycle

`scripts/generate-certs.sh` creates:

- `payments-service.p12`;
- the shared `truststore.p12`;
- the project CA certificate.

`scripts/setup-cert-volumes.sh --service payments-service` copies only the required files into `payments-service-certs`.

`scripts/sync-cert-volumes-to-server.sh --service payments-service` stages those generated files, uploads them to the Oracle/cloud host, runs the setup script remotely, and removes the temporary remote stage.

The values of `KEY_STORE_PWD` and `TRUST_STORE_PWD` must match the password used when the PKCS12 files were generated. Updating only the environment or only the volume creates an unusable pair.

## Jenkins Integration

Jenkins uses a Secret file credential with the exact ID:

```text
env-payments
```

`getCredentialsList()` exposes it as `PAYMENTS_ENV`. `scripts/ci/setup_env.sh` copies it to `payments-service/.env.payments` with mode `600` before build/deployment. The same credential flow is available during rollback.

The pipeline removes `.env.payments` in post actions, so secrets do not remain in the Jenkins workspace. The file is also ignored by Git.

The deployment order starts infrastructure and discovery before payments, then starts the gateway after the domain services. Rollback treats payments as optional for older commits that predate this service.

## Errors Encountered and Resolutions

### 1. Payment certificate missing from the volume

Observed error:

```text
FileNotFoundException: /app/resources/certs/payments-service.p12
```

Cause: the external `payments-service-certs` volume existed, but it had not been populated with the new service certificate.

Resolution: payments was added to certificate generation, local volume setup, remote synchronization, deployment volume creation, and rollback. Both certificate scripts gained repeatable `--service` support so payments can be repaired without requiring or replacing unrelated service material.

### 2. Service could not be instantiated

Observed error:

```text
NoSuchMethodException: StripeCheckoutService.<init>()
```

Cause: `StripeCheckoutService` has a production constructor and a package-private test constructor. With multiple constructors, Spring could not determine which one to inject.

Resolution: the production constructor was marked with `@Autowired`, keeping the test seam while making Spring's choice explicit.

### 3. Checkout returned 503

Observed response:

```text
503 Service Unavailable
Stripe secret key is not configured
```

Cause: `STRIPE_SECRET_KEY` was empty. Compose also explicitly assigned an empty interpolated value, which overrode values loaded from `env_file`.

Resolution: payments received a dedicated `.env.payments`; empty Compose overrides were removed; Jenkins gained the `env-payments` Secret file credential. The service now receives the key only from its secure runtime environment.

### 4. Docker rejected the environment file

Observed error:

```text
unexpected character "/" in variable name
```

Cause: the environment file contained a line beginning with `//`, followed by a `pk_test_...` key. Docker env files do not support `//` comments. In addition, `pk_test_...` is a publishable key and is invalid for server authentication.

Resolution: comments use `#`, and `STRIPE_SECRET_KEY` uses an `sk_test_...` or `sk_live_...` key.

### 5. Keystore password was incorrect

Observed errors:

```text
keystore password was incorrect
UnrecoverableKeyException
BadPaddingException
```

Cause: the password in `.env.payments` and the Jenkins credential did not match the password used by `generate-certs.sh`. At other times, a newly generated local certificate was paired with a stale remote volume.

Resolution: both PKCS12 files were validated with `keytool`; the environment passwords were aligned with the generator password; certificate SHA-256 hashes were compared locally and remotely; the Docker volume and Jenkins Secret file were resynchronized together.

### 6. Eureka registration failed with an SSL message

Observed error:

```text
Unsupported or unrecognized SSL message
```

Cause: payments attempted `https://eureka-server:8761`, but Eureka serves plain HTTP on its internal port.

Resolution:

```dotenv
DISCOVERY=http://eureka-server:8761/eureka
```

This discovery connection is isolated inside `shared-net`. Payments itself still serves HTTPS/mTLS to the gateway.

### 7. Stripe checkout returned 502

Observed response:

```text
502 Bad Gateway
Stripe checkout session creation failed
```

Cause: the internal one-entry truststore was configured globally through `JAVA_TOOL_OPTIONS`. Java could trust the private project CA but no longer had the public root certificates needed for Stripe HTTPS.

Resolution: payments Compose clears `JAVA_TOOL_OPTIONS`. Inbound mTLS remains configured with Spring server SSL properties, while outbound Stripe calls use Java's default public CA bundle.

### 8. Stripe errors were hidden

Problem: the frontend received a generic 502 with no safe operational detail, making key, request, and TLS failures indistinguishable.

Resolution: `PaymentController` now logs the Stripe exception class, API error code, Stripe request ID, and stack trace on the server. Sensitive keys are never logged, and the browser continues to receive a controlled generic message.

## Operational Checks

Check certificate volume contents:

```bash
docker run --rm -v payments-service-certs:/certs:ro alpine:3.20 ls -lah /certs
```

Expected files:

```text
payments-service.p12
truststore.p12
ca.crt
```

Validate startup and Eureka registration:

```bash
docker logs payments-service --tail 150
```

Expected messages include:

```text
Started PaymentsApplication
registration status: 204
```

Confirm that a secret key is present without printing it:

```bash
docker exec payments-service sh -c \
  'if [ -n "$STRIPE_SECRET_KEY" ]; then echo configured; else echo missing; fi'
```

Rebuild after configuration changes:

```bash
docker compose -f payments-service/docker-compose.yaml up -d --build --force-recreate
```

## Current Scope and Recommended Evolution

The current service creates one-time Stripe Checkout payment sessions. A production marketplace should evolve it with:

- Stripe webhook signature verification and idempotent event processing;
- persistent payment/order state rather than trusting browser return URLs;
- server-side product and price lookup instead of accepting authoritative amounts from the client;
- Stripe Connect accounts, onboarding, destination charges, application fees, transfers, and refunds;
- Stripe Tax calculation and customer address collection;
- Billing subscriptions where recurring marketplace plans are required;
- invoice creation and reconciliation;
- idempotency keys for checkout creation;
- restricted Stripe keys with least-privilege access;
- structured metrics, tracing, alerts, and dead-letter handling;
- integration tests against Stripe test mode or a controlled mock server.

Most importantly, the backend must derive the payable amount from trusted product data. Client-provided names and prices are useful for UI requests but must not become the final source of truth for real charges.

## Next Implementation Plan

The next version should stop accepting an authoritative amount, product name, seller, or currency from Angular. The browser is not a trusted source because a buyer can modify any outgoing request. Angular should send only the buyer's choices:

```json
{
  "productId": "product-id",
  "quantity": 1
}
```

Do not implement checkout as two browser requests where Angular first reads a product and then sends the returned price to payments. The displayed price is useful for the UI, but data that passes through the browser can still be changed.

### Phase 1 — Authoritative Product Checkout API

Add a private endpoint to products-service, for example:

```http
GET /internal/products/{productId}/checkout?quantity=1
```

It should return a minimal trusted snapshot:

```json
{
  "productId": "...",
  "name": "...",
  "unitAmount": 1299,
  "currency": "usd",
  "quantity": 1,
  "sellerId": "...",
  "sellerStripeAccountId": "acct_...",
  "available": true,
  "version": 12
}
```

Products-service must verify that:

- the product exists and is active;
- its current price comes from MongoDB;
- the requested quantity is allowed;
- sufficient stock is available;
- the seller is allowed to receive payments;
- the currency and minor-unit amount are valid;
- the product version has not changed during preparation.

Keep this endpoint private to the Docker network. Authenticate the calling payment service using mTLS and, if needed, a service-specific authorization policy. Do not expose it as a normal browser route.

### Phase 2 — Synchronous Checkout Validation

Change the public payment request DTO so it accepts only `productId` and `quantity`. When payments receives the request:

1. Read the authenticated buyer ID from the trusted gateway header.
2. Call the private products-service checkout endpoint synchronously.
3. Reject missing, inactive, unavailable, or invalid products immediately.
4. Use the returned unit amount, currency, product name, seller, and version.
5. Calculate the total and any marketplace fee on the server.
6. Create the Stripe Checkout Session from this trusted snapshot.

Use synchronous HTTP for this step rather than Kafka. Stripe session creation needs an immediate authoritative answer; Kafka is not a natural request-response mechanism and would introduce timeout, correlation, retry, and race-condition complexity.

### Phase 3 — Pending Order and Idempotency

Before calling Stripe, persist a pending payment/order record containing:

- internal order/payment ID;
- buyer ID;
- product ID and product version;
- seller ID;
- quantity;
- unit amount, total, currency, and marketplace fee;
- status such as `PENDING_CHECKOUT`;
- creation and expiration timestamps;
- Stripe Checkout Session ID when available.

Use the internal payment ID as a Stripe idempotency key. Repeated frontend requests or network retries must return/reuse the same logical checkout rather than creating multiple payable sessions.

### Phase 4 — Inventory Reservation

For products with limited stock, reserve inventory before creating the Stripe session:

```text
payments-service -> products-service: reserve(productId, quantity, paymentId, expiresAt)
```

Products-service owns inventory and must make reservation operations atomic and idempotent. If Stripe session creation fails, cancel the reservation. If checkout expires, release it. After confirmed payment, convert the reservation into a committed stock reduction.

### Phase 5 — Stripe Webhooks

Add a public Stripe webhook endpoint to payments-service. Verify every event using `STRIPE_WEBHOOK_SECRET`; never trust an unsigned webhook body or the browser success redirect.

Handle at least:

- `checkout.session.completed`;
- `checkout.session.expired`;
- `payment_intent.payment_failed`;
- refund and dispute events when those features are added.

Store processed Stripe event IDs so webhook retries are idempotent. The success page must read payment status from the backend; reaching `?payment=success` is not proof that money was received.

### Phase 6 — Kafka Payment Events

Publish domain events only after the webhook transaction updates the internal payment state. Useful topics/events include:

```text
PaymentSucceeded
PaymentFailed
PaymentExpired
PaymentRefunded
OrderCancelled
```

Kafka consumers can then:

- commit or release product inventory;
- create order history projections;
- notify buyers and sellers;
- trigger invoices or email receipts;
- update analytics and reporting.

Every consumer should use the payment ID and event ID for idempotency because Kafka delivery and consumer retries can produce duplicate processing attempts.

### Phase 7 — Stripe Connect Marketplace Payouts

The current implementation sends the full charge to the platform Stripe balance. To pay sellers automatically, onboard each seller to Stripe Connect and store the connected account ID securely.

After onboarding is complete, create destination charges or another Connect charge model appropriate to the marketplace. The server should calculate:

```text
buyer total = trusted product total + applicable taxes/fees
seller amount = product total - marketplace commission
platform revenue = application fee
```

Do not accept a connected account ID or marketplace fee from Angular. Retrieve them from trusted seller/platform configuration.

### Phase 8 — Tax, Billing, and Invoicing

Once one-time marketplace payments and webhooks are reliable:

- add Stripe Tax for address collection and tax calculation;
- add Stripe Invoicing where sellers or business buyers require invoices;
- add Billing only for genuinely recurring plans or subscriptions;
- persist Stripe customer, invoice, subscription, and tax identifiers against internal entities;
- define refund, cancellation, dispute, and payout reconciliation workflows.

### Recommended Final Flow

```text
Angular
  -> POST /api/payments/checkout-sessions { productId, quantity }
Gateway
  -> validates JWT, adds buyer identity, rate limits
Payments Service
  -> requests trusted checkout snapshot from Products Service
Products Service
  -> validates product/price/seller/stock and reserves quantity
Payments Service
  -> stores pending payment and creates idempotent Stripe Session
Angular
  -> redirects to Stripe Checkout
Stripe
  -> sends signed webhook to Payments Service
Payments Service
  -> verifies webhook, records final status, publishes Kafka event
Products Service
  -> commits or releases inventory idempotently
Notifications / Orders / Analytics
  -> consume the payment event asynchronously
Stripe Connect
  -> allocates platform fee and seller funds, then handles payouts
```

### Suggested Implementation Order

1. Replace the public checkout DTO with `productId` and `quantity`.
2. Add the private authoritative product checkout endpoint.
3. Add a synchronous mTLS client from payments to products.
4. Persist pending payment/order records.
5. Add idempotency keys.
6. Add stock reservations and expiration.
7. Add verified Stripe webhooks.
8. Publish idempotent Kafka payment events.
9. Build order history and payment-status APIs.
10. Add Stripe Connect seller onboarding and destination charges.
11. Add refunds, disputes, Tax, Invoicing, and Billing as required.
