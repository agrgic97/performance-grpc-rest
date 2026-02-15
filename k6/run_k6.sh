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

  echo "▶ REST $TARGET: $name"
  k6 run \
    -e BASE_URL="$REST_BASE" \
    --summary-export="$OUT_DIR/rest_${TARGET}_${name}_${TS}.json" \
    "$script"
}

run_grpc () {
  local name="$1"
  local script="$2"

  echo "▶ gRPC $TARGET: $name"
  k6 run \
    -e GRPC_ADDR="$GRPC_ADDR" \
    --summary-export="$OUT_DIR/grpc_${TARGET}_${name}_${TS}.json" \
    "$script"
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
    run_rest small  "k6/rest_small.js"
    run_rest medium "k6/rest_medium.js"
    run_rest large  "k6/rest_large.js"
  else
    run_rest "$TEST" "k6/rest_${TEST}.js"
  fi

elif [ "$MODE" = "grpc" ]; then
  if [ "$TEST" = "all" ]; then
    run_grpc small         "k6/grpc_small.js"
    run_grpc medium        "k6/grpc_medium.js"
    run_grpc large         "k6/grpc_large.js"
    run_grpc stream_small  "k6/grpc_stream_small.js"
    run_grpc stream_medium "k6/grpc_stream_medium.js"
    run_grpc stream_large  "k6/grpc_stream_large.js"
  else
    run_grpc "$TEST" "k6/grpc_${TEST}.js"
  fi

else
  echo "ERROR: Mode must be 'rest' or 'grpc'" >&2
  exit 1
fi

echo "✔ Done. Results in $OUT_DIR/"
