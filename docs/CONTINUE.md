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

## S3 round 3 (2026-11-08): 5 standing stretches — 21 total
- Standing upper-back stretch (spinalFlexion), Standing lunge hip flexor (each side), Overhead
  reach, Standing back bend (core), Standing knee hug (each side). Poses checked by eye on the
  emulator (back-bend arms fixed). 64 voice clips. 31/31 tests, lint 0 errors.
- Still to do for S3: ~9 more stretches, easier/harder variants (EVIDENCE §5.4), chair
  versions (needs a chair prop in FigureView), wrists/forearms for Desk.

## S6 reminders + week streaks, first version (2026-11-08)
- Setup page 6 "When will you stretch?": if-then cue (P-7), days per week (streak target, 2–7),
  daily reminder + time. Stored in AppPreferences. Settings → "Goal, focus and limits" edits it.
- Reminder (`settings/Reminders.kt`): inexact daily alarm (no exact-alarm permission) →
  notification "Time to stretch — Your plan: stretch after you brush your teeth…". POST_NOTIFICATIONS
  asked only when the user turns the reminder on (R-007); if denied, a plain note explains and
  everything else works. Re-armed on every app launch, because RECEIVE_BOOT_COMPLETED is **not
  approved** — after a phone restart the reminder resumes only once the app is opened.
- Week streak (`Stats.weekStreak`, test added): weeks meeting the days target; this week only
  counts once met, so nothing "breaks". Shown on Progress (top card) and Today's week card.
