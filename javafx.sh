#!/bin/bash
set -e

FX_VERSION="21.0.12"
FX_DIR="javafx-sdk-${FX_VERSION}"
FX_ZIP="openjfx-${FX_VERSION}_linux-x64_bin-sdk.zip"
FX_URL="https://download2.gluonhq.com/openjfx/${FX_VERSION}/${FX_ZIP}"
PATH_TO_FX="${FX_DIR}/lib"

cmd_install() {
    if [ -d "$FX_DIR" ]; then
        echo "Folder $FX_DIR already exists, nothing to do."
        return 0
    fi

    echo "Downloading JavaFX ${FX_VERSION} (linux x64)..."
    curl -fL -o "$FX_ZIP" "$FX_URL"

    echo "Extracting into the current folder..."
    unzip -q "$FX_ZIP"

    rm -f "$FX_ZIP"

    echo "JavaFX ${FX_VERSION} installed in ./${FX_DIR}"
}

cmd_run() {
    if [ ! -d "$PATH_TO_FX" ]; then
        echo "JavaFX not found in $PATH_TO_FX. Run '$0 install' first."
        exit 1
    fi

    javac --module-path "$PATH_TO_FX" --add-modules javafx.controls -d out $(find . -name "*.java")
    java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out Main
}

case "$1" in
    install)
        cmd_install
        ;;
    run)
        cmd_run
        ;;
    *)
        echo "Usage: $0 {install|run}"
        exit 1
        ;;
esac