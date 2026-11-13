# CLEAN Stretch — implementation plan (DRAFT)

Drafted 2026-11-08. **No code is authorized.** This plan becomes actionable only
when the owner approves the baseline in docs/PRODUCT.md and names a slice to
build. Recommendations are marked **(rec.)**; settled choices link to
docs/DECISIONS.md. Evidence defaults P-1…P-10 live in docs/EVIDENCE.md.

---

## 1. Technical approach

| Area | Recommendation | Why |
|---|---|---|
| Language/UI | Kotlin + Jetpack Compose + Material 3 **(rec.)** | Same as Words, Sensors and Lift; native "stock app" feel; the owner already builds it. |
| Look | Material You dynamic color (wallpaper-based) with a calm fallback palette; System/Light/Dark setting like Words; generous spacing, large type, motion that is fast but never blocking | Feels like a Google app. Animation never owns state (Words R-018 lesson). |
| Min / target SDK | minSdk 26 (Android 8), compile/target 36 **(rec.)** | Matches Words; test phone is Android 16. |
| Package | `dev.clean.stretch`, display name **Stretch** | Follows `dev.clean.words`. |
| Storage | Room database for profile, history, routines, ROM checks; small preferences file for settings **(rec.)** | Lift's proven pattern: real migrations, schema export, history snapshots. |
| Catalog | Bundled, versioned Kotlin data (stretches, poses, sources) installed by stable key | Lift's catalog pattern; works offline; history never depends on catalog edits. |
| Voice cues | Android's built-in `TextToSpeech` (offline engine on device) | No dependency; falls back to chimes if no voice is installed. |
| Chime | Short bundled sound via `SoundPool` | No dependency. |
| Vibration | `Vibrator` / `VibrationEffect` | Needs the normal-level VIBRATE permission (auto-granted; still listed for approval). |
| Screen on | Window flag `FLAG_KEEP_SCREEN_ON` while a routine runs | No permission (D-010). |
| Reminders | `AlarmManager` inexact daily alarm + notification channel; reschedule after reboot **(rec.)** | No WorkManager dependency, no exact-alarm permission. Needs POST_NOTIFICATIONS (approved, D-016) and RECEIVE_BOOT_COMPLETED (needs approval). |
| Backup | Storage Access Framework "Save to file / Open file", versioned JSON | No storage permission; same approach as Lift. Android auto-backup off. |
| Network | None. No INTERNET permission. | CLEAN rule (D-003). |

### Dependencies to approve
| Dependency | Purpose | Alternative if declined |
|---|---|---|
| Compose BOM, Material 3, activity-compose (same versions as Words) | UI | — (baseline) |
| androidx.room (runtime + compiler) and KSP plugin | Database, migrations | Hand-written JSON files like Words (more custom code, harder history queries) |
| androidx.lifecycle viewmodel-compose / runtime-compose | Screen state that survives rotation | Manual state holders |
| JUnit, androidx.test (test only) | Tests | — |

No analytics, crash reporting, ads, image loading or animation libraries.

### Permissions to approve
| Permission | Use | Status |
|---|---|---|
| POST_NOTIFICATIONS | Daily reminder | **Approved** (D-016): ask at first launch, re-ask only when setting a reminder |
| VIBRATE | Transition buzz | Needs approval (normal permission, no prompt) |
| RECEIVE_BOOT_COMPLETED | Restore the reminder after a phone restart | Needs approval |
| FOREGROUND_SERVICE (+ type) | Locked-screen running | **Later only**, not in first release |

---

## 2. Data and state ownership

**Bundled (read-only, versioned):**
- `Stretch` — stable key, name, kind (static, dynamic, active, PNF, roll,
  strengthen), main/secondary muscles, joints crossed, position (floor,
  kneeling, standing, chair, side-lying), equipment, level, sides (one / both),
  easier/harder/standing variant keys, cautions, contraindication tags, cue
  text, source IDs, pose key.
- `Pose` — keyframed 2D skeleton angles for the animated figure.
- `Source` — citation text and URL for the offline Sources screen.

