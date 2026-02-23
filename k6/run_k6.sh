#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-rest}"     # rest | grpc
TARGET="${2:-java}"   # java | node
TEST="${3:-all}"      # z.B. small | medium | large | stream_large | all

OUT_DIR="results"
mkdir -p "$OUT_DIR"

TS="$(date +%Y-%m-%d_%H-%M-%S)"

if ! command -v k6 >/dev/null 2>&1; then
  echo "ERROR: k6 not found. Install via 'brew install k6'" >&2
  exit 1
fi

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

run_rest () {
  local name="$1"
  local script="$2"

  echo "▶ REST $TARGET: $name (constant-arrival-rate, 100 rps)"
  k6 run \
    -e BASE_URL="$REST_BASE" \
    --summary-export="$OUT_DIR/rest_${TARGET}_${name}_100rps_${TS}.json" \
    "$script"
}

run_grpc () {
  local name="$1"
  local script="$2"

  echo "▶ gRPC $TARGET: $name (constant-arrival-rate, 100 rps)"
  k6 run \
    -e GRPC_ADDR="$GRPC_ADDR" \
    --summary-export="$OUT_DIR/grpc_${TARGET}_${name}_100rps_${TS}.json" \
    "$script"
}

run_rest_payload_class () {
  local payload_class="$1"

  run_rest "$payload_class" "k6/rest_${payload_class}.js"

  if [[ "$payload_class" =~ ^(small|medium)$ ]]; then
    run_rest "json_${payload_class}" "k6/rest_${payload_class}_structured.js"
  fi
}

run_grpc_payload_class () {
  local payload_class="$1"

  run_grpc "$payload_class" "k6/grpc_${payload_class}.js"

  if [[ "$payload_class" =~ ^(small|medium)$ ]]; then
    run_grpc "structured_${payload_class}" "k6/grpc_${payload_class}_structured.js"
  fi

  run_grpc "stream_${payload_class}" "k6/grpc_stream_${payload_class}.js"
}

############################################
# Main
############################################
echo "=========================================="
echo " Mode   : $MODE"
echo " Target : $TARGET"
echo " Test   : $TEST"
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
  elif [[ "$TEST" =~ ^(small|medium|large)$ ]]; then
    run_grpc_payload_class "$TEST"
  else
    run_grpc "$TEST" "k6/grpc_${TEST}.js"
  fi

else
  echo "ERROR: Mode must be 'rest' or 'grpc'" >&2
  exit 1
fi

echo "✔ Done. Results in $OUT_DIR/"
