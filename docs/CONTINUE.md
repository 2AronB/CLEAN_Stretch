# Continue here

**State on 2026-11-13:** every first-release slice (S0–S7) exists, plus S8 (strengthen and
foam rolling), S9 (range-of-motion checks), a 3D figure, 55 moves, the catalog split by body
area with an owner review log, and **routines that keep running with the screen off** (D-037).
Version `0.3.0-first-release-slices` (versionCode 3), database v5, compile/target SDK 37.
79 JVM tests pass and lint shows no warnings. Pushed to https://github.com/2AronB/CLEAN_Stretch.

The day-by-day log, with every check that was run, is in `docs/HISTORY.md`. Decisions are in
`docs/DECISIONS.md` (D-001…D-036). How to add a move: `docs/ADDING_STRETCHES.md`. Play
Store checklist and drafts: `docs/PLAY_RELEASE.md`.

## What the app does now

**Today**
- Suggested routine for the chosen kind (Full stretch / Warm-up / Cool-down / Quick break),
  shown as move tiles with **Start** (straight into the player) and **Edit** (optional
  changes, D-032).
- "Make it yours" setup card until setup is done; "Unfinished routine → Resume / Save what I
  did" after an interruption; "Sore spot?" (Tender spot); saved routines; this week's days and
  week streak; an evidence card.

**Routine generator (Generator v2, `plan/Generator.kt`)**
- Weekly dose (P-1): each focus area gets today's share of ~10 min/week (what's left ÷ planned
  days still to come), never more than 4 min per area per session; leftover time fills the
  least-covered areas; a met goal shifts time elsewhere with a note.
- Variety: seeded by date; moves used often this week go to the back. Order stays standing →
  floor.
- Warm-ups use flowing moves first; full routines and cool-downs use holds. Optional
  Strengthen block at the end (automatic for Desk and hypermobility) and a minute of foam
  rolling at the start of warm-ups and cool-downs if the user has a roller.
- Safety filters: areas to avoid, no floor, no kneeling, no wall, no roller, pregnancy (no lying,
  easier only), osteoporosis (no spinal flexion or twisting, no rolling on the spine),
  hypermobility (easier only). Notes explain anything left out.

**Setup and settings**
- First run: 6 pages (goal + 65+, focus areas, limits and equipment incl. foam roller and
  Strengthen switch, health conditions with the doctor-advice dialog, routine length, schedule:
  if-then cue, days a week, daily reminder).
- Afterwards, Settings → **Goal, focus and limits** (highlighted card) opens a menu of the six
  sections with summaries.
- Settings also has theme, wallpaper colours, still figures, voice/chimes/vibration switches,
  backup / restore / **Delete my data**, Sources & credits, and Pose Studio.

**Player**
- Keeps running with the screen off or the app in the background (D-037): the timer lives in
  `run/RunEngine.kt` (real clock time) and `run/RunSession.kt`, kept alive by `run/RunService.kt`
  (foreground service, "media playback"). Its notification shows the stretch, a live countdown,
  Pause/Resume and Skip; tapping it returns to the player. Swiping the app away stops the run and
  Today offers Resume. The notification only shows if notifications are allowed (Android 13+);
  the run works either way.
- Hands-free: pre-recorded Kokoro **af_heart** voice clips (D-026), 3-2-1 ticks and chimes,
  vibration, keep-screen-on. Setup time depends on the move (5 / 10 / 15 s) with "I'm ready";
  "+15 s"; skip and back; End saves an "ended early" session; checkpoint and resume after the
  app is killed.
- 3D figure with Side / Angled / Front / Above views (view chip), floor, mat, shadows and black
  outlines (D-034, D-035). Rolling keeps hands or feet planted with two-bone IK.

**Library (55 moves)**
- 37 holds, 8 flowing moves, 7 strengthen holds and 3 foam-rolling moves; easier/harder pairs
  with "Other versions" links.
