# Continue here

Phase: **implementation — S0 + S1 done, S2 runner first version** (2026-11-08, D-020, D-025).
Version 0.2.0-s2-runner.

## S2 runner (2026-11-08, D-025) — what exists
- Room database `stretch.db` v1 (`data/History.kt`): `sessions` + `session_steps`, insert-only
  immutable snapshots (name, side, primary regions, muscles, planned/actual seconds, skipped).
  Schema exported to `app/schemas/`. No destructive migration fallback.
- Player records each hold, saves on finish ("completed") or End/Back ("ended early"), writes
  a checkpoint (SharedPreferences "runner") at every step. Today shows **Unfinished routine →
  Resume / Save what I did**; starting a new routine auto-saves an older unfinished one.
- Cues (`ui/Cues.kt`): built-in TextToSpeech (step name/side, "Hold for 40 seconds",
  "Next: …" at 6 s left, "Routine complete"), 3-2-1 ticks + end chime (ToneGenerator),
  vibration between steps. Settings → "During a routine" switches for each.
- Progress: six area rings (minutes held this week, Monday start, recomputed from rows) +
  recent history list. Today's week strip ticks days with a session.
- Verified on emulator: neck stretch ran to completion and saved; quad run killed with
  force-stop mid-routine → Unfinished card → Resume continued on the left side → End saved
  "ended early"; Progress showed both. 19/19 JVM tests (new HistoryTest), lint 0 errors.
- **Not verified:** how voice and chimes sound (emulator audio not heard by Claude); behavior
  on a real phone.
