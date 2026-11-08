# CLEAN Stretch — evidence review and proposed defaults (DRAFT)

Researched 2026-11-08. This is a planning document: it proposes defaults for the
owner to accept, trim or reject. Nothing here is approved, and it is not medical
advice. Each answer carries a confidence label:

- **High**: several meta-analyses or a formal expert consensus agree.
- **Moderate**: one good meta-analysis, or consistent smaller trials.
- **Low**: a few small trials, mixed results, or guidance from non-research sources.
- **Editorial**: our own reasoning from anatomy or design, not tested.

Sources are numbered [S1]… and listed at the end.

---

## 0. One-page summary

| Question | Short answer | Confidence |
|---|---|---|
| "5 minutes per muscle per week"? | Real. One meta-analysis found gains when weekly stretching per muscle reached ≥5 min, with ≥5 days/week helping [S1]. A larger 2024 meta-analysis found gains keep rising to **~10 min/week per muscle**, then plateau [S2]. | Moderate–High |
| Minimum hold? | **30 s** works for most adults; 15 s is weaker [S6]. Adults 65+ did better with **60 s** [S7]. | Moderate |
| How much is too much? | For flexibility, more than **~4 min per muscle per session** or **~10 min per muscle per week** added nothing [S2]. Before maximal or explosive effort, keep static stretching under **60 s per muscle** [S4][S5]. Mild discomfort works as well as hard stretching, so pain is never needed [S2][S3]. | Moderate–High |
| Best hold pattern? | Total time matters more than how you split it [S1][S2]. 1×30 s was as good as 3×60 s per day in one trial [S6]. Expert consensus: 2–3 sets of 30–120 s, ideally daily [S3]. | Moderate |
| Best stretch per muscle? | No single "best" stretch. Hamstrings improved across many techniques, positions and durations [S12]; standing and lying versions worked equally [S13]. Choose stretches people can relax into, which reach every joint the muscle crosses. | Moderate (+Editorial) |
| Static vs dynamic vs PNF? | For long-term range, **static and PNF beat dynamic/ballistic** [S8][S3]. For warm-ups, dynamic stretching, plus short static holds if wanted, doesn't hurt performance [S3]. | High |
| Active vs passive? | Both work. Small trials disagree on which gains more [S16][S17]. Active stretching builds control you can use without help; passive is easier and more relaxing. | Low |
| Stabilizer/strength moves? | **Yes, as an option.** Strength training improves range about as much as stretching [S9]. Eccentric training improves flexibility [S10]. Strength work prevents injuries and stretching does not [S11]. Strengthening, not stretching, is advised for posture [S3] and office neck pain [S19]. | High |
| Different goals (runner/lifter/office)? | Yes. The dose science is the same; targets, timing and add-ons differ (§7). | Moderate |
| Sticking with it? | Plan when and where you'll stretch (if-then plans) [S22]. Expect about 2 months for a habit to form; missing one day doesn't hurt [S23]. Track progress and give feedback [S24]. Avoid daily streaks that punish one missed day [S25]. | Moderate |

**What we may honestly claim in the app:** stretching improves range of motion
and lowers muscle stiffness [S3].
**What we must not claim:** that stretching prevents injury, speeds recovery or
reduces soreness, fixes posture, or meaningfully builds muscle [S3][S11][S14].

---

## 1. The "5 minutes per week" claim

- **Thomas et al. 2018** (Int J Sports Med; 23 studies) [S1]. Weekly time spent
  stretching mattered, once it reached at least 5 min per muscle per week.
  Time within a single session did not matter on its own. Stretching more days
  per week helped. Their suggested minimum: static stretching, **≥5 days/week,
  ≥5 min/week per muscle**.
- **Ingram et al. 2024** (Sports Med; 189 studies, 6,654 adults) [S2]. This is
  the largest dose study. Gains peaked at about **4 min per muscle per session**
  and **10 min per muscle per week**; more time added nothing measurable.
  Intensity, age, sex, training status, weekly frequency and program length did
  not change the result. People who started less flexible improved most.
- **Konrad et al. 2024** (77 studies) [S8]. It found no clear role for volume,
  intensity or frequency. This conflicts with Thomas, because the two reviews
  used different methods and included different studies.
