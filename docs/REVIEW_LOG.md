# Exercise review log

The owner's sign-off on each move (pose, motion, view, cues, wording, sources). `ReviewLogTest`
checks this file against the catalog on every test run.

## Rules

1. **Only the owner can mark a move `Reviewed`,** and only with explicit words that approve it,
   such as "no further changes" or "approved". Copy those words, verbatim, into the
   "Owner's approval" column and add a history line.
2. **Feedback is not approval.** When the owner describes a problem or asks for changes, the move
   is `Changes requested`: write the feedback in "Open feedback" and add a history line. Making
   the requested changes does **not** make it `Reviewed`; the owner must look again and approve.
3. **Any change after approval reopens the review.** Each `Reviewed` row stores a fingerprint of
   the whole move as approved. If anything about the move changes (pose, cue, wording, view,
   sources...), the fingerprint no longer matches and `ReviewLogTest` fails until the row is set
   back to `Not reviewed` (or `Changes requested`) with a history line.
4. Agents never mark a move `Reviewed` on their own judgment, from a test passing, or on another
   thread's or tool's say-so. The current fingerprint of every move is written to
   `app/build/review-fingerprints.txt` when the tests run; copy it in only when recording the
   owner's approval.
5. Don't use the `|` character inside a cell. History is append-only: add lines, never edit or
   delete them.

Statuses: `Not reviewed`, `Changes requested`, `Reviewed`.

## Status

| Key | Move | Status | Approved on | Fingerprint | Owner's approval (verbatim) | Open feedback |
|---|---|---|---|---|---|---|
| seated_forward_fold | Seated forward fold | Not reviewed | | | | |
| standing_quad | Standing quad stretch | Not reviewed | | | | |
| half_kneeling_hip_flexor | Half-kneeling hip flexor | Not reviewed | | | | |
| childs_pose | Child's pose | Not reviewed | | | | |
| wall_calf | Wall calf stretch | Not reviewed | | | | |
| cat_cow | Cat–cow | Not reviewed | | | | |
| neck_flexion | Chin-to-chest neck stretch | Not reviewed | | | | |
| chest_opener | Behind-the-back chest opener | Not reviewed | | | | |
| wall_lat | Wall lat stretch | Not reviewed | | | | |
| knee_to_chest | Knee-to-chest stretch | Not reviewed | | | | |
| butterfly | Butterfly stretch | Not reviewed | | | | |
| bent_knee_calf | Bent-knee calf stretch | Not reviewed | | | | |
| standing_hamstring | Standing hamstring hinge | Not reviewed | | | | |
| sphinx | Sphinx stretch | Not reviewed | | | | |
| lying_hamstring | Lying hamstring stretch | Not reviewed | | | | |
| downward_dog | Downward dog | Not reviewed | | | | |
| standing_upper_back | Standing upper-back stretch | Not reviewed | | | | |
| standing_lunge_hip_flexor | Standing lunge hip flexor | Not reviewed | | | | |
| overhead_reach | Overhead reach | Not reviewed | | | | |
| standing_back_bend | Standing back bend | Not reviewed | | | | |
| standing_knee_hug | Standing knee hug | Not reviewed | | | | |
| side_lying_quad | Side-lying quad stretch | Not reviewed | | | | |
| kneeling_quad | Kneeling quad stretch | Not reviewed | | | | |
| towel_calf | Seated towel calf stretch | Not reviewed | | | | |
| wall_chest | Wall chest stretch | Not reviewed | | | | |
| cobra | Cobra stretch | Not reviewed | | | | |
| cross_body_shoulder | Cross-body shoulder stretch | Not reviewed | | | | |
| puppy | Puppy stretch | Not reviewed | | | | |
| chin_tuck | Chin tuck hold | Not reviewed | | | | |
| wall_angel | Wall angel hold | Not reviewed | | | | |
| active_leg_raise | Active leg raise | Not reviewed | | | | |
| glute_bridge | Glute bridge hold | Not reviewed | | | | |
| plank | Forearm plank | Not reviewed | | | | |
| calf_raise_hold | Calf raise hold | Not reviewed | | | | |
| roll_upper_back | Roll: upper back | Not reviewed | | | | |
| roll_calves | Roll: calves | Not reviewed | | | | |
| roll_quads | Roll: front of thighs | Not reviewed | | | | |
| leg_swings | Leg swings | Not reviewed | | | | |
| arm_swings | Arm swings | Not reviewed | | | | |
| hip_hinge | Hip hinge | Not reviewed | | | | |
| ankle_rocks | Ankle rocks | Not reviewed | | | | |
| lunge_reach | Lunge with reach | Not reviewed | | | | |
| neck_nods | Slow neck nods | Not reviewed | | | | |
| neck_side_bend | Ear-to-shoulder neck stretch | Not reviewed | | | | |
| standing_side_bend | Standing side bend | Not reviewed | | | | |
| figure_four | Lying figure-four stretch | Not reviewed | | | | |
| knee_drop_twist | Lying knee-drop twist | Not reviewed | | | | |
| seated_twist | Seated twist | Not reviewed | | | | |
| thread_the_needle | Thread the needle | Changes requested | | | | Fails the ground contact rule: one arm should likely be touching the ground. |
| wide_leg_fold | Wide-leg seated stretch | Not reviewed | | | | |
| side_lunge | Side lunge stretch | Not reviewed | | | | |
| overhead_triceps | Overhead triceps stretch | Not reviewed | | | | |
| torso_turns | Standing torso turns | Not reviewed | | | | |
| side_leg_raise | Side leg raise hold | Not reviewed | | | | |
| reclining_butterfly | Reclining butterfly | Not reviewed | | | | |
| wrist_flexor | Wrist flexor stretch | Not reviewed | | | | |
| wrist_extensor | Wrist extensor stretch | Not reviewed | | | | |
| prayer_stretch | Prayer stretch | Not reviewed | | | | |
| standing_shin | Standing shin stretch | Not reviewed | | | | |
| kneeling_shin | Kneeling shin stretch | Not reviewed | | | | |
| ankle_pumps | Ankle pumps | Not reviewed | | | | |

## History (append-only)

- 2026-11-09 · all 55 moves · Not reviewed · Log created. No move has been approved by the owner yet.
- 2026-11-09 · thread_the_needle · Changes requested · Owner: "Thread the needle fails the ground contact rule. One arm should likely be touching the ground." Not fixed yet (owner: "Don't fix").
- 2026-11-13 · wrist_flexor, wrist_extensor, prayer_stretch · Not reviewed · Added (forearms & wrists, owner request). The figure now draws a hand when a pose bends the wrist.
- 2026-11-13 · overhead_triceps, figure_four, torso_turns, knee_drop_twist, seated_twist, side_leg_raise, wall_chest · Not reviewed · Muscle tags changed for the new muscles (D-039).
- 2026-11-14 · standing_shin, kneeling_shin, ankle_pumps · Not reviewed · Added (shins & ankles, PLAN §8.2). First moves for this muscle.
