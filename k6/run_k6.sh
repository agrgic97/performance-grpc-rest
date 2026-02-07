#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-rest}"     # rest | grpc
TARGET="${2:-java}"   # java | node

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
  GRPC_ADDR="localhost:3001"
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
echo "=========================================="

if [ "$MODE" = "rest" ]; then
  run_rest small  "k6/rest_small.js"
  run_rest medium "k6/rest_medium.js"
  run_rest large  "k6/rest_large.js"
elif [ "$MODE" = "grpc" ]; then
  run_grpc small  "k6/grpc_small.js"
  run_grpc medium "k6/grpc_medium.js"
  run_grpc large  "k6/grpc_large.js"
else
  echo "ERROR: Mode must be 'rest' or 'grpc'" >&2
  exit 1
fi

echo "✔ Done. Results in $OUT_DIR/"
