# Build history (detailed log)

Every round of work as it happened, 2026-11-08 to 2026-11-09, moved here from CONTINUE.md
unchanged so CONTINUE.md can describe the current state. Newest entries are at the bottom.
Earlier sections describe things that were later changed (for example the S1 "preview"
player, system TTS voice and 2D figure).


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

## Where next (recommendation, 2026-11-08)
1. **Generator v2 — "right on generation" (D-032).** Weekly dose per focus area (P-1: ~10 min
   per area per week, ≤4 min per area per session, spread over planned days — PLAN acceptance
   "Weekly target"); day-to-day variety (no identical routine every day); proper warm-ups with
   ~6 new dynamic moves (catalog has only cat–cow); keep the current position flow. Plus the
   "around" reminder wording.
2. **Content pass toward ~45.** Fill gaps (neck variants, inner thigh, wrists/forearms for Desk,
   chair versions with a chair prop), owner wording review of the catalog.
3. **Play readiness track (D-028).** Release build with shrinking + signing key, accessibility
   pass (TalkBack labels, large text), privacy policy + Data safety ("no data collected"),
   health-app declaration, store listing/icon, dependency/SDK updates, condition review (D-031),
   cold start on a real phone, PLAN acceptance walk-through on the phone.
4. **Later:** "Tender spot" flow, screen-off running (foreground service), sturdier timer.

## Generator v2 (2026-11-09) — "right on generation" (D-032)
- **Weekly dose (P-1):** each focus area (or every area if none chosen) gets today's share of
  ~10 min/week = (10 − minutes done this week) ÷ planned days still to come, capped at 4 min;
  neediest area first. Leftover time fills from the least-covered areas; no area ever passes
  4 min in a session. A met weekly goal shifts time elsewhere with a note.
  Region-level for now (e.g. "Legs" covers hamstrings, quads and calves).
- **Variety:** suggestion seeded by date (+ routine kind) and moves used often this week go to
  the back, so it changes day to day while keeping the standing → floor order.
- **Warm-ups:** 6 new flowing moves (leg swings, arm swings, hip hinge, ankle rocks, lunge with
  reach, slow neck nods) [S3][S4][S5]; warm-ups pick flowing moves first, full routines and
  cool-downs pick holds first. Voice "Keep moving for N seconds." for flowing moves.
- **Reminder wording:** "A gentle nudge at *around* 7:00 PM" — "around" opens "Why around?"
  (minimal permissions; Android may deliver up to ~10 minutes late).
- Tests: PLAN "Weekly target" acceptance (≈2 min on day 1 of 5; (10−4)/3 = 2 min on day 3;
  cap 4), cap across all kinds/lengths, met-goal shift, day-to-day variety, warm-up mix.
- Today passes week minutes, planned days left, week move counts and the date seed. The review
  screen's Shuffle/length still regenerate without week data (fine for now).
- Generator v2 **not yet seen on the emulator**: the PC ran out of memory (two emulators +
  builds; 0.5 GB free), so the check was stopped and the Pixel_8 emulator shut down. Verified by
  46/46 JVM tests, lint clean, and pose-preview images of the 6 new moves. Owner to try on phone.
- Tip: run one emulator at a time on this 16 GB PC; close it after testing.

## Settings → Goal, focus and limits (2026-11-09, owner request)
- After first setup it opens a **menu of the six sections** (Goal, Focus areas, Limits and
  equipment, Health conditions, Routine length, Schedule and reminder) with a one-line summary
  of each; tap one to change it, "Done" returns to the menu, "Save and close" (or Back) saves.
  First-run setup still walks through all six pages.
- The Settings entry is now a highlighted card (primary container colour).
- Checked: build, 46/46 tests, lint clean. **Not seen on the emulator** (kept off to save RAM);
  owner to check on phone.

