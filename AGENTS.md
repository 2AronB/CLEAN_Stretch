# CLEAN Stretch engineering instructions

Read docs/CONTINUE.md first, then docs/PRODUCT.md, docs/DECISIONS.md,
docs/PLAN.md and docs/EVIDENCE.md (defaults, safety rules, claims tiers). Product detail belongs in PRODUCT.

Current phase: discovery. Application code is not authorized.

Use plain language; the owner is learning app development. At most three focused
questions per round. Distinguish owner requirements, recommendations,
assumptions, approvals and verified behavior.

CLEAN rules: Android only, offline, no ads/tracking/analytics/accounts/
subscriptions/paywalls. No dependencies, SDKs, permissions or services without
approval. Protect user data; never reset storage or hide failures. Stretch
guidance must cite sources and must not present itself as medical advice.
Source library: C:\Users\Kayli\Desktop\CLEAN Development\development .

All in-app health wording must follow docs/EVIDENCE.md §11 claims tiers.
History is stored as immutable session snapshots; derived stats are recomputed.
