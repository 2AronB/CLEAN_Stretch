# Pose preview

Renders each catalog stretch to PNG: one row per camera view (the stretch's starting view first,
marked `*`), one column per keyframe (start / stretch / deep). It paints the shapes from
`figure/FigureDrawing.kt`, the same code the app draws with, so floor, mat, shadows, outlines,
props and view all match the app. Only the colors are fixed light-theme stand-ins. No Android SDK
or emulator needed, just a JDK and Gradle's bundled Kotlin compiler.

**Windows (the owner's PC)**, from the repo root:

```powershell
powershell -ExecutionPolicy Bypass -File tools\pose-preview\build-and-run.ps1            # every stretch
powershell -ExecutionPolicy Bypass -File tools\pose-preview\build-and-run.ps1 butterfly  # only these keys
```

It uses Android Studio's JDK and the newest Gradle in `~\.gradle\wrapper\dists` (set
`KOTLIN_LIB` to override). Java prints a few "deprecated" warnings while compiling; they're
harmless.

**Linux/macOS:**

```sh
KOTLIN_LIB=/path/to/gradle/lib tools/pose-preview/build-and-run.sh [key ...]
```

Images land in `tools/pose-preview/out/` (git-ignored).