- **Expert consensus (Warneke et al. 2025, 20 researchers, ≥80% agreement)** [S3]:
  stretch each muscle in 2–3 sets of 30–120 s, every day, to build up weekly
  time.
- **ACSM** [S15]: stretch at least 2–3 days/week, daily is most effective; build
  up about 60 s per muscle per session.

**Takeaway:** 5 min/week is a good *minimum*; **10 min/week per focus muscle**
is where gains appear to max out. These figures are **per muscle group**, so a
6-muscle routine at 10 min each is about 60 min/week (about 12 min on 5 days),
which is reasonable.

**App proposal (P-1):** each focus muscle has a weekly target, defaulting to
10 min. The generator sets holds per session from (weekly target ÷ planned
days), capped at 4 min per muscle per session. A ring for each muscle shows
progress toward the week's target. Users can set 5 / 10 / 15 min under Advanced.

## 2. Minimum stretch, hold length and pattern

- 30 s and 60 s holds worked equally for hamstrings in adults aged 21–39. Going
  from 1 to 3 stretches a day added nothing [S6].
- In adults 65+ (average age about 85), 60 s gave faster, longer-lasting gains
  than 30 or 15 s; 15 s was the weakest [S7].
- A 2021 trial in 60–65-year-olds found 30 s worked best, and suggested 60 s
  holds strained the nerves (Low confidence; one trial).
- ACSM: hold 10–30 s for most adults, 30–60 s for older adults, 2–4 repetitions
  [S15].

**App proposal (P-2):**

| User | Default hold | Range in Advanced |
|---|---|---|
| Adults | 40 s | 30–90 s |
| 65+ (optional age in setup) | 60 s | 30–90 s |
| Warm-up static holds | 20–30 s | max 30 s |

Each stretch repeats until the session's time for that muscle is used up.
Sides alternate (L→R→L→R) so neither side waits long. The runner adds a 5 s
"get into position" period, which doesn't count as stretching time.

## 3. How much is too much

- **For flexibility gains:** nothing extra beyond about 4 min per muscle per
  session or about 10 min per week [S2]. More time costs effort for no measured
  benefit.
- **Before strength or explosive work:** in one review, ≥60 s of static
  stretching per muscle cut immediate performance by about 4.6%; <60 s cut it
  about 1.1% [S4]. Short holds inside a full warm-up have trivial effects [S5].
  The consensus advises against >60 s per muscle before maximal or explosive
  efforts [S3].
- **Intensity:** stretching hard and stretching gently gave the same range
  gains [S2][S3]. The consensus defines intensity by feel: a stretch sensation
  or mild discomfort [S3]. ACSM: stretch to tightness or slight discomfort
  [S15].
- **Very high doses** (≥15 min per muscle per session, 5 days a week, 6+ weeks)
  give small strength or muscle gains. That is poor value compared with lifting
  [S3].
- **Special cases:** see §9 for hypermobility, pregnancy and osteoporosis.

**App proposal (P-3):**
- On-screen cue: "Ease in to mild tension — about 3–5 out of 10. Never pain."
- A per-muscle cap of 4 min in each session.
- Warm-up routines never schedule more than 30 s of static stretching per
  muscle.
- No "push deeper" prompts.

## 4. Why range improves, and what that means for the app

Most of the gain comes from tolerating the stretch sensation better, plus a
small drop in stiffness. The muscle fibres themselves don't get longer
[S18][S26]. Gains fade after stopping. After 2–6 weeks off, some benefit
remains; people who kept active held on to more [S27].

**Implications:**
- **Consistency matters most.** This supports a design built around weekly
  goals.
- **Range-of-motion checks are real.** Measured improvements reflect genuine
  usable range, so the planned self-tests are worth having.
- **Be honest in the copy.** Don't say we "lengthen" muscles; say we "build
  usable range."
- **Maintenance:** after a goal is reached, about half the weekly target is
  probably enough to hold it. This is Editorial: no source sets a maintenance
  dose.

## 5. Best stretch per muscle group

- **No single best.** A systematic review found hamstring stretching increased
  range across a variety of techniques, positions and durations [S12]. Standing
  and lying versions were equally effective [S13].
