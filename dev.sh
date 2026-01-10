#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

AMAP_KEY_INPUT="${1:-${AMAP_KEY:-}}"
PORT="${2:-${SERVER_PORT:-8080}}"

if [[ -z "${AMAP_KEY_INPUT}" ]]; then
  echo "缺少高德 Key：用法 ./dev.sh <AMAP_KEY> [port]" >&2
  echo "或先设置环境变量：export AMAP_KEY=你的高德Key" >&2
  exit 1
fi

export AMAP_KEY="${AMAP_KEY_INPUT}"
exec mvn -q spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT}"

