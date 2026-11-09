# CLEAN Stretch

An offline Android stretching app: science-informed routines that run hands-free,
drawn with a single animated figure. CLEAN means no ads, trackers, accounts,
subscriptions or paywalls, and no Internet permission.

**Status:** first draft (Slices S0 + S1, 2026-11-08). App shell, Material You theme,
stretch library (28 stretches with easier/harder versions, 6 strengthen holds, 3 foam-rolling moves), animated figure engine, Pose Studio and a
hands-free player with voice, chimes and vibration. Finished sessions are saved on the
device (Room), unfinished ones can be resumed, and Progress shows this week's minutes. See
[docs/CONTINUE.md](docs/CONTINUE.md) for exactly what is built and verified.

## Run

Open this folder in Android Studio and run the `app` configuration. Minimum Android 8
(API 26), compile/target API 36. Versions match CLEAN Words: Gradle 9.3.1, Android Gradle
Plugin 9.1.1 (built-in Kotlin), Compose plugin 2.2.10, Compose BOM 2026.02.01.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

## Figure previews without an emulator

`tools/pose-preview/build-and-run.sh` compiles the pure-Kotlin figure and catalog code
and renders every stretch's keyframes to PNG. Use it, or **Settings → Pose Studio** on a
phone, when adding or tuning stretches.

## Where things are

| Area | Files |
|---|---|
| Figure math (pure Kotlin, unit-tested) | `app/src/main/java/dev/clean/stretch/figure/` |
| Stretch catalog and sources | `.../catalog/` |
| Compose UI, figure drawing | `.../ui/`, `.../ui/figure/FigureView.kt` |
| Product, decisions, evidence, plan | `docs/` |

Health wording in the app must follow the claims tiers in
[docs/EVIDENCE.md](docs/EVIDENCE.md) §11.