**User data (Room):**
- `Profile` — goal profile(s), age band (optional), focus muscles, weekly target
  per muscle, planned days, hold-length override, cue settings.
- `Limitation` — painful areas, position limits, conditions (hypermobility,
  pregnancy, osteoporosis, recent surgery), equipment owned.
- `Plan` — the user's if-then plan and reminder time.
- `SavedRoutine` — a generated routine the user kept (ordered steps with
  stretch keys and durations).
- `Session` — **immutable snapshot** of a completed or interrupted routine:
  start/end time, profile used, and each step's stretch key, *name snapshot*,
  side, muscles snapshot, planned seconds, actual seconds, skipped flag. Later
  catalog edits never change past sessions.
- `RomCheck` — test key, date, value, unit, side, note.

**Derived, never stored as truth:** weekly per-muscle minutes, rings, streak
weeks and consistency % are always recalculated from sessions, so they can't
drift.

**Rules:**
- Weeks start on a user-chosen day (default Monday); local time zone; a session
  counts on its start date.
- "Not measured" is distinct from zero in ROM checks.
- Deleting a saved routine never deletes history.
- History deletion (if offered) needs confirmation; a "Recently deleted"
  safety net is deferred.
- Unsupported newer backup versions are refused without touching current data
  (template rule).

---

## 3. Core designs

### 3.1 Hands-free runner (R-002)
- A routine is compiled into a flat list of **steps**: Setup (5 s, not
  counted), Hold, Switch sides, Rest, Roll pass-paced, PNF phases, Strengthen
  hold.
- A pure-Kotlin **state machine** computes the current step from a monotonic
  clock (`elapsedRealtime`), the start time and pause totals. The screen only
  displays that state, so a hiccup can't drift the timer, and the same engine
  can later run inside a foreground service for locked-screen use (D-010).
- A checkpoint is saved at each step boundary. If Android kills the app
  mid-routine, reopening offers **Resume** or **Finish & save what I did**.
- Controls: Pause/Resume, Skip, Back one step, +15 s, End (saves partial).
  Large tap targets; works with one hand on the floor.
- Cues per step: voice line ("Left hamstring, forty seconds"), chime with 3-2-1
  countdown, vibration. Each can be switched off (D-014). The voice announces
  what's next during the last 5 s.

### 3.2 Animated figure (R-004)
- One faceless, rounded figure drawn with Compose `Canvas`. Each stretch has 2–4
  keyframes (start → stretch position) plus a slow "breathing" oscillation
  during holds. Left/right is a mirror, so each stretch has one authored pose.
- Target muscles glow on the figure; a small body map shows the same areas.
- Reduced motion (system setting): show the final pose still, with no looping.
- **Authoring tool:** a debug-only **Pose Studio** screen with joint sliders and
  a "copy pose" button, so making 30–100 poses is practical. This is the
  biggest effort and risk, which is why it is spiked first (Slice S1).

### 3.3 Generator (R-001, P-1…P-8)
Inputs: profile, focus muscles, available time, session type (flexibility,
warm-up, cool-down, desk, tender spot), limitations, equipment, this week's
minutes so far.

1. **Filter** the catalog using the safety rules (EVIDENCE §9, §13); if a
   variant passes, swap it in rather than dropping the stretch.
2. **Allocate** seconds per muscle: what's left of the weekly target ÷ days
   left, capped at 4 min per session; for warm-ups, static stretching is capped
   at 30 s per muscle.
3. **Select** stretches by coverage: main muscles first, then those reaching
   all joints a muscle crosses; prefer supported positions; avoid repeats.
4. **Order** stretches to minimize getting up and down (floor → kneeling →
   standing), group the two sides, and put dynamic moves first in warm-ups.
