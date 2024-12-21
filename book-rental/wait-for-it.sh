#!/usr/bin/env bash
# wait-for-it.sh: Wait for a service to become available

set -e

TIMEOUT=15
HOST=""
PORT=""
CMD=""
WAIT_START=$(date +%s)

usage() {
  echo "Usage: wait-for-it.sh <host:port> [--timeout=<seconds>] -- <command>"
  exit 1
}

for arg in "$@"; do
  case $arg in
    --timeout=*)
      TIMEOUT="${arg#*=}"
      shift
      ;;
    --)
      shift
      CMD=("$@")
      break
      ;;
    *)
      if [[ -z $HOST ]]; then
        HOST="${arg%:*}"
        PORT="${arg#*:}"
      else
        usage
      fi
      ;;
  esac
done

if [[ -z $HOST || -z $PORT || -z $CMD ]]; then
  usage
fi

echo "Waiting for $HOST:$PORT..."

while ! nc -z "$HOST" "$PORT"; do
  sleep 1
  if [[ $(( $(date +%s) - WAIT_START )) -ge $TIMEOUT ]]; then
    echo "Timeout reached! $HOST:$PORT is not available."
    exit 1
  fi
done

echo "$HOST:$PORT is available, starting command..."
exec "${CMD[@]}"