- Search where every word must match and everyday words are understood ("easy hamstring",
  "roller calves", "twist"), plus filters for type, area, muscle, position, equipment, level and
  "Fits my limits"; results easiest first.

**Progress**
- Week streak, six area rings (stretch minutes this week), range-of-motion card (picture-based
  check-ins: toe reach, overhead reach, ankle lunge, straight-leg raise; "+N steps since…"),
  and session history.

**Reminders**: daily, "around" the chosen time (one-shot alarm with a 10-minute window, re-armed
daily and on every setting change; the notification permission is asked only when it's turned
on). It's re-armed when the app opens, since the boot permission isn't approved.

**Data**: on the device only (Room v5 + small settings file). Versioned backup file with
validated restore, Delete my data, no internet permission, Android auto-backup off.

**Release build**: shrunk (4.2 MB APK); signing reads the git-ignored `keystore.properties`;
`staging` build = shrunk + debug-signed, installed as a separate app for testing.

## Verified vs not

- **Verified:** JVM tests for generator, safety rules, weekly dose, library filter and search,
  backup, history/streaks, ROM trends, IK pins, joint ranges, 3D solver, voice-clip coverage;
  emulator walk-throughs of most screens; the shrunk staging build (setup, 3D player, history,
  delete); reminder delivery on the emulator; voice quality and setup times on the owner's phone.
- **Verified 2026-11-13 (API 37 emulator):** screen locked + forced Doze for 70 s mid-routine:
  holds recorded at full length and steps advanced on time; voice clips played; notification
  countdown, Pause (timer stops, wake lock released), Resume, tap-to-return, End (saved, service
  and wake lock gone), finish screen, rotating mid-run (same run continues).
- **Still to see on the owner's phone:** a real routine with the phone locked (voice through the
  speaker, battery), Generator v2 suggestions day to day, library search/filters, Tender spot,
  the setup section menu, round-5 moves in motion, TalkBack and the largest font size.

## Owner actions

1. Create the Play **upload key** (Android Studio → Build → Generate Signed Bundle/APK → Create
   new), fill `keystore.properties`, and back the key up safely.
2. **Final review of health conditions** and their limits against a current clinical source
   before release (D-031). This blocks release.
3. Review wording: the 12 round-5 cue sets and the level-2 choices (seated twist, side lunge,
   wide-leg), strength and rolling cautions, and ROM check levels.
4. Review the drafts in `docs/PLAY_RELEASE.md` (privacy policy, Data safety, health declaration,
   store text) and add a contact address.

## Next (recommended order)

Owner said 2026-11-13: publishing is still a way off, so focus on improvements.

1. Colour themes: four seasonal palettes proposed (Burnt fall, Spring pastels, Winter calm,
   Summer energy) as a user choice in Settings, alongside wallpaper colours. Waiting on the
   owner's pick of a default and any colour changes.
2. Continue the catalog with `docs/ADDING_STRETCHES.md` (catalog is now split by body area).
3. Multi-keyframe flows (circles, sequences) in `figure/Motion.kt`; optional Advanced toggle to
   count foam-rolling minutes toward the area rings (explained to owner, not decided).
4. Before release: screenshots and store graphics, content rating, foreground-service
   declaration, `bundleRelease` with the upload key.

## Working on this project (practical notes)

- Build, test and lint: `$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'` then
  `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug`.
- **Memory:** this PC has 16 GB. Run `.\gradlew.bat --stop` after building (idle Gradle daemons
  hold ~5 GB) and run one emulator at a time. Never test on the owner's phone (AGENTS.md).
- Poses without an emulator: `tools/pose-preview/build-and-run.ps1` renders every keyframe from
  all four views using the app's own drawing code.
- Voice clips: `tools/voice/generate.py` with the Kokoro setup in
  `C:\Users\Kayli\Desktop\CLEAN Development\kokoro` (steps in ADDING_STRETCHES.md §6).
- Parallel sessions: use git worktrees; the only file that tends to conflict is this one.
  Merge into `main`, run the full checks, then push.
