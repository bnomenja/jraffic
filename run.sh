#!/usr/bin/env bash

set -euo pipefail

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$PROJECT_DIR/build"
JAVAFX_HOME="${JAVAFX_HOME:-/home/spites/tools/javafx-sdk-21.0.12}"
JAVAFX_LIB="$JAVAFX_HOME/lib"

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    echo "Error: Java and javac must be installed and available in PATH." >&2
    exit 1
fi

if [[ ! -f "$JAVAFX_LIB/javafx.controls.jar" ]]; then
    echo "Error: JavaFX was not found at: $JAVAFX_HOME" >&2
    echo "Set JAVAFX_HOME to the directory containing the JavaFX lib folder." >&2
    exit 1
fi

sources=()
while IFS= read -r -d '' source_file; do
    sources+=("$source_file")
done < <(find "$PROJECT_DIR" -type f -name '*.java' -print0)

if (( ${#sources[@]} == 0 )); then
    echo "Error: no Java source files were found." >&2
    exit 1
fi

mkdir -p "$BUILD_DIR"

echo "Compiling ${#sources[@]} Java source files..."
javac \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls \
    -d "$BUILD_DIR" \
    "${sources[@]}"

echo "Starting Jraffic..."
exec java \
    -Dprism.dirtyopts=false \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls \
    -cp "$BUILD_DIR" \
    Main
