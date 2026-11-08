# Decisions

| ID | Date | Decision | Status |
| --- | --- | --- | --- |
| D-001 | 2026-11-08 | Follow the development kit: discovery before code, owner approves baseline and names the first slice. | Agreed |
| D-002 | 2026-11-08 | Android only; owner has Android Studio. Stack expected to match Words/Sensors (Kotlin, Compose, Material 3). | Platform agreed; stack proposed |
| D-003 | 2026-11-08 | CLEAN rules: no ads, trackers, subscriptions, paywalls, accounts. | Agreed |
| D-004 | 2026-11-08 | Serve all four goals: flexibility, lifting warm-up/cool-down, desk relief, specific targets. | Agreed |
| D-005 | 2026-11-08 | Routines are generated from goal/time/areas/limitations, not curated-only or user-built-only. | Agreed |
| D-006 | 2026-11-08 | Audience: owner, then wife testing, Play Store later as a separate step. | Agreed |
| D-007 | 2026-11-08 | Stretches shown by one animated, code-drawn figure; no 3D engine or animation dependency. | Agreed |
| D-008 | 2026-11-08 | Limitations cover injured areas, position limits, conditions/precautions, equipment. | Agreed |
| D-009 | 2026-11-08 | Progress = consistency metrics + optional range-of-motion self-checks. Feelings ratings and goal milestones not selected for progress. | Agreed |
| D-010 | 2026-11-08 | First prototype keeps the screen on while a routine runs; design must not block adding locked-screen running later. | Agreed |
| D-011 | 2026-11-08 | Reminders: daily reminder first. Desk-break nudges and CLEAN Lift cool-down prompt deferred. | Agreed |
| D-012 | 2026-11-08 | CLEAN Lift (C:\Users\Kayli\Desktop\CLEAN Apps) is a read-only reference; not moved or edited. | Agreed |
| D-013 | 2026-11-08 | First release = core loop (see PRODUCT). ROM checks and target programs follow. | Agreed |
| D-014 | 2026-11-08 | Voice, chime and vibration cues all on by default, each switchable. | Agreed |
| D-015 | 2026-11-08 | Local versioned backup/restore like CLEAN Lift; no cloud. | Agreed |
| D-016 | 2026-11-08 | Notification permission approved for reminders only. Ask on first launch and again when setting up a reminder; app fully usable without it. | Agreed |
| D-017 | 2026-11-08 | Display name "Stretch"; folder renamed by owner from CLEAN Flexibility to CLEAN Stretch. | Agreed |
| D-018 | 2026-11-08 | Keep all evidence proposals P-1…P-10 for now; trim clutter after most features exist. Advanced options hidden by default. | Agreed |
| D-019 | 2026-11-08 | Explore foam rolling / self-massage (P-10) as part of the app. | Owner requested; proposal drafted |
| D-020 | 2026-11-08 | Owner: "follow your lead … impress me with a first draft." Treated as authorization for slices S0 + S1, with the plan's recommended defaults (minSdk 26, package dev.clean.stretch, Words-matched versions). No new dependencies or permissions were needed for S0 + S1 (Compose only; haptics via the view, no VIBRATE). Room, VIBRATE and RECEIVE_BOOT_COMPLETED still need explicit approval when S2/S6 need them. | Agreed (interpreted; confirm) |
| D-021 | 2026-11-08 | GitHub repo https://github.com/2AronB/CLEAN_Stretch ; pushes authorized without asking until Play Store release. | Agreed |
| D-022 | 2026-11-08 | Build split: Claude writes code in its cloud workspace (can compile/test pure-Kotlin figure + catalog code and render poses, but cannot download Android libraries); Android builds run on the owner's PC in Android Studio. | Agreed in practice |
| D-023 | 2026-11-08 | S1 includes a visual preview of the hands-free player (timer, figure, breath cues, haptic tick, keep-screen-on) so the owner can judge the feel. It does not save sessions; the real runner remains S2. | Proposed |
| D-024 | 2026-11-08 | Owner: "Continue through the updates – add more stretches too." Catalog grown from 6 to 16 (neck, chest, lats, glutes, inner thigh, bent-knee calf, standing/lying hamstring, sphinx, downward dog). Technique text is Editorial; sources cite the dose/consensus papers already in EVIDENCE.md, not per-exercise trials. Today's "Full-body reset" stays the original six. S2 still waits on Room + VIBRATE approval, so no runner work was done. | Agreed (owner request); stretch content needs owner review |
| D-025 | 2026-11-08 | Owner approved **Room** (with its KSP code generator, Google's `com.google.devtools.ksp` 2.3.4; Room 2.8.4) and the **VIBRATE** permission for S2. Database `stretch.db` v1 with schema export to `app/schemas`; no destructive migration fallback. TTS uses Android's built-in engine (a `<queries>` entry for the TTS service, no Internet). RECEIVE_BOOT_COMPLETED is still not approved/used. | Agreed (owner: "Approve both") |
| D-026 | 2026-11-08 | Voice cues will be **pre-generated audio clips** (natural open-source voice, likely Kokoro-82M, Apache-2.0) made on the PC and shipped in the app, played with Android's built-in audio player. No speech engine or new runtime dependency on the phone. Reason: owner tested on phone and found the system TTS voice too unnatural; users won't write their own stretches, so the phrase set is fixed. System TTS stays as a fallback for any missing clip. Voice choice still open. | Agreed in principle (owner) |
