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

## 2. Write the catalog entry (`catalog/SampleCatalog.kt`)

Copy a similar entry and fill in every field:

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
- **`highlight`**: body segments that glow (the target muscles).
- **`summary`** (one sentence), **`cues`** (at least 3, spoken-style, plain language),
  **`cautions`** (optional; the "stop for sharp pain…" line is added automatically).
- **`sourceIds`**: at least one ID that exists in `catalog/Sources.kt` (IDs match
  `docs/EVIDENCE.md`). Copy citation text from EVIDENCE.md; never type citations from memory.
- Add the new value to **`all`** at the bottom of the file.

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

_To be written by the 3D work (D-034): sideways values (`sideBend`, `twist`, `armOut`,
`legOut`, `legTurn`) with direction and sensible ranges, when to pick each starting view
(Side / Angled / Front / Above), and how to preview them._

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
   (the tools are in `C:\Users\Kayli\Desktop\CLEAN Development\kokoro`).
4. Run the tests again. They should pass.

## 7. Check it

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

- Look at every keyframe with `tools/pose-preview` (see its README; on Windows use Gradle's
  bundled Kotlin and `;` in the classpath, and run it from PowerShell).
- Optional: the emulator (one at a time on this PC; never the owner's phone).
- Add a line to `docs/CONTINUE.md`: what was added, which checks ran, what still needs the
  owner's eyes.
