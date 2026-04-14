#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${ROOT_DIR}"

ensure_java_21() {
  local java_major
  java_major="$(java -version 2>&1 | awk -F '[\".]' '/version/ {print $2; exit}')"

  if [[ "${java_major}" == "21" ]]; then
    echo "[CI] Java 21 detected."
    return
  fi

  local candidates=(
    "/usr/lib/jvm/java-21-openjdk-amd64"
    "/usr/lib/jvm/java-1.21.0-openjdk-amd64"
    "/usr/lib/jvm/openjdk-21"
  )

  for candidate in "${candidates[@]}"; do
    if [[ -x "${candidate}/bin/java" ]]; then
      export JAVA_HOME="${candidate}"
      export PATH="${JAVA_HOME}/bin:${PATH}"
      echo "[CI] Switched JAVA_HOME to ${JAVA_HOME}"
      java -version
      return
    fi
  done

  echo "[CI] Java 21 is required. Current java version is ${java_major}."
  echo "[CI] Install JDK 21 or set JAVA_HOME to a Java 21 installation."
  exit 1
}

run_maven_module() {
  local module_dir="$1"
  local maven_goal="${2:-verify}"
  echo "[CI] Running Maven (${maven_goal}) for module: ${module_dir}"

  if [[ -x "${module_dir}/mvnw" ]]; then
    (
      cd "${module_dir}"
      ./mvnw -B clean "${maven_goal}"
    )
  else
    (
      cd "${module_dir}"
      mvn -B clean "${maven_goal}"
    )
  fi
}

run_gradle_module() {
  local module_dir="$1"
  echo "[CI] Testing Gradle module: ${module_dir}"

  (
    cd "${module_dir}"
    chmod +x ./gradlew
    export DISCOVERY="${DISCOVERY:-http://localhost:8761/eureka}"
    export MONGODB_USERNAME="${MONGODB_USERNAME:-admin}"
    export MONGODB_PWD="${MONGODB_PWD:-admin}"
    export MONGODB_HOST="${MONGODB_HOST:-localhost}"
    export MONGODB_PORT="${MONGODB_PORT:-27017}"
    export MONGO_DB="${MONGO_DB:-users_db}"
    export MONGODB_AUTH="${MONGODB_AUTH:-admin}"
    export JWT_EXP="${JWT_EXP:-3600000}"
    export SPRING_KAFKA_BOOTSTRAP_SERVERS="${SPRING_KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}"
    ./gradlew clean test
  )
}

run_frontend_tests() {
  local module_dir="frontend"
  echo "[CI] Testing Angular frontend (${module_dir})"

  (
    cd "${module_dir}"
    npm ci
    if [[ ! -f "node_modules/lightningcss-linux-x64-gnu/lightningcss.linux-x64-gnu.node" ]]; then
      echo "[CI] Reinstalling missing lightningcss Linux native binary."
      npm install --no-save --include=optional lightningcss-linux-x64-gnu@1.30.2
    fi
    if find src -type f \( -name "*.spec.ts" -o -name "*.test.ts" \) | grep -q .; then
      npm run test -- --watch=false
    else
      echo "[CI] No frontend unit tests found; skipping ng test."
    fi
    npm run build
  )
}

ensure_java_21

# Install shared library first because multiple services depend on it.
run_maven_module "shared" "install"

run_maven_module "eureka-server/eureka"
run_maven_module "gateway/gateway"
run_maven_module "products-service/products"
run_maven_module "media-service/media"

run_gradle_module "users-service/service"
run_frontend_tests

echo "[CI] Build and test completed successfully."
