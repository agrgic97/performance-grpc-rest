#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-rest}"           # rest | grpc
TARGET="${2:-java}"         # java | node
TEST="${3:-all}"            # small | medium | large | stream_large | all
DEPLOYMENT="${4:-local}"    # local | docker (default: local)

OUT_DIR="results"
mkdir -p "$OUT_DIR"

RPS_LEVELS=(100 1000 5000 10000 50000 100000)
TEST_DURATION="${TEST_DURATION:-30s}"

TS="$(date +%Y-%m-%d_%H-%M-%S)"

if ! command -v k6 >/dev/null 2>&1; then
  echo "ERROR: k6 not found. Install via 'brew install k6'" >&2
  exit 1
fi

if [ "$DEPLOYMENT" = "docker" ]; then
  if [ "$TARGET" = "java" ]; then
    REST_BASE_DEFAULT="http://localhost:8081"
    GRPC_ADDR_DEFAULT="localhost:9091"
  elif [ "$TARGET" = "node" ]; then
    REST_BASE_DEFAULT="http://localhost:3001"
    GRPC_ADDR_DEFAULT="localhost:4001"
  else
    echo "ERROR: Unknown target '$TARGET'" >&2
    exit 1
  fi
elif [ "$DEPLOYMENT" = "local" ]; then
  if [ "$TARGET" = "java" ]; then
    REST_BASE_DEFAULT="http://localhost:8080"
    GRPC_ADDR_DEFAULT="localhost:9090"
  elif [ "$TARGET" = "node" ]; then
    REST_BASE_DEFAULT="http://localhost:3000"
    GRPC_ADDR_DEFAULT="localhost:4000"
  else
    echo "ERROR: Unknown target '$TARGET'" >&2
    exit 1
  fi
else
  echo "ERROR: Deployment must be 'docker' or 'local'" >&2
  exit 1
fi

REST_BASE="${REST_BASE:-$REST_BASE_DEFAULT}"
GRPC_ADDR="${GRPC_ADDR:-$GRPC_ADDR_DEFAULT}"

run_rest () {
  local name="$1"
  local script="$2"

  for rps in "${RPS_LEVELS[@]}"; do
    echo "▶ REST $TARGET/$DEPLOYMENT: $name (${rps} RPS, constant-arrival-rate, ${TEST_DURATION})"
    k6 run \
      -e BASE_URL="$REST_BASE" \
      -e RPS="$rps" \
      -e TEST_DURATION="$TEST_DURATION" \
      --summary-export="$OUT_DIR/rest_${TARGET}_${DEPLOYMENT}_${name}_${rps}rps_constant_${TS}.json" \
      "$script"
  done
}

run_grpc () {
  local name="$1"
  local script="$2"

  for rps in "${RPS_LEVELS[@]}"; do
    echo "▶ gRPC $TARGET/$DEPLOYMENT: $name (${rps} RPS, constant-arrival-rate, ${TEST_DURATION})"
    k6 run \
      -e GRPC_ADDR="$GRPC_ADDR" \
      -e RPS="$rps" \
      -e TEST_DURATION="$TEST_DURATION" \
      --summary-export="$OUT_DIR/grpc_${TARGET}_${DEPLOYMENT}_${name}_${rps}rps_constant_${TS}.json" \
      "$script"
  done
}

############################################
# Main
############################################
echo "=========================================="
echo " Mode       : $MODE"
echo " Target     : $TARGET"
echo " Deployment : $DEPLOYMENT"
echo " Test       : $TEST"
echo " RPS-Levels : ${RPS_LEVELS[*]}"
echo " Duration   : $TEST_DURATION"
echo " REST       : $REST_BASE"
echo " gRPC       : $GRPC_ADDR"
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