- Gaps vs PLAN §3.1: timer is still frame-based, not an `elapsedRealtime` state machine (a
  long pause in the background isn't counted, which is fine, but a screen-off run stops);
  no foreground service; no JVM state-machine tests for pause/skip/back; save errors show on
  Today only after navigating there (`SessionSaver.lastError` is not observable state).

## Built
- S0 shell: Words-matched Gradle setup, `dev.clean.stretch`, display name Stretch,
  Material You theme with sage fallback, System/Light/Dark, wallpaper-colors switch,
  edge-to-edge, tabs Today / Stretches / Progress / Settings, saved back stack,
  Sources & credits, no INTERNET permission, auto-backup off.
- S1 figure: pure-Kotlin pose model, forward kinematics, floor grounding, curved spine,
  keyframe motion (Hold with breathing deepen, Flow), mirroring, muscle highlight with
  breath glow, reduced motion (system setting or "Still figures"), Pose Studio
  (sliders, mirror, play, copy as Kotlin).
- 16 stretches with sources (6 from S1, 10 added in round 2 — see below); Today hero card; library with region filters;
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

## Round 2 (2026-11-08, D-024): 10 more stretches — 16 total
- Added: Chin-to-chest neck, Behind-the-back chest opener, Wall lat (wall), Knee-to-chest
  (lying, each side), Butterfly, Bent-knee calf (wall, each side), Standing hamstring hinge
  (each side), Sphinx (core), Lying hamstring (each side), Downward dog (level 2).
- Today's "Full-body reset" stays the original six (`SampleCatalog.fullBodyReset`).
- Verified: build, 15/15 tests (floor contact, on-screen, sources, Tier C wording now cover all
  16), lint 0 errors; every new thumbnail checked by eye on the emulator; lying hamstring
  played in the player; no crashes.
- Weakest pose: Butterfly (side view hides the feet). Owner review wanted on all new
  wording and poses.

## Not yet verified
- Owner look-and-feel review on phone (the point of S1). First impression
  (2026-11-08): "animations are clean … some need polish, but clear enough for now."
- AGENTS.md still says "discovery, application code not authorized"; D-020 says
  implementation. Owner to confirm, then align AGENTS.md.

## Next
1. Owner runs a 5-minute routine hands-free (PLAN S2 acceptance): voice, chimes, vibration.
2. Check cold-start time on a real phone.
3. S2 hardening: elapsedRealtime state machine + JVM tests (pause, skip, back, +15 s,
   kill/resume); make save errors observable.
4. Owner decision: AGENTS.md still says "discovery, no app code" — align it with D-020.

## Owner feedback from phone test (2026-11-08)
- **Voice:** works, but the owner finds Android's TTS voice too unnatural. Under discussion
  (natural offline voice options) — don't change until decided.
- **Screen off (future todo, not now):** timer keeps counting with the screen off, but the next
  step doesn't start. Decision for now: the runner is **screen-on only**. Revisit with a
  foreground service + elapsedRealtime state machine (D-010).
- **Transition time:** 5 s setup is too short for some moves (e.g. standing up from lying down
  needs 10–15 s). Hold off fixing until the voice is decided; options to discuss: setup time
  based on the position change between stretches, a Short/Normal/Long setting, and an
  "I'm ready" tap to start early.

## Voice clips — next session (D-026, voice chosen: Kokoro af_heart)
- Tools (outside the repo): `C:\Users\Kayli\Desktop\CLEAN Development\kokoro` — Python venv
  with kokoro-onnx 0.4.7 + model files `kokoro-v1.0.onnx` / `voices-v1.0.bin` (from the
  kokoro-onnx GitHub release). Samples live in git-ignored `voice-samples/`.
- To do: a repo script (e.g. `tools/voice/`) that lists every phrase the app speaks (16 stretch
  names, left/right side, "Get into position", "Hold for N seconds" for each hold length,
  "Next: …", 3-2-1, "Routine complete"), generates them with af_heart at speed 0.95, converts
  to small OGG/Opus in `app/src/main/res/raw/`, and a JVM test that fails if a catalog stretch
  has no clip. `Cues.say` becomes a clip queue (MediaPlayer/SoundPool, built in), falling back
  to system TTS only for a missing clip. Credit Kokoro (Apache-2.0) on Sources & credits.
- Then: longer/position-aware setup time + "I'm ready" tap (see owner feedback above).

## Voice clips done (2026-11-08, D-026)
- 48 af_heart clips (1.1 MB Ogg Vorbis) in `app/src/main/res/raw/voice_*.ogg`, kept for release
  builds by `res/raw/keep.xml`. Lines come from `ui/VoiceLines.kt`; `tools/voice/phrases.txt` +
  `tools/voice/generate.py` regenerate them (see the script header). `VoiceLinesTest` fails if a
  stretch is added/renamed without updating phrases.txt and clips. Kokoro credited on Sources.
- Checked: 22/22 tests, lint 0 errors; emulator played clips (no "missing clip" fallback).
  Owner still to listen on the phone.

## Setup time done (2026-11-08, owner feedback)
- Setup now depends on the move (`buildSteps` in PlayerScreen.kt): side switch 5 s, first
  stretch 10 s, new stretch in the same position 10 s, position change (e.g. floor → standing)
  15 s. "I'm ready" button during setup starts the hold early. `SetupTimeTest` covers it.
- Checked: 23/23 tests, lint 0 errors; emulator: "I'm ready" started the hold. Owner to judge
  the timings on the phone. Possible later: a Short/Normal/Long setting.

## S4 setup + S5 generator, first version (2026-11-08)
- **Setup** (`ui/SetupScreen.kt`): goal (General/Runner/Lifter/Desk), 65+ (60 s holds), focus
  areas, areas to avoid, no floor / no kneeling / wall, health conditions (yes → doctor advice
  dialog → "I understand" → limits on, D-029), usual length 5/10/15. Today shows a "Make it
  yours" card until done; Settings → "Goal, focus and limits" reopens it.
- **Profile** stored in Room (`data/ProfileStore.kt`), DB v2 via AutoMigration (schema 2.json).
  Verified on emulator: upgrade kept all existing history.
- **Generator** (`plan/Generator.kt`, pure Kotlin): kinds Full / Warm-up (30 s, flowing moves
  first) / Cool-down / Quick break (standing, ≤4 min); focus areas first, least-stretched this
  week first; safety filters for avoid areas, floor, kneeling, wall, pregnancy (no lying, level
  1), osteoporosis (no `spinalFlexion`), hypermobility (level 1); notes explain gaps.
  `GeneratorTest` (8 tests).
- **Review screen** (`ui/RoutinePreviewScreen.kt`): reorder, swap (same area, allowed only),
  remove, shuffle, length; Start replaces the preview with the player.
- Voice clips for 30 s and 60 s holds added (50 clips).
- Checked: 31/31 tests, lint 0 errors; emulator walk-through of setup, dialog, Today, preview,
  player. Owner to review wording of conditions and default limits.
- Gap seen: with "no floor" + "avoid legs" only 2 stretches qualify → S3 needs more standing
  and chair stretches. Saving routines and the if-then plan / reminder ask are not built yet.
