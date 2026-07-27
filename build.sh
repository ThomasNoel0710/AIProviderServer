#!/usr/bin/env bash

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Building backend..."
(
  cd "$ROOT_DIR/backend"
  ./mvnw package
)

echo "Building frontend..."
(
  cd "$ROOT_DIR/frontend"
  npm run build
)

echo "Build completed."