# Continue here

**State on 2026-11-20 (D-045):** every first-release slice (S0–S7) exists, plus S8 (strengthen and
foam rolling), S9 (range-of-motion checks, now 9), a 3D figure, 77 moves (every muscle has at
least three main moves, D-043), the catalog split by body
area with an owner review log, and **routines that keep running with the screen off** (D-037).
Version `0.3.0-first-release-slices` (versionCode 3), database v6, compile/target SDK 37.
98 JVM tests pass; lint shows no errors (2 "newer version available" warnings). Pushed to https://github.com/2AronB/CLEAN_Stretch.

The day-by-day log, with every check that was run, is in `docs/HISTORY.md`. Decisions are in
`docs/DECISIONS.md` (D-001…D-045). How to add a move: `docs/ADDING_STRETCHES.md`. Play
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

**Library (72 moves, 20 muscles in 7 areas)**
- 51 holds, 11 flowing moves, 7 strengthen holds and 3 foam-rolling moves; easier/harder pairs
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

## Added 2026-11-19 (later): owner round D-044 — seen on the emulator

- **Player:** 15 s to get into position (10 s to switch sides) while the figure shows the new
  stretch once; "Starting in 3-2-1" with spoken numbers, then "Begin. Hold for N seconds."
  Nothing is announced during a hold.
- **Today:** routine type remembered; **Hold each stretch 30 / 45 / 60 s** (warm-ups 30 s);
  **Include: Kneeling / Floor / Foam roller / Strengthen** (replaces Setup's limits page; no
  wall question). Routine length follows the weekly goals, capped at 15 min.
- **Weekly goals:** 5 / 7 / 10 / 15 min, new users start on 7.
- **Setup:** three sections — Goal and weekly goals; Health and limits (65+, avoid,
  conditions); Days and reminders (1–3 reminders, each with its own routine).

## Added 2026-11-20: D-045 — weekly goals sized to real life

- No areas picked = the weekly goal for 8 commonly stiff muscles (neck, chest, upper and lower
  back, hip flexors, glutes, hamstrings, calves); smaller muscles (arms, core, inner/outer/deep
  hip, shins) aim for half. Default plan fell from ~137 to 60 min of holding a week.
- Seen on the emulator: Setup summary and weekly goals page.
- **Still open:** 60 min/week needs ~27 min a day on the default 3 days; routines stop at 15.

## Added 2026-11-19: owner round D-043 (seen on the emulator during D-044 only in part)

Owner's order: moves (min 3 per muscle) → body view → last two checks → engine work; plus
approval for `RECEIVE_BOOT_COMPLETED`. All four are built, tested and committed:

- **5 new moves** (Not reviewed in `docs/REVIEW_LOG.md`, new voice clips): Hands-behind-head
  chest stretch, Eagle arms (level 2), Wall triceps, Towel triceps (level 2), Arm circles (the
  first move that loops through several positions). Chest, shoulders and triceps now have three
  main moves each; a test keeps it that way for every muscle.
- **Body view:** tap a muscle → **Stretch this now** (up to three stretches for it, your limits
  respected). **Figure / List** switch (list starts on with TalkBack or very large text).
  **This week / Last week** switch.
- **Motion checks:** **Back-scratch reach** (seen from behind) and **Neck turn** (a small nose
  shows the turn).
- **Figure and engine:** head turn and a Back view (also in the view chip); flowing moves can loop
  through several positions (circles); **thread the needle** now has a hand flat on the floor and
  the threading arm resting on the floor (still "Changes requested" until you look).
- **Reminders** come back by themselves after a phone restart or app update.

Checks: 89 JVM tests pass, debug build and lint fine. **Not seen on the emulator**: it shut down
during this session and the only one running belonged to CLEAN Sensors, so it was left alone.

## Added 2026-11-14: moves for the five empty muscles (PLAN §8.2)

14 new moves, all `Not reviewed` in `docs/REVIEW_LOG.md`, each with poses checked in every view
with `tools/pose-preview` and new af_heart voice clips (44 new clips; no existing clip changed):

| Muscle | New moves (key) |
|---|---|
| Shins & ankles | Standing shin stretch (`standing_shin`), Kneeling shin stretch (`kneeling_shin`, level 2), Ankle pumps (`ankle_pumps`, flowing) |
| Biceps | Wall biceps stretch (`wall_biceps`), Standing biceps stretch (`standing_biceps`), Seated biceps stretch (`seated_biceps`, level 2) |
| Outer hip | Standing outer-hip stretch (`standing_outer_hip`), Seated outer-hip stretch (`seated_outer_hip`), Side leg swings (`side_leg_swings`, flowing) |
| Deep hip rotators | Seated figure-four stretch (`seated_figure_four`), Pigeon stretch (`pigeon`, level 2), Knee to opposite shoulder (`knee_to_opposite_shoulder`) |
| Side body | Seated side bend (`seated_side_bend`), Standing side reaches (`side_reaches`, flowing) |