## Library search and filters (2026-11-09, owner request)
- `plan/LibraryFilter.kt` (pure Kotlin, `LibraryFilterTest` 6 tests): search + filter groups
  — type (Holds, Flowing, Strengthen, Foam rolling), body area, muscle, position, equipment,
  level (Easier/Harder), "Fits my limits" (uses the Setup profile's safety rules). OR within a
  group, AND between groups. Results easiest first; moves that *mainly* target a chosen muscle
  or area come before ones that only touch it.
- Search: every word must match (name, summary, muscles, areas, type, equipment, position);
  understands everyday words and plurals ("hammies", "roller", "towel", "floor", "warmup",
  "easy/gentle" → easier, "hard/advanced" → harder; filler like "stretches", "for my" ignored).
- Library UI: search box, quick chips (types, Easier, Fits my limits, More filters (n)),
  expandable panel with every group, "x of N shown", Clear all, helpful empty state.
- Checked: 52/52 tests, lint clean. **Not seen on the emulator** (PC had 1.5 GB free); owner to
  try on phone.

## 3D figure prototype (2026-11-09, owner request) — branch `claude/3d-stretch-models-518e55`
Owner asked: some stretches are confusing (is it seen from the top or the side?); prototype the
"own-code 3D" option and add a faux-3D background. No new dependencies (D-007 still holds; D-034).
- `figure/Figure3d.kt`: 3D skeleton (x forward, y down, z toward the side-view viewer), camera
  (`Camera` yaw/pitch, named `View`s Side / Angled / Front / Above), gentle perspective
  (`Projection`), and `Scene` (frame covering every keyframe + exercise-mat extent).
  `Figure.solve` now = 3D solve flattened to the side view, so IK, catalog tests and ROM checks
  use one model. A test re-runs the old 2D solver on every catalog pose: identical.
- New `Pose` values (all default 0, so existing poses are unchanged): `sideBend`, `twist`,
  `near/farArmOut`, `near/farLegOut`, `near/farLegTurn` (hip rotation). They appear as Pose
  Studio sliders automatically. `Motion.view` picks the starting viewpoint.
- Renderer (`ui/figure/FigureView.kt`): floor plane with tiles whose back edge reads as a
  horizon, an exercise mat, flat shadows straight under every body part, limbs sorted
  far-to-near, far parts faded by distance across the floor, walls/rollers drawn in 3D
  (a wall between camera and body is see-through). Side view keeps 12° of height so the floor
  shows. Thumbnails hide the tiles.
- "Side view" chip on the stretch detail screen and in the player cycles the view; the camera
  turns smoothly (instantly with reduced motion). Pose Studio has a view picker; "Copy whole
  motion" now also copies `pins` and `view` (it used to drop pins).
- Catalog: butterfly now opens its knees to the sides with soles together (Angled view);
  cross-body shoulder brings the arm across the chest (Front view). Cues unchanged.
- Checks run: `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug` — pass; 7 new tests in
  `Figure3dTest`; lint has only the existing dependency-version warnings. Seen on emulator
  `emulator-5556` (owner's phone untouched): Today/library thumbnails, cross-body Front and
  Above, butterfly Angled.
- **Not checked / known limits:** player screen view chip not opened on the emulator; IK pins
  ignore sideways values (fine for current rolling motions); butterfly hands are near, not on,
  the ankles; side-lying quad is still drawn face-down (could now be rolled onto its side);
  Above view of standing stretches is hard to read. Owner to judge the look before more
  stretches get sideways poses.

## Tender spot flow (2026-11-09) — P-10 item 4
- Today → "Sore spot?" card → pick a muscle, answer two questions ("sore for more than 2
  weeks?", "hurts at rest, at night, or after an injury or fall?"). Any yes → "Best to get this
  checked" (EVIDENCE §13) and no routine. Otherwise `Generator.tenderSpot`: roll that spot (only
  with a roller and if allowed), the 1–2 easiest stretches for it, a strengthen move for the area
  if one fits; gentle 30 s holds; trimmed to ≤ 6 min. All limits apply; an avoided area gets a
  note and no moves. Start goes straight to the player ("Tender spot: calves").
- Wording avoids Tier C claims (no "releases knots"); "short, gentle routine".
- Checked: 61/61 tests (2 new), lint clean. Not seen on the emulator (memory); no animation code
  touched (3D work in progress on its branch).

## Stretch library round 5 + 3D finish (2026-11-09, owner: "in charge of the stretch library")
Branch `claude/3d-stretch-models-518e55`, up to date with `main` (fast-forwarded to `20d63ce` first).
- **Outline (owner request, D-035):** every body part, the head and the foam roller get the same
  near-black outline (`FigureColors.outline`) in every view, so outlines no longer appear and
  vanish as limbs move. The old background-colored "halo" on near limbs is gone; parts behind
  the torso still fade a little.
- **Shared drawing:** `figure/FigureDrawing.kt` turns a pose + `Stage` (scene + prop) into plain
  shapes (`Shape`, `Ink`); `ui/figure/FigureView.kt` paints them with Compose and
  `tools/pose-preview` paints the same list with Java2D. Preview images now match the app,
  from all four views. Windows runner: `tools/pose-preview/build-and-run.ps1` (see README).
- **New pose values:** `headTilt`, `near/farArmTurn` (shoulder rotation: goalpost arms),
  `roll` (whole body onto its side). All default 0; a test re-checks every old pose against
  the original 2D solver.
- **12 new moves (55 total),** all from S2/S3 (+S4/S5 for the flow, S9 for the strengthen
  hold, S15/S19 where the similar old entries use them): ear-to-shoulder neck stretch, standing
  side bend, lying figure-four, lying knee-drop twist, seated twist (level 2), thread the needle,
  wide-leg seated stretch (level 2), side lunge stretch (level 2), overhead triceps stretch,
  standing torso turns (flow), side leg raise hold (strengthen), reclining butterfly.
  Variants: reclining butterfly → butterfly → wide-leg; knee-drop twist → seated twist.
- **Safety:** new `spinalTwist` flag; `Generator` leaves twisting moves out for osteoporosis
  (EVIDENCE §9 already said "flexion and twisting"); setup text now says "round or twist".
- **Existing moves reviewed** from all views with the preview tool. Changed: wall angel arms now
  go out to the sides like real goalposts (Front view); side-lying quad really lies on its side
  (`roll`, Above view); foam rolling ×3 and glute bridge start Angled (rollers read as
  cylinders). Everything else stays Side, which was clearest.
- Voice clips: 42 new clips (Kokoro af_heart). The generator rewrites every clip, so the 128
  unchanged existing clips were restored from git to keep the commit small.
- `docs/ADDING_STRETCHES.md` §3 now has the 3D authoring guide (values, ranges, when to pick
  each view, previewing).
- Checks run: `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug` pass (64 tests, 3 new
  in `Figure3dTest`; `LibraryFilterTest` updated for the second "butterfly"). Every keyframe of
  all 55 moves rendered with pose-preview and looked at. Then seen on emulator `Pixel_8`
  (`emulator-5554`, the only device; shut down afterwards): library shows 55, seated twist
  detail (Angled) and player, view chip Angled → Front, outlines steady while moving, dark mode
  readable (light figure, dark outlines). Search fix: "twist" now finds all four twisting moves
  (thread the needle and torso turns didn't say "twist"); `LibraryFilterTest` checks it.
- **Owner to check:** how the new moves look in motion on the phone; wording of the 12 new cue
  sets; whether seated twist / side lunge / wide-leg should be level 2 (chosen as "harder").
  Known limits: IK pins ignore sideways values; torso turns are subtle from the Front view (turn
  the view to Above to see the twist).

## Play readiness: release build (2026-11-09)
- `app/build.gradle.kts`: release builds shrink code and resources (R8; `app/proguard-rules.pro`,
  currently no extra rules). APK 14.3 MB (debug) → 4.2 MB (release).
- Signing reads `keystore.properties` at the project root (`storeFile`, `storePassword`,
  `keyAlias`, `keyPassword`); that file and `*.jks`/`*.keystore` are git-ignored. Without it the
  release APK is built unsigned. **Owner step:** create the upload key in Android Studio (Build →
  Generate Signed Bundle/APK → Create new) and keep it backed up somewhere safe; losing it means
  asking Google to reset the upload key.
- New `staging` build type = release (shrunk) signed with the debug key, installed as a separate
  app (`dev.clean.stretch.staging`, "-staging" version) so the shrunk build can be tested
  without touching real data: `.\gradlew.bat :app:assembleStaging`.
- Checked: `assembleRelease` and `assembleStaging` succeed. **Not yet run:** the shrunk app on a
  device (the only emulator was the 3D thread's; PC had 1.6 GB free).

## Delete my data + shrunk build on a device (2026-11-09)
- Settings → Your data → **Delete my data** (red). Dialog lists what's erased, suggests a
  backup first, and "Delete everything" stays disabled until "I understand this can't be
  undone" is ticked. `BackupManager.deleteEverything()` clears history, profile, saved routines
  and ROM checks in one transaction, then the unfinished-routine checkpoint and all settings
  (`AppPreferences.clearAll`), and the reminder is cancelled.
- Emulator (Pixel_8, staging = shrunk release build, separate app): launched, setup saved,
  routine started (3D player) and ended → history showed it; delete with box unticked did
  nothing; ticked → "All your Stretch data has been deleted.", history empty, setup card back.
  No crashes. 64/64 tests, lint clean.
- Memory tip: `.\gradlew.bat --stop` after building frees ~5 GB of idle Gradle daemons.

## Catalog split by body area (2026-11-09, stretch-library thread)
- `catalog/SampleCatalog.kt` (1,500 lines) is split into `NeckMoves.kt` (4), `ShoulderMoves.kt`
  (5), `BackMoves.kt` (12), `CoreMoves.kt` (5), `HipMoves.kt` (13) and `LegMoves.kt` (16), by each
  move's first main muscle. `SampleCatalog` keeps library order (`all`, unchanged), `byKey`
  and the easier/harder pairs. Move definitions were copied unchanged (checked by script).
- New `CatalogTest.everyMoveLivesInItsAreaFileAndIsListedOnce`. `ADDING_STRETCHES.md` §2 updated.
- Checks run: `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug` pass (65 tests, fresh
  run); pose-preview still compiles and runs. One earlier run reported a build failure whose
  message wasn't kept; it didn't happen again on two more runs.
- On the branch only; not merged or pushed (the other thread is handling `main`).

## Exercise review log (2026-11-09, owner request)
- Owner: thread the needle fails the ground contact rule (one arm should likely touch the
  ground). **Not fixed** (owner: "Don't fix"); recorded as `Changes requested`.
- New `docs/REVIEW_LOG.md`: one row per move (status, approval date, fingerprint, owner's
  verbatim approval, open feedback) plus an append-only history. All 55 moves start
  `Not reviewed` except thread the needle. Rule (also in AGENTS.md): only the owner marks a move
  `Reviewed`, with explicit words such as "no further changes"; feedback and making changes are
  never approval.
- `ReviewLogTest`: every move has exactly one row; statuses are filled in properly; a `Reviewed`
  move must still match the fingerprint stored at approval (any change to pose, cues, wording,
  view or sources fails the test until the review is reopened). Current fingerprints are
  written to `app/build/review-fingerprints.txt`.
- `app/build.gradle.kts`: unit tests now declare `docs/REVIEW_LOG.md` and
  `tools/voice/phrases.txt` as inputs. Without this, Gradle reused a cached pass after the log
  was edited, so a bad row went unnoticed (found while testing the guard).
- Checked: a planted fake approval with a stale fingerprint fails the test; the real log passes.

## Merge, library updates and screen-off running (2026-11-13)
- Merged `claude/3d-stretch-models-518e55` (catalog split by body area, `docs/REVIEW_LOG.md`
  with `ReviewLogTest`). CONTINUE.md conflict: kept the current-state guide; that branch's notes
  are the two sections above. Checks after merge: 68 tests pass, lint clean.
- Updated Gradle 9.3.1 → 9.9.1, Compose BOM 2026.02.01 → 2026.09.00, Activity Compose 1.12.4 →
  1.13.0, Room 2.8.4 → 2.8.5; compileSdk/targetSdk 36 → 37 (the new Compose requires compiling
  against 37). New Compose lint caught `NonObservableLocale` in TodayScreen's week strip (fixed
  with `LocalConfiguration`). Lint now shows no warnings. Staging APK 4.2 → 5.2 MB.
- Screen-off running (D-037): `run/RunEngine` (pure state machine on `elapsedRealtime`, recorded
  holds capped at their length), `run/RunSession` (cues, checkpoints, saving, wake lock), and
  `run/RunService` (foreground service + notification). `PlayerScreen` now only draws the session.
  New `RunEngineTest` (11 tests). Manifest: FOREGROUND_SERVICE, FOREGROUND_SERVICE_MEDIA_PLAYBACK,
  WAKE_LOCK.
- Checks run: `:app:testDebugUnitTest :app:assembleDebug :app:lintDebug` pass (79 tests);
  `assembleStaging` builds. Emulator (API 37): see CONTINUE "Verified 2026-11-13". The emulator's
  notification permission was granted with `pm grant` to see the notification.
- Noticed, not changed: the finish screen says "Saved to your history" even when every hold was
  skipped and nothing was saved.

## Seasonal colour palettes (2026-11-13, D-038)
- `settings/Palette` (Fall default, Spring, Winter, Summer, Wallpaper) saved as `palette`;
  backed up and restored with the other settings. The old `wallpaper_colors` key is ignored.
- `ui/theme/Theme.kt`: full light and dark schemes for each palette, including surface
  containers (the old sage theme left those to Android's defaults). Settings → Appearance:
  "Light or dark" plus a Colors list with three swatches per palette.
- New `PaletteTest`: default is Burnt fall; text/background pairs ≥ 4.5:1 in every palette.
- Checks run: tests (81), assembleDebug, lintDebug all pass, no lint issues. Emulator (API 37):
  Burnt fall in dark and light on Today and Settings. The emulator was left on the Light setting.

## Per-muscle goals, body view, single RoM checks (2026-11-13, D-039)
- Read Ingram 2024 full text (Methods, Results, Figs 2–3, Discussion): dose ranges recorded in
  EVIDENCE §1. Tapping one range-of-motion check on Progress opens just that check.
- New area Arms & wrists; new muscles biceps, triceps, forearms & wrists, side body, outer hip,
  deep hip rotators, shins & ankles. Retagged: overhead triceps → triceps (moved to new
  `ArmMoves.kt`), figure-four + deep hip rotators, torso turns → side body, twists + side body
  (secondary), side leg raise → outer hip, wall chest + biceps (secondary). Library and Sore
  spot list only muscles that have moves.
- Database v6 (auto-migration): `session_steps.primaryMuscles`, `profile.secondary`,
  `profile.weeklyDose`. Backups include the new fields; older backups still restore.
- `Stats.minutesByMuscle` (per side), `Profile.weeklyTarget`, `Dose`, generator targets per
  muscle, `Generator.weeklyPlanMinutes` for the setup total, Weekly goals setup page,
  `ui/BodyMap.kt` replaces the six rings.
- Checks run: 83 JVM tests pass (new: per-side minutes incl. pre-v6 rows, dose/secondary
  targets, weekly total), assembleDebug, lintDebug (no issues), assembleStaging. Emulator:
  installed the previous version as the staging app, saved a 20 s neck hold, upgraded it to
  this version: no crash, body view shows "Neck: right 0.3 of 5 min". Weekly goals page:
  Hips secondary → about 27 min/week; + Legs primary → about 57 min/week with the warning.
- Not checked: TalkBack on the body view (it has a spoken summary), the Weekly goals page in the
  first-run flow end to end on a phone.

## Forearm and wrist moves (2026-11-13, D-040)
- New `Pose.nearWrist` / `farWrist`, `Figure3d.handTips`, hand drawn with the forearm's outline,
  ink and highlight in `FigureDrawing`; Pose Studio gets wrist sliders. No hand when the wrist is
  0, so older poses draw exactly as before (new `Figure3dTest.handIsDrawnOnlyWhenTheWristIsBent`).
- `ArmMoves`: wrist_flexor, wrist_extensor (sided, Angled view), prayer_stretch (both sides,
  Front view); prayer arm angles worked out so the palms meet at the middle. Review log rows
  added as Not reviewed. Search: wrist, forearm, hand, bicep, tricep, piriformis, shin, oblique.
- Voice: 10 new af_heart clips. The script first rewrote every clip; the old ones were restored
  from git and the script now only makes missing clips.
- Checks run: 84 JVM tests pass, assembleDebug, lintDebug (no issues); pose-preview of all three
  moves in every view; emulator: library search "wrist" shows exactly the three moves, wrist
  flexor detail screen shows the bent hand. Not checked: how the new clips sound.

## Automatic database-upgrade tests (2026-11-13, D-041)
- Owner approved `room-testing`; it failed at run time (needs kotlinx-serialization 1.8.1, the
  app ships 1.7.3 through AndroidX activity), so it was removed again rather than change a
  shipped library. `app/src/androidTest/.../data/MigrationTest.kt` builds a v1 and a v5 database
  from `app/schemas` (added as androidTest assets) with real rows, opens them with the current
  app (Room runs the auto-migrations and validates every table), and reads the rows back through
  the DAOs, including per-side minutes for a pre-v6 step.
- Checked the test can fail: with the 5→6 migration removed, it fails; restored.
- Checks run: 84 JVM tests, assembleDebug, lintDebug (no issues), 2 emulator tests pass. The
  app stayed installed (leaveApksInstalledAfterRun).
