#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-rest}"     # rest | grpc
TARGET="${2:-java}"   # java | node
TEST="${3:-all}"      # z.B. small | medium | large | stream_large | all
SERVICE_SOURCE="${4:-local}"  # local | docker

OUT_DIR="k6/results"
mkdir -p "$OUT_DIR"

TS="$(date +%Y-%m-%d_%H-%M-%S)"

if ! command -v k6 >/dev/null 2>&1; then
  echo "ERROR: k6 not found. Install via 'brew install k6'" >&2
  exit 1
fi

if [ "$SERVICE_SOURCE" = "local" ]; then
  if [ "$TARGET" = "java" ]; then
    REST_BASE="http://localhost:8080"
    GRPC_ADDR="localhost:9090"
  elif [ "$TARGET" = "node" ]; then
    REST_BASE="http://localhost:3000"
    GRPC_ADDR="localhost:4000"
  else
    echo "ERROR: Unknown target '$TARGET'" >&2
    exit 1
  fi
elif [ "$SERVICE_SOURCE" = "docker" ]; then
  if [ "$TARGET" = "java" ]; then
    REST_BASE="http://localhost:8081"
    GRPC_ADDR="localhost:9091"
  elif [ "$TARGET" = "node" ]; then
    REST_BASE="http://localhost:3001"
    GRPC_ADDR="localhost:4001"
  else
    echo "ERROR: Unknown target '$TARGET'" >&2
    exit 1
  fi
else
  echo "ERROR: Service source must be 'local' or 'docker'" >&2
  exit 1
fi

run_rest () {
  local name="$1"
  local script="$2"

  echo "▶ REST $TARGET: $name (constant-arrival-rate, 100 rps)"
  k6 run \
    -e BASE_URL="$REST_BASE" \
    --summary-export="$OUT_DIR/rest_${TARGET}_${SERVICE_SOURCE}_${name}_${TS}.json" \
    "$script"
}

run_grpc () {
  local name="$1"
  local script="$2"

  echo "▶ gRPC $TARGET: $name (constant-arrival-rate, 100 rps)"
  k6 run \
    -e BASE_URL="$GRPC_ADDR" \
    --summary-export="$OUT_DIR/grpc_${TARGET}_${SERVICE_SOURCE}_${name}_${TS}.json" \
    "$script"
}

run_rest_payload_class () {
  local payload_class="$1"

  run_rest "$payload_class" "k6/rest_${payload_class}.js"
}

run_grpc_payload_class () {
  local payload_class="$1"

  run_grpc "$payload_class" "k6/grpc_${payload_class}.js"
}

############################################
# Main
############################################
echo "=========================================="
echo " Mode   : $MODE"
echo " Target : $TARGET"
echo " Test   : $TEST"
echo " Source : $SERVICE_SOURCE"
echo " REST   : $REST_BASE"
echo " gRPC   : $GRPC_ADDR"
echo "=========================================="

if [ "$MODE" = "rest" ]; then
  if [ "$TEST" = "all" ]; then
    run_rest_payload_class small
    run_rest_payload_class medium
    run_rest_payload_class large
  elif [[ "$TEST" =~ ^(small|medium|large)$ ]]; then
    run_rest_payload_class "$TEST"
  else
    run_rest "$TEST" "k6/rest_${TEST}.js"
  fi

elif [ "$MODE" = "grpc" ]; then
  if [ "$TEST" = "all" ]; then
    run_grpc_payload_class small
    run_grpc_payload_class medium
    run_grpc_payload_class large
    run_grpc_payload_class large_gzip
    run_grpc stream_large "k6/grpc_stream_large.js"
  elif [[ "$TEST" =~ ^(small|medium|large|large_compressed|stream_large)$ ]]; then
    run_grpc "$TEST" "k6/grpc_${TEST}.js"
  else
    run_grpc "$TEST" "k6/grpc_${TEST}.js"
  fi

else
  echo "ERROR: Mode must be 'rest' or 'grpc'" >&2
  exit 1
fi

echo "✔ Done. Results in $OUT_DIR/"
