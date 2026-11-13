# Adding a stretch (or any move)

Checklist for adding a stretch, flowing move, strengthen hold or foam-rolling move. Every step
is checked by a unit test, so a missed step fails `:app:testDebugUnitTest` rather than slipping
through. All stretch work happens on the PC (D-030).

## 1. Decide what it is

| Kind (`StretchKind`) | Use for | Motion (`MotionKind`) | Time in a routine |
|---|---|---|---|
| `Static` | Classic hold | `Hold` (eases into `stretch`, deepens toward `deep` on each breath out) | Routine hold: 30 / 40 / 60 s |
| `Dynamic` | Back-and-forth warm-up moves | `Flow` (swings between `stretch` and `deep`, ~2.6 s each way) | Routine hold |
| `Strengthen` | Timed strength/control hold (P-5) | `Hold` | 30 s |
| `Roll` | Foam rolling (P-10), `equipment = FoamRoller` | `Flow`, body slides via `shiftX`; plant hands/feet with `pins` | 60 s |

`Flow` has only two end points. Circles or multi-step sequences need an engine change
(a keyframe list in `figure/Motion.kt`); talk to the owner first.

## 2. Write the catalog entry (`catalog/<Area>Moves.kt`)

Each move lives in the file for the body area of its **first** main muscle: `NeckMoves.kt`,
`ShoulderMoves.kt` (shoulders & chest), `ArmMoves.kt` (biceps, triceps, forearms & wrists), `BackMoves.kt` (upper back, lower back, lats),
`CoreMoves.kt`, `HipMoves.kt` (hip flexors, glutes, inner thigh) or `LegMoves.kt` (quads,
hamstrings, calves). Separate files let several people (or agents) add moves at the same time
without editing the same file. Copy a similar entry in that file and fill in every field:

- **`key`**: lowercase with underscores, **permanent**. History, saved routines, backups and
  voice clips refer to it. Never rename or reuse a key.
- **`name`**: what the voice says ("Wall calf stretch"). Changing it means new voice clips (step 6).
- **`sided`**: true if done one side at a time (the player runs both sides).
- **`primary` / `secondary`**: `Muscle`s. Primary areas drive weekly minutes, filters and the
  generator. They must not overlap.
- **`position`**: Standing / Seated / Kneeling / HandsAndKnees / Lying. This drives the "no floor",
  "no kneeling" and pregnancy rules, and the standing → floor order in routines.
- **`equipment`**: None / Wall / Strap / FoamRoller. A wall or roller also needs a `prop`.
- **`level`**: 1 = easy or supported, 2 = harder. Pregnancy and hypermobility only get level 1.
- **`spinalFlexion = true`** if it rounds the spine, held or repeated. It's left out for osteoporosis.
- **`spinalTwist = true`** if it twists the spine, held or repeated. Also left out for osteoporosis.
- **`highlight`**: body segments that glow (the target muscles).
- **`summary`** (one sentence), **`cues`** (at least 3, spoken-style, plain language),
  **`cautions`** (optional; the "stop for sharp pain…" line is added automatically).
- **`sourceIds`**: at least one ID that exists in `catalog/Sources.kt` (IDs match
  `docs/EVIDENCE.md`). Copy citation text from EVIDENCE.md; never type citations from memory.
- Add the new value to that file's **`all`** list, and to the end of **`SampleCatalog.all`**
  (library order; new moves go last). `CatalogTest` fails if a move is in the wrong area file
  or missing from either list.

