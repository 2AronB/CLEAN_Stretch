# CLEAN Stretch — product baseline (DRAFT, not approved)

Status: requirements discovery started 2026-11-08. Nothing here is approved for
implementation. IDs are proposed and may change before baseline approval.

## Purpose and user
An offline Android stretching app that builds a science-informed routine and then
runs it hands-free, so the user does not have to think once they press Start.
Users: the owner first, his wife as a tester; Play Store later as a separate step.
Feel: like a stock Google/Apple app — sleek, quick, pretty.

## Name
App display name: **Stretch**. Project/folder: CLEAN Stretch (agreed 2026-11-08).

## CLEAN contract (agreed)
No ads, trackers, analytics, subscriptions, paywalls or accounts. Android only.
Works fully offline (expected: no Internet permission, as in Words/Sensors).

## Goals the app must serve (agreed 2026-11-08)
1. General flexibility — range-of-motion gains over weeks.
2. Lifting warm-up and cool-down (alongside CLEAN Lift).
3. Desk/stiffness relief — short breaks.
4. Specific targets (e.g., splits, pancake, overhead) with progressive stages.

## Proposed requirements
- R-001 Generated routines: user picks goal, time and body areas; app builds a
  balanced routine respecting limitations. Saving a routine is likely. (agreed: generated)
- R-002 Hands-free runner: once started, the routine advances itself through
  each stretch, side switch and rest. First prototype keeps the screen on while
  running (agreed 2026-11-08). The runner's timing must not live in the screen,
  so locked-screen/background running can be added later without a rewrite.
  Voice (Android's built-in offline text-to-speech), chime with 3-2-1 countdown,
  and vibration are all on by default and individually switchable (agreed).
- R-003 Stretch library with target muscles, cues, cautions and evidence notes. Size open (60–100 suggested).
- R-004 Animated figure per stretch: one consistent faceless figure drawn in code,
  moving into the pose and breathing during holds. No new dependency. (agreed)
- R-005 Limitations: painful/injured areas, position limits (floor, kneeling,
  standing/chair), conditions/precautions (e.g., pregnancy, hypermobility,
  recent surgery — cautions, not medical advice), equipment available. Used to
  avoid, swap or soften stretches. (agreed scope)
- R-006 Progress: consistency (sessions, minutes, streaks, weekly time per
  muscle group) and optional periodic range-of-motion self-checks. (agreed)
- R-007 Reminders: first release has a daily reminder at a user-chosen time
  (agreed). Desk-break nudges and a CLEAN Lift cool-down prompt are wanted
  later (deferred). Notification permission is approved as an offer, not a
  requirement: ask once on first launch; if declined, ask again (with a short
  explanation) only when the user sets up a reminder. Everything except
  reminders must work fully without it. No other permissions approved.
- R-008 Scientific basis: dosing defaults drawn from published evidence, with
  sources credited in-app. (intent; method open)

- R-009 Backup/restore: versioned local file export/import like CLEAN Lift; no
  cloud; Android auto-backup disabled. May follow shortly after the first build (agreed).

- R-010 Strengthen block: optional timed strength/control moves (EVIDENCE P-5).
- R-011 Foam rolling / self-massage and "Tender spot" flow (EVIDENCE P-10).
- R-012 Range-of-motion self-checks with trends.
- R-013 Advanced modes (PNF, Active-only, overrides) and specific-target programs.
- R-014 Claims and Sources: in-app health statements follow EVIDENCE §11 tiers;
  offline Sources/credits screen.

## First release (agreed scope 2026-11-08, details not yet approved)
Core loop: ~30 stretches; generator for general flexibility, desk relief and
lifting warm-up/cool-down; hands-free screen-on runner with voice/chime/vibration;
limitations; daily reminder; consistency tracking (sessions, minutes, streaks,
weekly time per muscle group).
Next after that: range-of-motion self-checks, specific-target programs (splits
etc.), larger library. Later: locked-screen running, desk nudges, Lift link.

## Open questions
- Pause/skip/extend behavior during a routine.
- Evidence-based defaults P-1…P-9 proposed in docs/EVIDENCE.md (2026-11-08);
  awaiting owner decision. Includes optional Strengthen block and goal profiles.
- History rules for edited or deleted routines.

## Lessons carried from CLEAN Lift (C:\Users\Kayli\Desktop\CLEAN Apps)
Reference only; Lift is unpolished and stays where it is.
- Generator style to reuse: deterministic, curated catalog with primary vs
  secondary muscle tags; honest notes when equipment/limits leave a gap instead
  of adding filler; targets are editable defaults, not personal prescriptions;
  each catalog row records its evidence/provenance.
- Data style to reuse: Room database, history snapshots that later edits never
  change, versioned local JSON backup/restore, Android auto-backup disabled.
- Gap: Lift is a set logger. Its timers only run while visible and rest is a
  hint, not a countdown. The hands-free timed runner is new work for this app.