- **Choosing well (Editorial, from anatomy):**
  1. Reach every joint the muscle crosses. For example: calf with straight knee
     (gastrocnemius) *and* bent knee (soleus); hip-flexor stretch with a knee
     bend for rectus femoris.
  2. Prefer **supported** positions people can relax in (lying or seated over
     balance-heavy ones), because relaxation drives tolerance.
  3. For hamstrings, tilt the pelvis forward rather than rounding the back;
     earlier research found this gave greater gains (cited in [S13]).
  4. Every stretch needs at least one easier and one harder version, plus a
     standing or chair version where possible, so limitations can swap it.
- Each catalog entry records its main and secondary muscles, joints crossed,
  position (floor / kneeling / standing / chair), equipment, level, cautions
  and source. This copies CLEAN Lift's catalog approach.

## 6. Stretch types

| Type | What it is [S3] | Best use | Evidence |
|---|---|---|---|
| Static (passive / assisted) | Hold at the stretch point, using gravity, a strap, a wall or a hand | Long-term flexibility; cool-downs | Strong for range [S8][S1] |
| Static (active / unassisted) | Hold the position using the opposite muscles | Usable range; control; hypermobile users | Mixed: one trial favored active for active range and kept the gains 4 weeks after stopping [S16]; another favored passive [S17] |
| Dynamic | Controlled swings or movements near end range, no hold | Warm-ups | Weaker for long-term range [S8]; doesn't hurt performance [S3] |
| Ballistic | Faster, bouncing movements at end range | Not used by default | Weaker for long-term range [S8] |
| PNF (contract-relax) | Stretch, tense the stretched muscle lightly, relax, then stretch further | Advanced flexibility; specific targets | Strong for range, comparable to static [S8]. Hold the contraction about 3 s at ≤20% effort [S20]. ACSM allows 3–6 s at 20–75%, then a 10–30 s stretch [S15]. |

**Passive — pros and cons:**
- **Pros:** easiest to learn, relaxing, reaches the largest range, and works
  with a strap or wall.
- **Cons:** builds range you may not control yet. Gains may fade faster after
  stopping (one trial [S16]). Less suitable for hypermobile users.

**Active — pros and cons:**
- **Pros:** builds strength and control at end range, and works the opposite
  muscle at the same time.
- **Cons:** harder and more tiring. It can't reach as deep, and cramping is
  possible.

**App proposal (P-4):**
- Default sessions use static holds (mostly passive), with active holds mixed in.
- Warm-ups use dynamic stretching.
- PNF ("Contract-relax") and an "Active-only" style sit in Advanced. The runner
  voices each phase: "Gently press into the floor… 3, 2, 1… relax and ease
  deeper."

## 7. Goal profiles

The dose science is the same everywhere; the profile changes **targets,
timing, cap rules and add-ons**.