5. **Explain:** a preview lists every stretch with a "why" line, plus an honest
   note if a limitation leaves a muscle uncovered (Lift's "no filler" rule).
6. The same inputs give the same routine (deterministic). "Shuffle" picks
   alternatives.

### 3.4 Progress (R-006)
Home shows this week's rings per focus muscle, total minutes and "3 of 4 days
this week." Streaks count **weeks** that reached the goal (P-7), and missed
days are never highlighted. History shows a calendar list. ROM checks get trend
charts (Slice S9).

---

## 4. Risks and unknowns

| Risk | Impact | Mitigation |
|---|---|---|
| Animation quality and authoring time | High: the app's "pretty" promise | S1 spike with 3 poses and Pose Studio before committing to 30 |
| Catalog accuracy and safety | High | Every entry has a source or an Editorial tag; owner review checklist; safety-filter unit tests |
| Offline voice may be missing or robotic on some phones | Medium | Detect at setup; chimes still work; offer a link to Android voice settings |
| Reminder delivery on OnePlus/OxygenOS battery optimization | Medium | Inexact alarms; test on the owner's phone; explain battery settings if a reminder is missed |
| Timer accuracy when paused, rotated or interrupted | Medium | Monotonic clock state machine plus checkpoint tests |
| Pregnancy and joint-replacement rules lack current clinical sources | Medium | Conservative defaults and a clinician prompt; verify before Play release |
| Scope creep (owner keeps everything) | Medium | Small slices; backlog items aren't built until named |
| Implementation environment (see §7) | Medium | Decide who builds and verifies |

---

## 5. Milestone slices

Each slice is complete and testable on its own. **First release = S0–S7**
(D-013). Later slices are listed so the architecture leaves room for them; they
are not authorized by being listed.

| Slice | Delivers | Requirements | Key checks |
|---|---|---|---|
| **S0 Shell** | Project, theme (dynamic color, System/Light/Dark), navigation: Today / Routines / Progress / Settings; Credits & Sources screen; no-Internet manifest | — | Build, lint, launch on emulator; manifest has no INTERNET |
| **S1 Figure spike** | Canvas figure, keyframe interpolation, breathing, mirroring, muscle glow, reduced motion, Pose Studio; 3 sample stretches | R-004 | Owner judges look and feel on phone; 60 fps check; reduced-motion check |
| **S2 Runner** | Step compiler, state machine, voice/chime/vibration, keep-screen-on, controls, checkpoint/resume, save Session; plays a fixed sample routine | R-002 | JVM state-machine tests (pause, skip, back, +15 s, kill and resume); emulator run; owner runs a 5-min routine hands-free |
| **S3 Catalog v1** | About 30 stretches with variants, metadata, poses, cues and sources | R-003, R-008 | Catalog integrity tests (keys, variants exist, every muscle covered, sources resolve); owner content review |
| **S4 Setup & limitations** | First-run flow: goal profiles, age option, focus areas, limitations, equipment, if-then plan, notification ask; editable in Settings | R-005, R-007 (permission ask) | Permission denied path keeps everything else working; data survives restart |
| **S5 Generator** | Flexibility / warm-up / cool-down / desk routines; preview, swap, shuffle, save; safety filters; coverage notes | R-001 | Rule tests: caps, warm-up ≤30 s per muscle, each limitation excludes the right stretches, determinism, empty-catalog honesty |
| **S6 Progress & reminders** | Weekly rings, minutes, week streaks, history list; daily reminder at plan time, reboot reschedule, re-ask flow | R-006, R-007 | Week-boundary and time-zone tests; streak tests; reminder fires on emulator and phone |
| **S7 Backup/restore** | Versioned JSON export/import with validation and confirmed replacement | R-009 | Round-trip test; corrupt/newer-version file refused without data loss |
| S8 Strengthen + Foam rolling | Strengthen block (P-5); rolling exercise type with pass pacing; equipment gating; Tender spot flow with body map | R-010, R-011 | Safety exclusions for rolling; pacing timing tests |
| S9 ROM self-checks | Guided self-tests (e.g., toe reach, shoulder reach, ankle knee-to-wall), entry, trends | R-012 | "Not measured" vs zero; trend math tests |
| S10 Advanced & programs | PNF and Active-only modes; specific-target programs (splits, pancake, overhead) with milestones; hold/target overrides; Runner/Lifter profiles tuned | R-013 | PNF phase timing; program progression tests |
| S11 Catalog expansion | 60–100 stretches | R-003 | Same integrity tests |
| Later | Locked-screen running (foreground service), desk-break nudges, CLEAN Lift cool-down link, Play Store prep | D-011 | Separate approvals |

### Acceptance examples (first release)
- **Hands-free:** start a 6-stretch routine, put the phone on the floor and
  touch nothing. Every stretch, side switch and the finish are announced; the
  session appears in history with correct minutes.
- **Interruption:** lock the phone mid-hold (screen off), reopen. The app offers
  Resume or Finish & save; nothing is lost or double-counted.
- **Limitation:** set "no floor" and "painful low back." Generated routines
  contain only standing/chair stretches and none that load the low back; a note
  explains any uncovered muscle.
- **Weekly target:** with a 10-min hamstring target and 5 planned days,
  Monday's routine gives hamstrings about 2 min; after Monday and Tuesday are
  done, Wednesday's adjusts to what's left; it never exceeds 4 min.
- **Warm-up rule:** a Lifter warm-up never schedules more than 30 s of static
  stretching per muscle.
- **Notifications declined:** every feature except reminders works; setting a
  reminder explains why and offers the system prompt again.
- **Streaks:** missing a Wednesday in a week that still reaches the goal keeps
  the week streak; no "you broke your streak" message ever appears.
- **Backup:** export, clear test data on an emulator, import. History, profile
  and routines match exactly.

---

## 6. Verification approach
- **JVM unit tests:** runner state machine, generator rules, safety filters,
  dose math, week/streak math, backup codec, catalog integrity.
- **Room migration tests** once a second schema exists (Lift practice).
- **Emulator UI checks** with isolated test data, using an emulator-only script
  like Words' `test-emulator.ps1`. Never run against personal data.
- **Manual phone acceptance** on the owner's phone, plus the tester's: look and
  feel, voice quality, reminders under battery optimization, floor-use
  ergonomics.
- Each slice ends with a CONTINUE update: what changed, checks actually run,
  results, and pending manual checks.

## 7. Decisions needed before code
1. Approve the baseline (PRODUCT + EVIDENCE P-1…P-10 as defaults).
2. Approve dependencies (§1): Room + KSP, lifecycle-compose.
3. Approve permissions: VIBRATE, RECEIVE_BOOT_COMPLETED.
4. Confirm minSdk 26 and package `dev.clean.stretch`.
5. **Who builds:** Codex on your PC (can run Gradle and the emulator), or this
   Claude session (can write files into the folder but cannot build or run
   them here), or both with clear handoffs.
6. Git: local repo now; a GitHub repo like `CLEAN_Words` if you want pushes.
7. Name the first slice to authorize (recommended: **S0 + S1** together, so the
   riskiest piece — the figure — is judged early).

## 8. Next stage plan (written 2026-11-13)

**Where things stand:** S0–S9 are built, plus screen-off running (D-037), seasonal palettes
(D-038), per-muscle weekly goals with a body view (D-039) and wrist moves (D-040). The owner
said publishing is "still a bit out", so this stage is **depth and polish**, with release prep
last. Sizes: S = one short session, M = one full session, L = several sessions.

Rule for every step: follow AGENTS.md (tests, lint, emulator only, CONTINUE.md update). New
moves follow ADDING_STRETCHES.md and go into REVIEW_LOG.md as "Not reviewed".

### 8.1 Gate: owner phone pass (owner, S)
Nothing below depends on it, but its feedback reorders the list. On the phone, try:
the colours (Settings → Appearance), the Weekly goals setup page and its weekly total, the
body view on Progress, tapping one range-of-motion check, a routine with the screen locked
(voice through the speaker, notification Pause/Skip), the three wrist moves and their voice
clips, TalkBack and the largest font size.

### 8.2 Fill the empty muscles (agent + owner review, L)
Muscles with goals but no moves show grey bars. One muscle per session, in this order:

| Muscle | Candidate moves (pick 2–3) | Notes |
|---|---|---|
| Shins & ankles | Standing top-of-foot stretch (toes tucked behind), kneeling shin stretch (level 2) | Kneeling one is left out for "no kneeling" |
| Biceps | Wall biceps stretch, behind-back clasp with straight arms | Wall needs the wall prop |
| Side body | Overhead side reach (retag or new), side-lying reach | Decide whether the standing side bend's main muscle becomes side body |
| Outer hip | Standing cross-legged side lean, lying cross-body knee pull | — |
| Deep hip rotators | Seated figure-four, pigeon (level 2) | Pigeon: check knee caution wording |

Each move: pose (pose-preview, every view), cues and wording (EVIDENCE §11), sources (Editorial
plus dose sources [S2][S3]), voice clips (script now makes only missing ones), review-log row.

### 8.3 Range-of-motion checks, round 2 (agent, M)
Picture-choice checks like the current four, each tied to the muscles it reflects:
back-scratch reach (shoulders/triceps), lying knee hug ("Thomas test", hip flexors), neck turn
(neck), seated twist (side body / back), butterfly knee height (inner thigh). Keys are
permanent; trends and "not measured" work as today.

### 8.4 Body view, next round (agent, M)
- Tap a muscle → a sheet with its minutes and a **"Stretch this now"** button that builds a
  short routine for it (reuses the Tender spot builder).
- A list mode for screen readers and the largest font size (same data as the figure).
- "Last week" toggle, recomputed from history like everything else.
- Show which muscles today's suggested routine works, on the Today card.

### 8.5 Engine and figure (agent, M–L; owner check on looks)
- Multi-keyframe flows (circles, sequences) in `figure/Motion.kt` (noted since S1).
- Forearm rotation (palm up / palm down) so the wrist flexor and extensor stretches look
  different.
- Planted hands on the floor for moves that need ground contact (thread the needle is
  "Changes requested"; owner said not to fix yet, so ask first).

### 8.6 Reliability and data safety (agent; one approval, M)
- ~~Real database-upgrade tests~~ **Done 2026-11-13 (D-041):** `MigrationTest` upgrades v1 and
  v5 databases to the current version on the emulator; no new dependency was needed.
- Reminder after a phone restart needs `RECEIVE_BOOT_COMPLETED` (**needs owner approval**);
  today the app re-arms it when opened.
- Small known issues: the finish screen says "Saved to your history" even when every hold was
  skipped; the emulator started with `-no-snapshot-save` rewinds to an old snapshot on every
  boot, so test data doesn't carry over between sessions (use it knowingly).

### 8.7 Goals tuning (agent + owner, S–M)
- Watch the weekly totals in real use; if routines feel long, consider defaulting new users
  to fewer primary areas or showing "about N min a day" next to each area as it's picked.
- Optional "Advanced" toggle to count foam rolling toward the bars (explained to owner, not
  decided).
- A small "goal met" moment on Progress when a muscle side fills (no streak pressure,
  EVIDENCE P-7).

### 8.8 Release prep (last; owner-heavy)
Health-condition clinical review (**blocks release**, D-031), wording reviews, privacy policy
URL and contact address, Data safety and health declarations, foreground-service declaration
(Play may ask for a short video), content rating, screenshots and store graphics, upload key,
`bundleRelease`. Checklist: `docs/PLAY_RELEASE.md`.

### Suggested order
8.1 (whenever the owner can) → 8.2 shins & ankles → 8.6 migration tests (if approved) → 8.2
biceps → 8.4 → 8.3 → remaining 8.2 muscles → 8.5 → 8.7 → 8.8.

### Decisions waiting on the owner
1. Which muscle gets moves next (suggested: shins & ankles).
2. Approve `RECEIVE_BOOT_COMPLETED` so reminders survive a phone restart? (optional)
