#!/usr/bin/env bash

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_JAR="$ROOT_DIR/backend/target/backend-0.0.1-SNAPSHOT.jar"

if [ ! -f "$BACKEND_JAR" ]; then
  echo "Backend JAR not found. Run ./build.sh first."
  exit 1
fi

if [ ! -d "$ROOT_DIR/frontend/dist" ]; then
  echo "Frontend build not found. Run ./build.sh first."
  exit 1
fi

cleanup() {
  echo
  echo "Stopping CRS..."
  kill "$BACKEND_PID" 2>/dev/null || true
}

trap cleanup EXIT INT TERM

echo "Starting backend..."
(
  cd "$ROOT_DIR/backend"
  java -jar "$BACKEND_JAR"
) &

BACKEND_PID=$!

echo "Starting frontend..."
(
  cd "$ROOT_DIR/frontend"
  npm run preview -- --host localhost --port 5173
)