# Google Play release (D-028) — checklist and drafts

**Status: drafts for the owner to review.** Play Console forms and policies change. Before
submitting, check every answer below against the live Play Console questions and current Google
Play policy pages. Nothing here is legal advice.

## 1. Checklist

| Item | Status |
|---|---|
| Release build with code/resource shrinking | Done (`assembleRelease`, 4.2 MB APK) |
| Shrunk build tested on a device (`assembleStaging`, separate app) | Done on emulator 2026-11-09: setup, routine, player (3D), history, delete flow |
| Upload key created and backed up (owner) | **To do** — see `docs/CONTINUE.md` |
| App Bundle built (`bundleRelease`) and signed | To do (after the key) |
| Health-condition wording and limits: final review + current clinical source (D-031, EVIDENCE §9) | **Blocks release** |
| Catalog wording review (owner) | To do |
| Accessibility pass (screen reader, large text, touch targets) | Code audit done (icon buttons labelled, pause button labelled); **TalkBack + largest font size on a device to do** |
| Dependency and SDK updates (lint warnings) | To do (after 3D work merges) |
| Privacy policy published at a public URL | To do (draft below) |
| Data safety form | Draft below |
| Health apps declaration | Draft below |
| Store listing text, screenshots, icon, feature graphic | Draft text below; images to do |
| Content rating questionnaire | To do (expect "Everyone") |
| Target audience: adults (not designed for children) | To confirm |
| Owner acceptance run on phone (PLAN §5 examples) | To do |

## 2. Facts the forms will ask about (verified from the code)

- **Permissions:** `VIBRATE` (buzz between steps) and `POST_NOTIFICATIONS` (daily reminder,
  asked only when the user turns it on). **No `INTERNET` permission**, so the app cannot send
  anything anywhere.
- **No accounts, ads, analytics, crash reporting or third-party SDKs** that collect data.
  Libraries: AndroidX/Compose and Room (local database).
- **Data kept on the device only:** session history, profile answers (goal, focus areas, areas
  to avoid, health-condition yes/no answers, equipment, routine length), range-of-motion check
  answers, saved routines, settings. Android auto-backup is turned off. Users can export a
  backup file themselves; it goes wherever they choose and the app never uploads it.
- **Deleting data:** Settings → Your data → **Delete my data** (confirm + "can't be undone"
  tick) erases everything; uninstalling or clearing storage does too.

## 3. Privacy policy (draft)

> **Stretch privacy policy**
>
> Stretch works entirely on your phone. It has no internet permission, no accounts, no ads and
> no analytics, so it cannot send your information anywhere.
>
> **What Stretch stores, on your phone only:** your stretching history, the answers you give
> in setup (your goal, focus areas, areas to avoid, whether certain health conditions apply,
> equipment you have, and your usual routine length), your range-of-motion check answers, your
> saved routines and your settings.
>
> **What Stretch shares:** nothing. No one, including the developer, can see this information.
>
> **Backups:** Stretch does not use Android's automatic cloud backup. If you choose "Back up to
> a file", Stretch writes a file to the place you pick; what happens to that file afterwards is
> up to you.
>
> **Permissions:** vibration (a short buzz between steps) and notifications (only if you turn on
> the daily reminder).
>
> **Deleting your information:** use Settings → Your data → Delete my data, or uninstall Stretch.
>
> **Health information:** Stretch is not medical advice and is not a medical device. Answers
> about health conditions are used only on your phone, to leave out moves that may not suit
> you.
>
> **Contact:** [owner to add a contact address]. Last updated: [date].

## 4. Data safety form (draft answers)

- Does the app collect or share any of the required user data types? **No.** Data that stays on
  the device and is never sent off it doesn't count as "collected" in Play's definitions.
  **Verify this definition in the current form before answering.**
- Is all user data encrypted in transit? Not applicable (nothing is transmitted).
- Can users request deletion? Data never leaves the device; uninstalling deletes it.

## 5. Health apps declaration (draft notes)

- Category: general fitness / wellness (stretching and flexibility routines).
- Not a medical device; it doesn't diagnose, treat or monitor any condition. The app says "not
  medical advice" in Settings, on Sources and in Tender spot.
- Health-related inputs: optional yes/no answers about conditions (pregnancy, osteoporosis,
  hypermobility, recent surgery), used only on the device to leave out moves. A "talk to your
  doctor" message is shown when one is answered yes.
- Health claims follow EVIDENCE §11 (no treatment, injury-prevention or recovery claims).

## 6. Store listing (draft)

**App name:** Stretch — offline routines

**Short description (≤ 80 characters):**
Hands-free stretching routines that fit your goals. No ads, no account.

**Full description:**
Stretch builds a short routine around your goal, the areas you want to work on and anything
you need to avoid. Then it runs it hands-free: a natural voice tells you what's next, chimes
count you down, and a simple animated figure shows each move.

- Routines for a full stretch, warm-up, cool-down or quick desk break
- Weekly goals per body area, based on published stretching research
- Easier and harder versions of stretches, plus optional strength holds and foam rolling
- Picture-based range-of-motion check-ins to see your progress
- A "sore spot" helper that builds a short, gentle routine
- Reminders at the time you choose, week streaks that never shame a missed day

Stretch is a CLEAN app: no ads, no trackers, no accounts, no subscriptions and no internet
permission. Everything stays on your phone. Sources are credited in the app. Stretch is not
medical advice.