| Profile | When | Focus muscles (default; editable) | Rules | Add-ons |
|---|---|---|---|---|
| **General flexibility** | Any time; a separate session works best | Whole body or chosen areas | Weekly targets (P-1) | Optional strength/control |
| **Runner** | Before: dynamic. After or separate: static | Calves (both), hip flexors, quads, hamstrings, glutes, adductors | Before a run: dynamic plus ≤30 s static per muscle. A 2025 meta-analysis found stretching did not change running economy [S21], and a 2021 review of runners found static stretching hurt sprint and jump performance [S28]. | Calf and hip strength (strength reduced injuries; stretching didn't [S11]) |
| **Lifter** | Before: dynamic + short static. After / rest days: static | Picked by lift: ankles (squat), hips, thoracic spine/shoulders (overhead), pecs/lats | Before lifting: <60 s static per muscle [S3][S4]. Long holds belong after lifting or on separate days. | Full-range strength training already builds range [S9]; CLEAN Lift integration later |
| **Desk / stiffness** | Short breaks; a daily routine | Neck, upper back, chest, hip flexors, hamstrings, wrists | 2–5 min sessions, standing or chair | **Neck and shoulder-blade strengthening** — strongest evidence for office neck pain [S19]. Active microbreaks of 2–3 min every 30 min reduced discomfort without hurting productivity [S29] (desk nudges are deferred). |
| **Specific target** (splits, pancake, overhead) | Progressive program | Target chain | Higher weekly targets per muscle, PNF allowed, staged milestones | End-range strength for the target (Editorial) |
| **Older adult** (age option) | Daily | Whole body | 60 s holds [S7][S15]; stable positions | Balance work: ACSM recommends 2–3 days/week for older adults [S15] |

## 8. Stabilizer / strength-and-control moves — should we include them?

**Evidence for:**
- Strength training and stretching improved range equally across 11 trials [S9].
- Eccentric training improves flexibility [S10].
- Strength training cut sports injuries to under a third; stretching showed no
  benefit [S11].
- Strengthening, not stretching, is advised for posture [S3].
- Strengthening is supported for office neck pain [S19].
- Hypermobility guidance favors movement control and stabilizer strength with
  low joint load [S30].

**Evidence against / risks:**
- More app scope.
- Harder to keep hands-free, because reps need counting.
- Muddies a "stretching" app.

**Proposal (P-5): yes, as an optional "Strengthen" block.**
- Timed rather than counted: about 1–3 short holds or slow timed reps at end
  range, such as a wall angel hold, active straight-leg raise or chin tuck.
- On by default for **Desk** and when **hypermobility** is selected; off
  elsewhere, and toggled with one switch.
- Time spent here counts toward minutes and streaks, but **not** toward
  per-muscle stretch targets.

## 9. Limitations and safety rules for the generator

These are cautions, not medical advice. Each one needs owner review, and the
health-condition rules should be checked against a current clinical source
before release.

| Limitation | Proposed generator behavior | Source / confidence |
|---|---|---|
| Painful/injured area | Exclude stretches whose main *or* secondary muscles load the area; offer gentle versions of nearby stretches only | Editorial |
| No floor / no kneeling / standing-only / chair-only | Filter by position; use the standing/chair version | Editorial |
| **Hypermobility** | Favor active and supported holds; never push to end range; lower intensity cue; Strengthen on | Low–Moderate [S30] |
| **Pregnancy** | Avoid end-range and bouncing; no lying on the back after the first trimester; prefer side-lying, seated or standing; "check with your clinician" screen | Moderate for general safety [S31]. The no-lying-on-back rule comes from **older** ACOG guidance; confirm against current ACOG Committee Opinion 804 before release. The relaxin "don't overstretch" advice is Low confidence. |
| **Osteoporosis / bone-fracture risk** | Exclude full-range, held, loaded or repeated spinal flexion and twisting (e.g., seated forward fold, deep supine twist); prefer hinge-at-hips versions | Moderate [S32][S33] |
| Recent surgery / joint replacement | Exclude that region until the user removes the limitation; prompt them to follow their surgeon's precautions | Editorial; research gap |
| Older adult | 60 s holds; balance-safe positions; support nearby | Moderate [S7][S15] |

**Stop rules shown in every session (P-6):** stop for sharp pain, numbness,
tingling or shooting pain, dizziness, or pain that lasts afterward. These are
standard practice, labeled Editorial.

## 10. Getting people to stick with it

| Finding | Source | App design (P-7) |
|---|---|---|
| If-then plans ("If [cue], then I stretch") improved goal attainment, d = 0.65 in 94 tests. A larger 2024 analysis with bias correction found a smaller effect. | [S22] | Setup asks "When will you stretch?" and offers cue choices (after brushing teeth, after a workout, at a set time). The reminder repeats the user's own plan. |
| Habits took a median of 66 days to become automatic (range 18–254). One missed day didn't hurt; many misses did. | [S23] | Use a **weekly goal**, not a daily streak. Show "Week 3 of building the habit." Never shame a miss. |
| Broken streaks lower persistence; letting people repair a streak softens the effect. | [S25] | Streaks count **weeks** that hit the goal (e.g., 4 of 7 days). Missed days are never highlighted; notifications never say "you broke your streak." |
| Apps and trackers with self-monitoring and feedback modestly raise activity. Goal setting is the most common technique. | [S24] | Per-muscle weekly rings, minutes, consistency %, ROM-check trend lines. |
| Start-up effort kills habits (Editorial) | — | One tap from the reminder or home screen to a running routine; sessions as short as 3 min; hands-free runner. |

## 11. What the app may say (claims guide)

Rule: every health statement in the app must match one of the tiers below and
link to its source. Not a lawyer; this is an evidence standard, not legal review.

### Tier A — say plainly (strong evidence)
| Claim (example wording) | Sources |
|---|---|
| "Regular stretching increases your range of motion." | [S1][S2][S3][S8] |
| "Consistency matters most — about 10 minutes per muscle each week is where gains level off." | [S1][S2] |
| "Gains fade if you stop, but some stick around for weeks." | [S27] |
| "Stretching reduces muscle stiffness." | [S3] |
| "Short stretches inside a warm-up won't hurt your workout." | [S3][S5] |
| "Strength training through a full range builds flexibility too." | [S9] |
| "A few minutes of foam rolling gives a short-term boost in range without hurting performance." | [S34][S35] |

### Tier B — say with "may" / "in studies" (moderate or limited evidence)
| Claim (example wording) | Sources / caveat |
|---|---|
| "Regular stretching may support blood vessel health." | Consensus says "may, more research needed" [S3]. Meta-analysis of 8 small trials in middle-aged/older adults: lower arterial stiffness, slightly lower diastolic BP [S38]. |
| "In one workplace study, 10 minutes of daily stretching lowered anxiety and body pain." | Single RCT, 134 workers [S39]. Must say "one study." |
| "Foam rolling may ease how sore muscles feel after exercise." | Pooled effect on pain perception [S35]; small and from low-quality trials. |
| "Strengthening exercises can ease desk-related neck pain." | [S19] — attach to the Strengthen block, not to stretching. |
| "Many people find stretching relaxing." | Consensus: psychological value, personal preference [S3]. |

### Tier C — never claim
| Don't say | Why |
|---|---|
| Prevents injuries | Not supported [S3][S11] |
| Speeds recovery / prevents soreness (stretching) | [S3][S14] |
| Fixes or improves posture | [S3] |
| Builds muscle or strength (meaningfully) | [S3] |
| "Lengthens" muscles | Gains are mostly tolerance, not longer fibres [S26] |
| Foam rolling "releases fascia," "breaks up knots/adhesions," flushes toxins | Evidence for myofascial release as the mechanism is scant; trigger-point identification is unreliable [S37] |
| Treats or cures back pain or any condition | In chronic low back pain, stretching had the smallest effect among exercise types [S40]; medical claims are out of scope |
| Burns fat, detoxes, boosts immunity | No support |

Copy style: describe what people can *do* ("reach further," "feel looser"),
not what tissues do. No testimonials, before/after promises or percentages
beyond what a source states.

## 12. Proposed defaults

Owner decision 2026-11-08: **keep all proposals for now**; trim for clutter
once most features are built. Advanced options stay hidden by default.

| ID | Proposal |
|---|---|
| P-1 | Weekly target per focus muscle (default 10 min; 5/10/15 in Advanced); cap of 4 min per muscle per session |
| P-2 | 40 s default hold (60 s for 65+); alternate sides; 5 s setup not counted |
| P-3 | "Mild tension 3–5/10, never pain" cue; warm-up static holds ≤30 s per muscle |
| P-4 | Static by default, mixing passive and active holds; dynamic for warm-ups; PNF and Active-only in Advanced |
| P-5 | Optional timed Strengthen block; on for Desk and hypermobility |
| P-6 | Safety rules (§9) and stop rules in every session |
| P-7 | If-then plan at setup; weekly-goal streaks; no broken-streak messages |
| P-8 | Goal profiles: General, Runner, Lifter, Desk, Specific target; age option for 65+ |
| P-9 | In-app claims limited to the §11 tiers, with an offline Sources screen |
| P-10 | Foam rolling / self-massage as an equipment-gated exercise type and a "Tender spot" flow (§13) |

## 13. Foam rolling and self-massage

**Why include it:** owner wants relief for sore spots that are hard to stretch
(e.g., upper back, outer thigh, feet). Rolling fits the hands-free runner well
because it is timed and paced.

**What the evidence says**
- Acute range: a meta-analysis of 13 range-of-motion studies found a large
  effect, with rolling increasing range in every study; no harm to other
  performance measures [S34]. Gains last roughly 20–30 minutes [S36][S41].
- Rolling vs stretching: similar acute range gains [S42], and no difference in
  long-term range gains [S41].
- Long-term: 11 training studies (mean age ~24) found a moderate range increase
  for hamstrings and quadriceps, but **not** ankle dorsiflexion when rolling the
  calves [S43].
- Performance and recovery: pre-rolling gave small sprint (+0.7%) and
  flexibility (+4%) gains; post-rolling reduced muscle pain perception (+6%,
  g = 0.47). Authors judged effects minor overall and better suited to warm-ups
  than recovery [S35]. Critics note the underlying trials are small and
  inconsistent (Low–Moderate confidence).
- Mechanism: likely mostly nervous-system (pain/tolerance) rather than
  "releasing" tissue — rolling one leg can increase range in the other leg
  [S36][S37]. This is why rolling a sore spot can feel better even when the
  tissue itself hasn't changed.

**Dosing (from a prescription commentary [S36])**
- 30–120 s per area per set; 1–3 sets (5+ sets predicted trivial effects).
- 2–4 s per pass in one direction (1 s passes were less effective).
- Total ~1–6 min for meaningful range gains.
- No optimal pressure identified; intensity did not change range outcomes.
- Moderate/hard rollers give more consistent pressure.

**Safety [S44] (expert consensus) + Editorial**
- Do not roll: open wounds, bone fractures (consensus contraindications).
- Caution / avoid area: local inflammation, deep vein thrombosis (consensus
  cautions); osteoporosis — avoid spine and bony points; blood thinners — gentle
  pressure; large varicose veins — avoid (Low confidence; industry sources).
- Editorial: never roll the front/side of the neck, directly on the lower spine,
  over joints or bony points; stop for numbness, tingling or sharp pain.
- A spot that stays painful for weeks, or pain at rest/night, gets a "see a
  clinician" note rather than more rolling.

**App proposal (P-10)**
1. **Equipment-gated exercise type.** Add foam roller, massage ball and roller
   stick to equipment. Rolling exercises appear only if the user owns one.
2. **Paced runner.** Voice/haptic tick each pass ("roll up… and down") at
   3 s per pass; default 60 s × 2 sets per area; side switching automatic.
3. **Placement.** Optional at the start of warm-ups (no performance cost) and in
   cool-downs (feel-good, Tier B soreness wording only).
4. **"Tender spot" flow.** Tap a sore area on the body map → app builds a
   3–6 min mini-routine: roll the area, then a stretch for the same region,
   then (if available) a Strengthen move. Uses all limitation/safety filters.
5. **Tracking.** Rolling minutes are logged separately and count toward total
   minutes and the weekly goal, but not toward per-muscle *stretch* targets
   (conservative; long-term rolling evidence is thinner). Advanced toggle can
   count it.
6. **Pressure cue.** "Firm but you can breathe slowly — ease off if it's sharp."

## Known gaps and conflicts
- Thomas (frequency helps) vs Ingram/Konrad (frequency doesn't matter). Our
  weekly-target design works under either view.
- Active vs passive evidence is thin and mixed.
- No source sets a maintenance dose; P-1's maintenance step is Editorial.
- Pregnancy and joint-replacement rules need current clinical sources before
  release.
- Study participants were mostly young adults (mean age about 27) [S2], so
  results for older users rest on fewer trials [S7].

---

## Sources

- S1 Thomas E et al. 2018. The relation between stretching typology and stretching duration: the effects on range of motion. *Int J Sports Med* 39:243–254. https://pubmed.ncbi.nlm.nih.gov/29506306/
- S2 Ingram LA et al. 2024/2025. Optimising the dose of static stretching to improve flexibility. *Sports Med* 55:597–617. https://pubmed.ncbi.nlm.nih.gov/39614059/
- S3 Warneke K et al. 2025. Practical recommendations on stretching exercise: a Delphi consensus statement. *J Sport Health Sci* 14:101067. https://doi.org/10.1016/j.jshs.2025.101067
- S4 Behm DG et al. 2016. Acute effects of muscle stretching on physical performance, range of motion, and injury incidence. *Appl Physiol Nutr Metab*. https://nectar.northampton.ac.uk/id/eprint/8154/
- S5 Chaabene H et al. 2019. Acute effects of static stretching on muscle strength and power. *Front Physiol* 10:1468. https://pmc.ncbi.nlm.nih.gov/articles/PMC6895680
- S6 Bandy WD et al. 1997. The effect of time and frequency of static stretching on flexibility of the hamstring muscles. *Phys Ther* 77:1090–6. https://pubmed.ncbi.nlm.nih.gov/9327823/
- S7 Feland JB et al. 2001. Duration of hamstring stretching in people aged 65+. *Phys Ther* 81:1110–7. https://pubmed.ncbi.nlm.nih.gov/11319936/
- S8 Konrad A et al. 2024. Chronic effects of stretching on range of motion with consideration of moderating variables. *J Sport Health Sci* 13:186–194. https://pubmed.ncbi.nlm.nih.gov/37301370/
- S9 Afonso J et al. 2021. Strength training versus stretching for improving range of motion. *Healthcare* 9:427. https://doi.org/10.3390/healthcare9040427
- S10 O'Sullivan K et al. 2012. The effects of eccentric training on lower limb flexibility. *Br J Sports Med* 46:838–845. https://bjsm.bmj.com/content/46/12/838
- S11 Lauersen JB et al. 2014. Exercise interventions to prevent sports injuries. *Br J Sports Med* 48:871–877. https://bjsm.bmj.com/content/48/11/871
- S12 Decoster LC et al. 2005. The effects of hamstring stretching on range of motion: a systematic review. *JOSPT* 35:377–387. https://pubmed.ncbi.nlm.nih.gov/16001909/
- S13 Decoster LC et al. 2004. Standing and supine hamstring stretching are equally effective. *J Athl Train* 39:330–334. https://pmc.ncbi.nlm.nih.gov/articles/PMC535525
- S14 Herbert RD et al. 2011. Stretching to prevent or reduce muscle soreness after exercise. *Cochrane* CD004577. https://www.cochrane.org/ms/node/2048
- S15 ACSM / Garber CE et al. 2011 position stand, flexibility and neuromotor tables. https://www.unm.edu/~lkravitz/Article%20folder/ACSMGuidelinesUNM.pdf ; ACSM 2021 update https://www.acsm.org/blog-detail/acsm-certified-blog/2021/03/18/stretching-and-flexibility-guidelines-update
- S16 Meroni R et al. 2010. Active vs static stretching on hamstring flexibility. *Clin J Sport Med* 20:8–14. https://pubmed.ncbi.nlm.nih.gov/20051728/
- S17 Passive vs active hamstring stretching (J Phys Ther Sci, 2015). https://pmc.ncbi.nlm.nih.gov/articles/PMC4668158/
- S18 Weppler CH, Magnusson SP 2010. Increasing muscle extensibility: increasing length or modifying sensation? *Phys Ther*. https://research.regionh.dk/da/publications/increasing-muscle-extensibility-a-matter-of-increasing-length-or-/
- S19 Louw S et al. 2017. Exercise in office workers with neck pain. *S Afr J Physiother* 73:392. https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6093121/ ; Cramer H et al. 2020 *J Occup Health* summary https://myorthoevidence.com/AceReport/Report/10086
- S20 Sharman MJ et al. 2006. PNF stretching: mechanisms and clinical implications. *Sports Med* 36:929–939. https://research.usc.edu.au/esploro/outputs/journalArticle/Proprioceptive-neuromuscular-facilitation-stretching--mechanisms/99472601602621
- S21 Acute and chronic effects of stretching on running economy: systematic review with meta-analysis (2025). *Sports Med Open*. https://link.springer.com/article/10.1186/s40798-025-00859-0
- S22 Gollwitzer PM, Sheeran P 2006. Implementation intentions and goal achievement. *Adv Exp Soc Psychol*. https://kops.uni-konstanz.de/entities/publication/2e749bfb-8533-437c-8203-7e788c910c5f ; 2024 update summary https://www.thebehavioralscientist.com/glossary/implementation-intentions
- S23 Lally P et al. 2010. How are habits formed. *Eur J Soc Psychol*. https://bps.org.uk/research-digest/how-form-habit
- S24 Laranjo L et al. 2021. Do smartphone apps and activity trackers increase physical activity? *Br J Sports Med* 55:422–432. https://blogs.bmj.com/bjsm/2021/01/26/do-smartphone-applications-and-activity-trackers-increase-physical-activity-in-adults/
- S25 Silverman J, Barasch A 2023. On or off track: how (broken) streaks affect consumer decisions. *J Consum Res*. https://knowledge.insead.edu/marketing/consumer-streaks-are-motivating-key-keeping-them-alive
- S26 Ingram LA et al. 2025. Mechanisms underlying ROM improvements following static stretching (meta-analysis), summarized at https://www.painscience.com/biblio/flexibility-gains-due-to-boosted-tolerance.html
- S27 Konrad A et al. 2025. Detraining effects following chronic stretching training on ROM. *Sports Med Open* 11:141. https://iris.unipa.it/handle/10447/703451
- S28 Ullman ZJ et al. 2021. Isometric exercises vs static stretching in warm-ups for running athletes. *Int J Exerc Sci* 14:1204–1218. https://pmc.ncbi.nlm.nih.gov/articles/PMC8758156
- S29 Radwan A et al. 2022. Effects of active microbreaks on office workers. *Cogent Eng* 9. https://doaj.org/article/b3c2d7ac5b084dbfb2253dd67f51a356
- S30 The Ehlers-Danlos Society — Physical therapy. https://www.ehlers-danlos.com/physical-therapy/
- S31 ACOG Committee Opinion 804 (2020), abstract: https://read.qxmd.com/read/32217980/ ; older ACOG supine guidance summarized at https://www.ideafit.com/acog-guidelines
- S32 Osteoporosis Canada — Too Fit To Fracture. https://osteoporosis.ca/too-fit-to-fracture/ ; Giangregorio LM et al. 2015 *Osteoporos Int* https://pubmed.ncbi.nlm.nih.gov/25510579/
- S33 Brooke-Wavell K et al. 2022. Strong, Steady and Straight: UK consensus on exercise for osteoporosis. https://eprints.soton.ac.uk/456586
- S34 Skinner B et al. 2020. Foam rolling on range of motion, recovery and athletic performance: systematic review and meta-analysis. *J Bodyw Mov Ther* 24:105–122. https://pubmed.ncbi.nlm.nih.gov/32825976
- S35 Wiewelhove T et al. 2019. A meta-analysis of the effects of foam rolling on performance and recovery. *Front Physiol* 10:376. https://pubmed.ncbi.nlm.nih.gov/31024339/
- S36 Behm DG et al. 2020. Foam rolling prescription: a clinical commentary. *J Strength Cond Res* 34:3301–3308. https://paulogentil.com/pdf/Foam%20Rolling%20Prescription%20-%20A%20Clinical%20Commentary.pdf
- S37 Behm DG, Wilke J 2019. Do self-myofascial release devices release myofascia? Rolling mechanisms: a narrative review. *Sports Med*. https://paulogentil.com/pdf/Do%20Self-Myofascial%20Release%20Devices%20Release%20Myofascia.%20Rolling%20Mechanisms%20-%20A%20Narrative%20Review.pdf ; Konrad A et al. 2023 contralateral effects review https://www.ncbi.nlm.nih.gov/pmc/articles/PMC10191906/
- S38 Kato M et al. 2020. Stretching exercises and arterial stiffness in middle-aged and older adults: meta-analysis. *IJERPH* 17:5643. https://pubmed.ncbi.nlm.nih.gov/32764418/
- S39 Montero-Marín J et al. 2013. Effectiveness of a stretching program on anxiety levels of workers: RCT. *Aten Primaria* 45:376–383. https://pubmed.ncbi.nlm.nih.gov/23764394/
- S40 What modifies the effect of exercise treatment for chronic low back pain? Meta-epidemiologic regression. *JOSPT* 2022;52:792–802. https://pubmed.ncbi.nlm.nih.gov/35960505/
- S41 Konrad A et al. 2023. The contralateral effects of foam rolling on ROM and muscle performance. *Eur J Appl Physiol* 123:1167–1178. https://link.springer.com/article/10.1007/s00421-023-05142-2
- S42 Wilke J et al. 2020. Acute effects of foam rolling on ROM in healthy adults: multilevel meta-analysis (comparison summarized in Konrad 2022). https://pmc.ncbi.nlm.nih.gov/articles/PMC9197890
- S43 Konrad A et al. 2022. Foam rolling training effects on range of motion: systematic review and meta-analysis. *Sports Med*. https://www.x-mol.com/paper/1529876473204191232
- S44 Bartsch KM et al. 2021. Expert consensus on the contraindications and cautions of foam rolling: international Delphi study. *J Clin Med* 10:5360. https://www.pubmed.gov/34830642/
