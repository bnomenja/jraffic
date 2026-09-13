#!/bin/bash
PATH_TO_FX="javafx-sdk-21.0.12/lib"

javac --module-path "$PATH_TO_FX" --add-modules javafx.controls -d out $(find . -name "*.java")
java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out Main