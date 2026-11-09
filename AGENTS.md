# CLEAN Stretch engineering instructions

Read docs/CONTINUE.md first, then docs/PRODUCT.md, docs/DECISIONS.md,
docs/PLAN.md and docs/EVIDENCE.md (defaults, safety rules, claims tiers). Product detail belongs in PRODUCT.

Current phase: implementation. Application code is authorized (owner, 2026-11-08, D-027).
Work slice by slice in docs/PLAN.md order unless the owner says otherwise. The goal is an
eventual Google Play release (D-028), so build with that in mind.

Build, test and lint on the owner's PC (Android Studio's JDK), and run Android tests only on an
emulator — never the owner's phone; never clear app data. All new stretch development (poses,
cues, voice clips) is done on the PC (D-030). Follow docs/ADDING_STRETCHES.md when adding or changing a stretch.

Use plain language; the owner is learning app development. At most three focused
questions per round. Distinguish owner requirements, recommendations,
assumptions, approvals and verified behavior.

CLEAN rules: Android only, offline, no ads/tracking/analytics/accounts/
subscriptions/paywalls. No new dependencies, SDKs, permissions or services without
approval (approved so far: Room + KSP, VIBRATE — see DECISIONS). Protect user data; never
reset storage or hide failures. Stretch guidance must cite sources and must not present
itself as medical advice. Source library: C:\Users\Kayli\Desktop\CLEAN Development\development .

All in-app health wording must follow docs/EVIDENCE.md §11 claims tiers.
History is stored as immutable session snapshots; derived stats are recomputed.
Update docs/CONTINUE.md at the end of each slice or session: what changed, checks actually
run, results, and pending manual checks. Pushes to GitHub are authorized (D-021).