- **Standing side bend** now counts for Side body as well as Lats (owner: "Yes, main = both").
  Nothing else about it changed.
- New Easier/Harder links: standing shin → kneeling shin, standing biceps → seated biceps,
  lying figure-four → pigeon. Search also understands foot/feet, itb, tfl, abductor, waist, "figure 4".
- `GeneratorTest` used to assert that shins had no moves; it now expects a normal target.
- Checks run after every muscle (84 JVM tests) and again on `main` after merging the D-042 work
  (85 JVM tests; lint 0 errors, 0 warnings, 2 style hints in BodyMap.kt and TodayScreen.kt): all pass,
  `assembleDebug` builds, `lintDebug` reports no issues. Not run: the emulator (nothing in this
  work touches the database or screens), so the new moves have **not** been seen in the app itself.
- Chest, shoulders and triceps were filled on 2026-11-19 (D-043).

## Verified vs not

- **Verified:** JVM tests for generator, safety rules, weekly dose, library filter and search,
  backup, history/streaks, ROM trends, IK pins, joint ranges, 3D solver, voice-clip coverage;
  emulator walk-throughs of most screens; the shrunk staging build (setup, 3D player, history,
  delete); reminder delivery on the emulator; voice quality and setup times on the owner's phone.
- **Verified 2026-11-13 (API 37 emulator):** screen locked + forced Doze for 70 s mid-routine:
  holds recorded at full length and steps advanced on time; voice clips played; notification
  countdown, Pause (timer stops, wake lock released), Resume, tap-to-return, End (saved, service
  and wake lock gone), finish screen, rotating mid-run (same run continues).
- **Still to see on the emulator (agent) and phone (owner):** everything from 2026-11-19 (D-043):
  the five new moves and their clips, Stretch this now, list mode, last week, the two new checks,
  the Back view, thread the needle's new pose, and a reminder after a restart.
- **Still to see on the owner's phone:** a real routine with the phone locked (voice through the
  speaker, battery), Generator v2 suggestions day to day, library search/filters, Tender spot,
  the setup section menu, round-5 moves in motion, TalkBack and the largest font size, and the
  14 new moves from 2026-11-14 (in motion, and how their voice clips sound).

## Owner actions

1. Create the Play **upload key** (Android Studio → Build → Generate Signed Bundle/APK → Create
   new), fill `keystore.properties`, and back the key up safely.
2. **Final review of health conditions** and their limits against a current clinical source
   before release (D-031). This blocks release.
3. Review wording: the 12 round-5 cue sets and the level-2 choices (seated twist, side lunge,
   wide-leg), strength and rolling cautions, and ROM check levels.
4. Review the 14 new moves from 2026-11-14 and the 5 from 2026-11-19 in the app (pose, motion,
   cues, voice clips), plus thread the needle's new pose, and say which are approved; they're
   listed above and in `docs/REVIEW_LOG.md`.
5. Review the drafts in `docs/PLAY_RELEASE.md` (privacy policy, Data safety, health declaration,
   store text) and add a contact address.

## Next (recommended order)

Owner said 2026-11-19: they're using the app and will give feedback when ready.

**The full plan is `docs/PLAN.md` §8.** Short version:

1. **Owner decision (D-045 follow-up):** the default plan (60 min/week) still needs ~27 min a day
   on 3 days. Options: start new users at 5 days a week, a shorter stiff list, or keep it and
   let the Setup note explain. Owner also to review the stiff list and which muscles count as
   smaller.
2. Agent: emulator walk-through of the D-043 body view, motion checks and new moves (the D-044
   pass covered the player, Today, Setup and reminders).
2. Owner feedback round from using the app; owner reviews of the new moves (see Owner actions).
3. Goals tuning (PLAN §8.7): "goal met" moment, optional foam-rolling toggle (not decided).
4. More sequence flows where they'd help (e.g. hip circles, ankle circles) — propose first.
5. Before release (§8.8): clinical review of health conditions (blocks release), screenshots and
   store graphics, content rating, foreground-service declaration, `bundleRelease` with the upload key.

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
- A new git worktree has no `local.properties` (it's git-ignored), so Gradle can't find the
  Android SDK. Copy it from the main folder first.
- Parallel sessions: use git worktrees; the only file that tends to conflict is this one.
  Merge into `main`, run the full checks, then push.
