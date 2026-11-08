# Pose preview

Renders each catalog stretch (start / stretch / deep keyframes) to PNG with the app's own
figure math, using the same draw order and limb outlines as `FigureView`. No Android SDK
needed — just a JDK and a Kotlin compiler jar plus the Kotlin stdlib.

```sh
KOTLIN_LIB=/path/to/kotlin/lib tools/pose-preview/build-and-run.sh
# images land in tools/pose-preview/out/ (git-ignored)
```

The default `KOTLIN_LIB` points at a Gradle 8 install's `lib` folder, which bundles both.