- Checked: 32/32 tests, lint 0 errors/code warnings; emulator: deny path shows the note; allow
  path → alarm registered for 7:00 PM (dumpsys alarm). **Not verified:** the notification
  actually appearing (can't trigger the private receiver from the PC); owner to check on phone.

## S7 backup/restore, first version (2026-11-08) — all first-release slices now exist
- Settings → "Your data": **Back up to a file** (Android file picker, no permission) writes
  versioned JSON (`data/Backup.kt`, format `clean-stretch-backup` v1): sessions + steps,
  profile, settings. **Restore from a backup** reads and fully validates first; newer-version,
  damaged or foreign files are refused with "Your current data is unchanged". A confirm dialog
  ("Replace your data?") precedes replacement; history + profile replace in one Room
  transaction (`BackupManager`). Checkpoint isn't included. Own tiny JSON codec (`data/Json.kt`),
  no new library. `BackupTest` (3 tests).
- Checked: 35/35 tests, lint clean; emulator: backup saved to Downloads (2.3 kB JSON), restore
  picked it, dialog showed "5 sessions…", "Restored 5 sessions.", history intact.
- Version 0.3.0 (versionCode 3).

## Saved routines (2026-11-08)
- Review screen → **Save** (name dialog) stores the edited routine (`data/SavedRoutines.kt`,
  DB v3 via AutoMigration; schema 3.json). Today → "Your routines": Open (goes to review) and
  Delete (confirm; history untouched). Included in backups as an optional `routines` list, so
  older v1 backup files still restore.
- Checked: 35/35 tests (backup round-trip includes a routine), lint clean; emulator: v2→v3
  upgrade kept data, save → listed on Today, delete → confirm → removed, no crashes.

## S3 round 4 (2026-11-08): variants + 7 stretches — 28 total
- New: Side-lying quad, Kneeling quad (level 2), Seated towel calf (strap), Wall chest (wall
  behind), Cobra (level 2), Cross-body shoulder, Puppy. Poses checked on the emulator
  (side-lying quad leg angles fixed).
- Easier → harder pairs (`SampleCatalog.variantPairs`, EVIDENCE §5.4): hamstrings lying →
  standing → seated fold → downward dog; quads side-lying → standing → kneeling; hip flexors
  standing lunge → half-kneeling; chest opener → wall chest; overhead reach → wall lat → puppy;
  standing back bend → sphinx → cobra; towel calf → wall calf. CatalogTest checks pairs
  resolve, share a main area and never go down in level.
- Detail screen shows "Other versions" links; review screen offers Easier/Harder per stretch
  (only versions the user's limits allow and not already in the routine).
- 86 voice clips (2.0 MB). Checked: 36/36 tests, lint clean, detail links on emulator.
  **Not checked on emulator:** the Easier/Harder buttons on the review screen (the UI
  automation was unreliable this round); owner to try.
- Still open for S3: adductor and neck variants, chair versions (needs a chair prop),
  wrists/forearms for Desk.

## Pose fix (2026-11-08, owner feedback)
- Side-lying quad looped the knee past 180°. Rebuilt as the standing quad laid down (thigh
  pivots back, knee 158–160°, heel to buttock, hand reaches back to the ankle); checked frame by
  frame on the emulator. New `CatalogTest.jointsStayInARealRange` (knees 0–170°, elbows
  0–160°) caught the butterfly at 175° too — lowered to 168° with knees raised.

## S9 range-of-motion checks, first version (2026-11-08)
- Four self-checks (`plan/RomTests.kt`): toe reach, shoulder reach (each side), knee to wall
  (each side), deep squat. Answers are levels (no tape measure). Skipped checks save nothing,
  so "not measured" never looks like a low score. Wording: "for tracking your own progress,
  not medical tests" (Editorial); stop-at-mild-tension cue.
- Stored insert-only in Room v4 (`rom_checks`, AutoMigration 3→4, schema 4.json); included in
  backups (optional `romChecks`, older files still restore).
- Progress → "Range of motion" card: latest answer per check/side, "+N steps since <date>",
  "Not measured yet", re-check suggestion after 28 days; "Check now / Check again" opens the
  check-in screen. `RomChecksTest` (trend math, never-measured is absent).
- Checked: 39/39 tests, lint clean; emulator: first check saved "Mid-shin", second "Ankles" →
  "+1 since 8 Nov" (now reads "+1 step"), no crashes. Owner to review check wording/levels.

## S8 Strengthen + foam rolling, first version (2026-11-08)
- New exercise kinds (`StretchKind.Strengthen`, `StretchKind.Roll`; `isStretch` for stretches).
- **Strengthen (P-5):** timed 30 s holds, no rep counting: chin tuck [S19], wall angel (wall)
  [S19][S9], active leg raise (each side) [S9], glute bridge [S9][S30], forearm plank (level 2),
  calf raise hold [S9][S10]. Block of 1–2 moves at the end of Full, Cool-down and Quick
  routines when on. Profile `strengthen` = automatic (on for Desk or hypermobility) unless the
  user flips the Setup switch.
- **Foam rolling (P-10):** roll upper back, calves, front of thighs. Only if "I have a foam
  roller" (Setup). One area (60 s, Flow ≈ 2.6 s per pass) opens warm-ups and cool-downs.
  Osteoporosis leaves out rolling on the spine. Cautions per [S44] + Editorial; pressure cue
  "firm where you can still breathe slowly". Roller drawn as a circle (`ROLLER_RADIUS`).
- Strengthen/rolling time counts toward minutes and streaks, **not** per-area stretch rings
  (recorded with empty primaryRegions).
- Sources added: S9, S10, S19, S30, S34, S36, S44 (text taken from EVIDENCE.md).
- Profile DB v5 (AutoMigration 4→5, new columns with defaults); backups carry the new fields
  (optional on read). GeneratorTest +2 (roller gating, osteoporosis, strengthen switch and
  placement, still fits the budget).
- Poses checked with tools/pose-preview (now runnable on Windows — see below) and on the
  emulator (rollers, Today routine ends with the Strengthen block for a Desk profile).
- Not built yet: the "Tender spot" flow, massage ball / roller stick, rolling pass ticks/voice
  per pass, an Advanced toggle to count rolling toward rings.
- pose-preview on Windows: compile with Gradle's bundled Kotlin
  (`~/.gradle/wrapper/dists/gradle-9.3.1-bin/*/gradle-9.3.1/lib`), classpath separator `;`,
  run `PosePreviewKt` from PowerShell (Git Bash mangles the classpath). Props draw as lines there.

## Round of owner feedback (2026-11-08 evening) — built, tested
- Today card (D-032): stretch tiles inside the card, **Start** (straight to the player) and
  **Edit** (review screen). Separate "In this routine" grid removed.
- Player: "I'm ready" / "+15 s" / "Next" moved above the controls, clear of the gesture bar.
- Rolling: `figure/Ik.kt` two-bone IK; `Motion.pins` keeps hands (calf, thigh rolls) or feet
  (upper-back roll) planted while the body slides; arms pivot at the shoulders.
  `CatalogTest.pinnedHandsAndFeetStayPutWhileRolling` samples every 250 ms. Frame-by-frame
  emulator capture of the calf roll was interrupted — owner to eyeball.
- ROM checks (D-033): picture choices drawn with the figure. Toe reach (7, hanging arms),
  overhead reach (4), ankle lunge (4 per side), straight-leg raise (5 per side). Retired
  shoulder_reach / knee_to_wall / deep_squat (saved rows kept, not shown).
- Reminder: one-shot `setWindow` alarm (10-min window) that re-arms itself daily and on every
  reminder-setting change. Emulator: set 20:56, fired 20:59:58, next day armed.
- Decisions D-031 (conditions: final review before Play), D-032, D-033.

## Next batch (owner-requested, not built yet)
- Reminder wording: "around 7:00 PM" where **around** links to a short explanation (we keep
  permissions minimal, so Android may deliver it up to ~10 minutes late). Owner is fine with
  the delay once explained (no exact-alarm permission).
