#!/usr/bin/env bash
set -euo pipefail

STATUS="${1:-unknown}"
BUILD_URL="${BUILD_URL:-}"
JOB_NAME="${JOB_NAME:-buy-01}"
BUILD_NUMBER="${BUILD_NUMBER:-0}"
GIT_COMMIT_SHORT="$(git rev-parse --short HEAD 2>/dev/null || echo 'n/a')"

MESSAGE="[${JOB_NAME} #${BUILD_NUMBER}] ${STATUS} (commit ${GIT_COMMIT_SHORT})"

if [[ -n "${BUILD_URL}" ]]; then
  MESSAGE="${MESSAGE} - ${BUILD_URL}"
fi

echo "[CI] Notification: ${MESSAGE}"

if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
  payload=$(cat <<JSON
{"text":"${MESSAGE}"}
JSON
)
  curl -fsSL -X POST -H 'Content-Type: application/json' --data "${payload}" "${SLACK_WEBHOOK_URL}" >/dev/null
fi

if command -v mail >/dev/null 2>&1 && [[ -n "${NOTIFICATION_EMAILS:-}" ]]; then
  echo "${MESSAGE}" | mail -s "Jenkins ${STATUS}: ${JOB_NAME} #${BUILD_NUMBER}" "${NOTIFICATION_EMAILS}"
fi
