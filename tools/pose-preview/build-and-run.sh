#!/bin/sh
# Compiles the pure-Kotlin figure + catalog sources with any kotlinc-compatible compiler jar and renders previews.
# Usage: KOTLIN_LIB=/path/to/dir/with/kotlin-compiler-embeddable+stdlib tools/pose-preview/build-and-run.sh
set -e
L="${KOTLIN_LIB:-/opt/gradle-8.14.3/lib}"
CP=$(ls $L/kotlin-compiler-embeddable-*.jar $L/kotlin-stdlib-2*.jar $L/kotlin-script-runtime-*.jar $L/kotlin-reflect-*.jar $L/kotlin-daemon-embeddable-*.jar $L/trove4j*.jar $L/annotations-*.jar $L/kotlinx-coroutines-core-jvm-*.jar | tr '\n' ':')
STD=$(ls $L/kotlin-stdlib-2*.jar | head -1)
SRC=app/src/main/java/dev/clean/stretch
OUT=tools/pose-preview/out
java -cp "$CP" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -cp "$STD" -nowarn \
  $SRC/figure/*.kt $SRC/catalog/*.kt tools/pose-preview/PosePreview.kt -d $OUT/classes
java -Djava.awt.headless=true -cp "$OUT/classes:$STD" PosePreviewKt $OUT "$@"
