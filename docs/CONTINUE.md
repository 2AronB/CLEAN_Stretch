# Continue here

**State on 2026-11-13:** every first-release slice (S0–S7) exists, plus S8 (strengthen and
foam rolling), S9 (range-of-motion checks), a 3D figure, 55 moves, the catalog split by body
area with an owner review log, and **routines that keep running with the screen off** (D-037).
Version `0.3.0-first-release-slices` (versionCode 3), database v6, compile/target SDK 37.
83 JVM tests pass and lint shows no warnings. Pushed to https://github.com/2AronB/CLEAN_Stretch.

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
- Weekly goals per muscle, per side (D-039): primary areas get the chosen dose (5 / 10 / 15
  min), secondary areas 5 min; today's share = what's left on the weaker side ÷ planned days
  left, never more than 4 min per muscle per side per session; moves that help several needy
  muscles are preferred; warm-ups chase goals with flowing moves only; a met goal shifts time
  elsewhere with a note.
- Variety: seeded by date; moves used often this week go to the back. Order stays standing →
  floor.
- Warm-ups use flowing moves first; full routines and cool-downs use holds. Optional
  Strengthen block at the end (automatic for Desk and hypermobility) and a minute of foam
  rolling at the start of warm-ups and cool-downs if the user has a roller.
- Safety filters: areas to avoid, no floor, no kneeling, no wall, no roller, pregnancy (no lying,
  easier only), osteoporosis (no spinal flexion or twisting, no rolling on the spine),
  hypermobility (easier only). Notes explain anything left out.

**Setup and settings**
- First run: 6 pages (goal + 65+, weekly goals (Off / Secondary / Primary per area, dose choice
  with explanations, live weekly total), limits and equipment incl. foam roller and
  Strengthen switch, health conditions with the doctor-advice dialog, routine length, schedule:
  if-then cue, days a week, daily reminder).
- Afterwards, Settings → **Goal, focus and limits** (highlighted card) opens a menu of the six
  sections with summaries.
- Settings also has light/dark, **colours** (Burnt fall default, Spring pastels, Winter calm,
  Summer energy, Wallpaper; D-038), still figures, voice/chimes/vibration switches,
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
  outlines (D-034, D-035); hands bend at the wrist when a pose sets a wrist angle (D-040). Rolling keeps hands or feet planted with two-bone IK.

**Library (58 moves, 20 muscles in 7 areas)**
- 37 holds, 8 flowing moves, 7 strengthen holds and 3 foam-rolling moves; easier/harder pairs
  with "Other versions" links.
- Search where every word must match and everyday words are understood ("easy hamstring",
  "roller calves", "twist"), plus filters for type, area, muscle, position, equipment, level and
  "Fits my limits"; results easiest first.

**Progress**
- Week streak, **body view** (front and back figures, a bar per muscle per side filling toward
  this week's goal; colour = primary / secondary / no goal; tap a muscle for its minutes;
  `ui/BodyMap.kt`), range-of-motion card (picture-based check-ins: toe reach, overhead reach,
  ankle lunge, straight-leg raise; "+N steps since…"; tap one check to update just that one),
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

**The full next-stage plan, in order and with sizes, is `docs/PLAN.md` §8.** The list below is the short version.

1. Owner to try on the phone (2026-11-14 round, D-042): Settings menus, the Today link popup and
   chips, the filling day circles and "Why?", the anatomy chart (side views are the roughest),
   the three new motion checks and the weekly check-in pop-up, the wrist moves' thumbs.
2. Owner review of the new wrist moves (wrist flexor, wrist extensor, prayer stretch, D-040) and
   their voice clips. Then new moves for muscles that still have none: biceps (doorway or wall biceps), shins & ankles (kneeling shin stretch), side body
   (standing side bend is tagged Back; consider retagging), outer hip (cross-body glute, IT-band
   side stretch). Each needs poses, cues, sources and voice clips (ADDING_STRETCHES.md).
3. More range-of-motion checks (proposed, not built): back-scratch shoulder reach, hip-flexor
   "Thomas test" lying knee hug, neck turn, seated twist, butterfly knee height.
4. Multi-keyframe flows (circles, sequences) in `figure/Motion.kt`; optional Advanced toggle to
   count foam-rolling minutes toward the area rings (explained to owner, not decided).
5. Before release: screenshots and store graphics, content rating, foreground-service
   declaration, `bundleRelease` with the upload key.

## Working on this project (practical notes)

- Build, test and lint: `$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'` then
  `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug`.
- Database-upgrade tests (emulator running, D-041):
  `.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true`.
  **Always pass that flag**: without it Gradle uninstalls the app afterwards, which deletes its data.
  A database change needs a new schema version and a test case in `MigrationTest`.
- **Memory:** this PC has 16 GB. Run `.\gradlew.bat --stop` after building (idle Gradle daemons
  hold ~5 GB) and run one emulator at a time. Never test on the owner's phone (AGENTS.md).
- Poses without an emulator: `tools/pose-preview/build-and-run.ps1` renders every keyframe from
  all four views using the app's own drawing code.
- Voice clips: `tools/voice/generate.py` with the Kokoro setup in
  `C:\Users\Kayli\Desktop\CLEAN Development\kokoro` (steps in ADDING_STRETCHES.md §6).
- Parallel sessions: use git worktrees; the only file that tends to conflict is this one.
  Merge into `main`, run the full checks, then push.
