#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-rest}"             # rest | grpc
TARGET="${2:-java}"           # java | node
TEST="${3:-all}"              # small | medium | large | large_compressed | stream_large | all
SERVICE_SOURCE="${4:-local}"  # local | docker
VUS="${5:-10}"                # virtual users, e.g. 10 | 50 | 100 | 500


OUT_DIR="k6/results"
mkdir -p "$OUT_DIR"

TS="$(date +%Y-%m-%d_%H-%M-%S)"

if [[ "$TARGET" != "java" && "$TARGET" != "node" ]]; then
  echo "ERROR: Unknown target '$TARGET'" >&2
  exit 1
fi

if [ "$SERVICE_SOURCE" = "local" ]; then
  if ! command -v k6 >/dev/null 2>&1; then
    echo "ERROR: k6 not found. Install via 'brew install k6'" >&2
    exit 1
  fi
  K6_CMD="k6"
  SCRIPT_DIR="k6"
  RESULT_DIR="$OUT_DIR"

  if [ "$TARGET" = "java" ]; then
    CLIENT_BASE="http://localhost:8085"   # client-spring-boot
  else
    CLIENT_BASE="http://localhost:3005"   # client-node
  fi

elif [ "$SERVICE_SOURCE" = "docker" ]; then
  if ! command -v docker >/dev/null 2>&1; then
    echo "ERROR: docker not found." >&2
    exit 1
  fi
  K6_CMD="docker compose run --rm k6"
  SCRIPT_DIR="/scripts"
  RESULT_DIR="/scripts/results"

  if [ "$TARGET" = "java" ]; then
    CLIENT_BASE="http://client-spring-boot:8086"
  else
    CLIENT_BASE="http://client-node:3006"
  fi

else
  echo "ERROR: Service source must be 'local' or 'docker'" >&2
  exit 1
fi

run_test () {
  local protocol="$1"
  local payload="$2"

  echo "▶ $protocol $TARGET: $payload (constant-vus, $VUS vus)"
  $K6_CMD run \
    -e BASE_URL="$CLIENT_BASE" \
    -e PROTOCOL="$protocol" \
    -e PAYLOAD="$payload" \
    -e VUS="$VUS" \
    --summary-export="$RESULT_DIR/${protocol}_${TARGET}_${SERVICE_SOURCE}_${payload}_${VUS}vus_${TS}.json" \
    "$SCRIPT_DIR/performance-test.js"
}

############################################
# Main
############################################
echo "=========================================="
echo " Mode   : $MODE"
echo " Target : $TARGET"
echo " Test   : $TEST"
echo " Source : $SERVICE_SOURCE"
echo " VUS    : $VUS"
echo " Client : $CLIENT_BASE"
echo "=========================================="

if [ "$MODE" = "rest" ]; then
  if [ "$TEST" = "all" ]; then
    run_test rest small
    run_test rest medium
    run_test rest large
  else
    run_test rest "$TEST"
  fi

elif [ "$MODE" = "grpc" ]; then
  if [ "$TEST" = "all" ]; then
    run_test grpc small
    run_test grpc medium
    run_test grpc large
    run_test grpc large_compressed
    run_test grpc stream_large
  else
    run_test grpc "$TEST"
  fi

else
  echo "ERROR: Mode must be 'rest' or 'grpc'" >&2
  exit 1
fi

echo "✔ Done. Results in $OUT_DIR/"