**Wording rules (EVIDENCE §11):** describe what people do or feel ("feel the front of your
thigh", "feel looser"). Never claim it prevents injury, speeds recovery, fixes posture,
"lengthens" muscles, releases fascia or knots, or treats any condition. `CatalogTest` scans for
banned phrases.

## 3. Pose it

- Poses are `figure/Pose.kt` values: angles in degrees, `0 = down`, `90 = forward`,
  `180 = up`, negative = behind. Knees bend backward (shin = thigh − knee).
- Hard limits (`CatalogTest.jointsStayInARealRange`): **knees 0–170°, elbows 0–160°**. Past
  180° the shin passes through the thigh.
- The lowest point is placed on the floor automatically. Check that feet, hands or the body that
  should touch the floor actually do.
- **Planted hands or feet while the body moves** (rolling, flows): `pins = setOf(Pin.NearHand, …)`.
  The IK keeps them still; `CatalogTest` checks they don't drift.
- Tune with **Settings → Pose Studio** (it has sliders and "Copy whole motion") or the preview tool (step 7).

### 3D poses and camera views

The figure is 3D (D-034). Every pose value above moves the body forward/back in the side view;
these **sideways values** (all default 0, so old poses never change) move it across the body.
Directions are given for the **near** side (the side facing you in the Side view); "far" values
mirror them.

| Value | What it does | Sensible range | Example |
|---|---|---|---|
| `sideBend` | Torso leans toward the near side (+) or far side (−) | −30…30 | Standing side bend −20 to −26 |
| `twist` | Chest turns toward the near side (+) or far side (−) around the spine | −45…45 | Seated twist 32–40; torso turns ±35 |
| `headTilt` | Ear toward the near shoulder (+) or far shoulder (−) | −40…40 | Ear-to-shoulder −32 to −38 |
| `nearArmOut` / `farArmOut` | Arm swings out to its own side (+) or across the chest (−) | −70…135 | Cross-body −55; arms out in a T 75–90 |
| `nearArmTurn` / `farArmTurn` | Turns the arm at the shoulder, which changes which way the elbow bends. With the arm out to the side, +90 bends the forearm **up** | −90…90 | Wall angel goalposts: out 90, turn 90, elbow 90 |
| `nearLegOut` / `farLegOut` | Leg swings out to its own side (+) or across (−) | −50…65 | Side leg raise 28; knee-drop twist +45 / −40 |
| `nearLegTurn` / `farLegTurn` | Turns the leg at the hip; + turns the knee out so a bent knee brings the foot **in** | −40…70 | Butterfly: leg out 55 + leg turn 60, knee 140 |
| `roll` | Turns the whole body about its long axis through the hip; +90 lies on the far side with the near side up | −90…90 | Side-lying quad: roll 90, seen from Above |

Tips:
- Twists and side bends happen in the torso; the hips stay square. To make knees fall to one
  side, use leg out (+ on the near leg, − on the far leg), as in the knee-drop twist.
- A stretch that **twists the spine** (held or repeated) sets `spinalTwist = true`. Like
  `spinalFlexion`, it's left out for osteoporosis (EVIDENCE §9). `GeneratorTest` checks it.
- Pins (IK) only know forward/back movement. Don't pin a hand or foot on a limb that also
  uses sideways values.

**Starting view** (`Motion.view`). People can always turn the view with the chip on the detail
and player screens; this only picks the first view.

| View | Use it when | Examples |
|---|---|---|
| `Side` (default) | The move is forward/back: hinges, lunges, folds, bridges, cat-cow | Most stretches |
| `Angled` | Limbs would hide each other in the Side view, or a prop needs to look 3D (rollers read as cylinders) | Butterfly, figure-four, seated twist, side lunge, foam rolling |
| `Front` | The move is sideways: side bends, arms across or out, head tilts, legs out | Cross-body, side bend, ear-to-shoulder, wall angel, reclining butterfly |
| `Above` | The body lies on its side, so the side profile faces up | Side-lying quad |

**Check it** with `tools/pose-preview`: each image shows every keyframe from all four views,
the starting view first (marked `*`). It draws with the app's own `FigureDrawing`, so what you
see is what the app shows. Pose Studio also has a view picker and sliders for every value.

## 4. Link easier and harder versions

If an easier or harder version of the same area exists, add a pair to
`SampleCatalog.variantPairs` (`"easier_key" to "harder_key"`). They must share a main area and the
level can't go down (`CatalogTest`). These drive "Other versions" on the detail screen and the
Easier/Harder buttons.

## 5. Generator and filters: nothing to do

The generator, library filters, search, Tender spot and safety rules all read the fields above.
Check that the fields are right rather than adding special cases. If search should understand a
new everyday word, add it to `SearchWords.synonyms` in `plan/LibraryFilter.kt`.

## 6. Voice clips

1. Run the tests once. `VoiceLinesTest` fails and writes the new phrase list to
   `app/build/voice-phrases.txt`.
2. Copy it over `tools/voice/phrases.txt`.
3. Generate the clips (af_heart, D-026):
   `<kokoro venv>/Scripts/python.exe -I tools/voice/generate.py <kokoro model dir>`
   (the tools are in `C:\Users\Kayli\Desktop\CLEAN Development\kokoro`). Only missing clips are made;
   add `--all` to remake every clip (they'd all need listening to again).
4. Run the tests again. They should pass.

## 7. Check it

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

- Look at every keyframe from every view with `tools/pose-preview`. On Windows, from the repo
  root: `powershell -ExecutionPolicy Bypass -File tools\pose-preview\build-and-run.ps1 <key> ...`
  (no keys = every stretch). Images land in `tools/pose-preview/out/`.
- Optional: the emulator (one at a time on this PC; never the owner's phone).
- Add the move to `docs/REVIEW_LOG.md` as `Not reviewed` (`ReviewLogTest` fails until you do).
  Changing a move the owner already approved reopens its review: set it back to `Not reviewed`
  or `Changes requested` with a history line. **Only the owner marks a move `Reviewed`**, with
  explicit approval such as "no further changes"; see the rules at the top of the log.
- Add a line to `docs/CONTINUE.md`: what was added, which checks ran, what still needs the
  owner's eyes.
