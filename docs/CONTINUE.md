# Continue here

Phase: **implementation — Slices S0 + S1 first draft** (2026-11-08, D-020).

## Built
- S0 shell: Words-matched Gradle setup, `dev.clean.stretch`, display name Stretch,
  Material You theme with sage fallback, System/Light/Dark, wallpaper-colors switch,
  edge-to-edge, tabs Today / Stretches / Progress / Settings, saved back stack,
  Sources & credits, no INTERNET permission, auto-backup off.
- S1 figure: pure-Kotlin pose model, forward kinematics, floor grounding, curved spine,
  keyframe motion (Hold with breathing deepen, Flow), mirroring, muscle highlight with
  breath glow, reduced motion (system setting or "Still figures"), Pose Studio
  (sliders, mirror, play, copy as Kotlin).
- Six sample stretches with sources; Today hero card; library with region filters;
  detail screens with left/right toggle; hands-free player *preview* (D-023).

## Verified (in Claude's workspace)
- 15 JVM tests pass (figure math, motion timing, catalog integrity, Tier C wording
  scan) using Gradle 8's bundled Kotlin 2.0.21 compiler + JUnit 4.13.2.
- Every keyframe rendered to PNG with tools/pose-preview and checked by eye.

## Git
- Git for Windows 2.56.0.4 installed on the PC (winget). This folder is the repo;
  `main` tracks https://github.com/2AronB/CLEAN_Stretch (public). The GitHub repo was
  empty before 2026-11-08 — the first commit is this PC's folder. Commits use
  `2AronB <2AronB@users.noreply.github.com>` (repo-local) so no personal email is public.

## Verified on the owner's PC (2026-11-08, Claude Code)
- `local.properties` created (points at the default Android SDK; git-ignored).
- `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug` — BUILD SUCCESSFUL on the
  first try, no code changes needed. 15/15 tests pass, 0 Kotlin warnings, 0 lint errors.
  Lint warnings (not fixed): OldTargetApi 1, AndroidGradlePluginVersion 1,
  GradleDependency 2, UseKtx 3, ModifierParameter 1, UnusedResources 1.
- Ran on emulator `Pixel_8` (API 37), installed with `adb -s emulator-5554` only
  (the owner's phone was also connected over wireless adb and was not touched).
  No app crashes or ANRs in logcat.
- Screens that rendered correctly: Today (greeting, "Full-body reset" hero card with
  animated figure, routine grid), Stretches (6 entries, region filter chips), Progress
  (four 0-of-10-min rings, empty history text), Settings (theme System/Light/Dark,
  wallpaper colors, still figures, version 0.1.0-s1-figure), Dark theme, Sources &
  credits, Pose Studio (sliders, keyframe tabs, Play/Mirror/Reset/Copy), stretch
  detail with Left/Right toggle (figure mirrors), player preview (2 sides, countdown
  ring, breath cue "Breathe out… ease a little deeper.", +15 s).

## Issues seen on the emulator
Still open:
- Cold start shows a black screen for ~4–5 s (first frame 4.7 s, 262 frames skipped).
  Partly emulator slowness; worth checking on a real phone or adding a baseline profile.

Fixed the same day (owner: "Fix them"), rebuilt (15/15 tests, lint 0 errors) and
rechecked on the emulator:
- Player "Get into position" label covered the figure's head → figure now has 64 dp
  top room in the player (`PlayerScreen.kt`).
- Player always started on the right side → `Route.Player.firstSide` carries the side
  picked on the detail screen; that side plays first.
- Detail screen's side choice reset after the player → screens are now wrapped in a
  `SaveableStateHolder` (`StretchApp.kt`), so saved screen state survives while the
  screen is on the back stack and is dropped when it leaves.
- Setup footer named the side being set up → "Next:" now names the next stretch/side.
- Ingram card said 2024 but the citation said 2025 → citation now reads
  "2025;55:597–617 (published online 2024)", matching EVIDENCE.md S2 ("2024/2025").
  The short label stays 2024 so it doesn't clash with S26 "Ingram et al. 2025".

## Not yet verified
- Owner look-and-feel review on phone (the point of S1). First impression
  (2026-11-08): "animations are clean … some need polish, but clear enough for now."
- AGENTS.md still says "discovery, application code not authorized"; D-020 says
  implementation. Owner to confirm, then align AGENTS.md.

## Next
1. Check cold-start time on a real phone.
2. Owner reviews the figure and player feel on the phone.
3. Then S2 (real runner: voice, chimes, sessions, resume) — needs Room + VIBRATE approval.
