# CLEAN Stretch

An offline Android stretching app: science-informed routines that run hands-free,
drawn with a single animated figure. CLEAN means no ads, trackers, accounts,
subscriptions or paywalls, and no Internet permission.

**Status:** all first-release features exist (2026-11-09): personalised hands-free routines with
weekly dosing and safety limits, a 3D animated figure, 55 moves, a natural recorded voice,
reminders, progress and range-of-motion check-ins, backup/restore and delete. Play Store
preparation is in progress. See [docs/CONTINUE.md](docs/CONTINUE.md) for the current state and
[docs/HISTORY.md](docs/HISTORY.md) for the detailed build log.

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
