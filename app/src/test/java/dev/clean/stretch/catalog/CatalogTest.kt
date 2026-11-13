package dev.clean.stretch.catalog

import dev.clean.stretch.figure.Body
import dev.clean.stretch.figure.Figure
import dev.clean.stretch.figure.MotionPlayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogTest {
    private val all = SampleCatalog.all

    @Test
    fun keysAreUniqueAndStable() {
        assertEquals(all.size, all.map { it.key }.toSet().size)
        all.forEach { assertTrue(it.key, it.key.matches(Regex("[a-z0-9_]+"))) }
    }

    @Test
    fun everyMoveLivesInItsAreaFileAndIsListedOnce() {
        val files = mapOf(
            Region.Neck to NeckMoves.all, Region.Shoulders to ShoulderMoves.all, Region.Arms to ArmMoves.all, Region.Back to BackMoves.all,
            Region.Core to CoreMoves.all, Region.Hips to HipMoves.all, Region.Legs to LegMoves.all,
        )
        files.forEach { (region, moves) ->
            moves.forEach { assertEquals("${it.key} belongs in the ${it.primary.first().region} file", region, it.primary.first().region) }
        }
        // Nothing defined in an area file is missing from the library, and nothing is listed twice.
        assertEquals(files.values.flatten().map { it.key }.sorted(), all.map { it.key }.sorted())
    }

    @Test
    fun everyStretchIsComplete() {
        all.forEach { s ->
            assertTrue("${s.key} primary", s.primary.isNotEmpty())
            assertTrue("${s.key} highlight", s.highlight.isNotEmpty())
            assertTrue("${s.key} cues", s.cues.size >= 3)
            assertTrue("${s.key} summary", s.summary.isNotBlank())
            assertTrue("${s.key} level", s.level in 1..3)
            assertTrue("${s.key} overlap", s.primary.intersect(s.secondary.toSet()).isEmpty())
        }
    }

    @Test
    fun everySourceResolves() {
        all.forEach { s ->
            assertTrue("${s.key} has sources", s.sourceIds.isNotEmpty())
            s.sourceIds.forEach { assertNotNull("${s.key} -> $it", Sources.byId(it)) }
        }
    }

    @Test
    fun wallPropsAreOnlyUsedWithWallEquipment() {
        all.filter { it.prop != null }.forEach { assertEquals(it.key, it.prop!!.equipment, it.equipment) }
        all.filter { it.kind == StretchKind.Roll }.forEach { assertEquals(it.key, Equipment.FoamRoller, it.equipment) }
    }

    @Test
    fun keyframesStayOnScreenAndOnTheFloor() {
        all.forEach { s ->
            MotionPlayer.keyframes(s.motion).forEach { pose ->
                val f = Figure.solve(pose)
                val b = f.bounds()
                assertTrue("${s.key} bottom ${b.bottom}", b.bottom in -0.01f..0.01f)
                assertTrue("${s.key} sane width ${b.width}", b.width < 260f)
                f.points.values.forEach { p -> assertTrue("${s.key} finite", p.x.isFinite() && p.y.isFinite()) }
            }
        }
    }

    @Test
    fun healthWordingAvoidsTierCClaims() {
        // docs/EVIDENCE.md §11: never claim these.
        val banned = listOf("prevent injur", "recovery", "posture", "lengthen", "release fascia", "toxin", "cure", "burn fat")
        all.forEach { s ->
            val text = (listOf(s.summary) + s.cues + s.cautions).joinToString(" ").lowercase()
            banned.forEach { assertTrue("${s.key} says '$it'", !text.contains(it)) }
        }
    }

    @Test
    fun pinnedHandsAndFeetStayPutWhileRolling() {
        all.filter { it.motion.pins.isNotEmpty() }.forEach { s ->
            val targets = dev.clean.stretch.figure.Ik.targets(s.motion.stretch, s.motion.pins)
            (0L..6000L step 250L).forEach { t ->
                val pose = MotionPlayer.holdPose(s.motion, t)
                val f = Figure.solve(pose, grounded = false)
                targets.forEach { (pin, target) ->
                    val joint = when (pin) {
                        dev.clean.stretch.figure.Pin.NearHand -> dev.clean.stretch.figure.Joint.NearHand
                        dev.clean.stretch.figure.Pin.FarHand -> dev.clean.stretch.figure.Joint.FarHand
                        dev.clean.stretch.figure.Pin.NearFoot -> dev.clean.stretch.figure.Joint.NearAnkle
                        dev.clean.stretch.figure.Pin.FarFoot -> dev.clean.stretch.figure.Joint.FarAnkle
                    }
                    val d = f[joint] - target
                    assertTrue("${s.key} $pin drifts at $t ms", d.x * d.x + d.y * d.y < 0.25f)
                }
                listOf(pose.nearKnee, pose.farKnee).forEach { assertTrue("${s.key} knee $it at $t", it in 0f..170f) }
                listOf(pose.nearElbow, pose.farElbow).forEach { assertTrue("${s.key} elbow $it at $t", it in 0f..160f) }
            }
        }
    }

    @Test
    fun jointsStayInARealRange() {
        // Knees can't fold past ~170° or hyperextend; past 180° the shin would pass through the thigh.
        all.forEach { s ->
            MotionPlayer.keyframes(s.motion).forEach { p ->
                listOf(p.nearKnee, p.farKnee).forEach { assertTrue("${s.key} knee $it", it in 0f..170f) }
                listOf(p.nearElbow, p.farElbow).forEach { assertTrue("${s.key} elbow $it", it in 0f..160f) }
            }
        }
    }

    @Test
    fun variantPairsResolveAndShareAnArea() {
        SampleCatalog.variantPairs.forEach { (easy, hard) ->
            val e = SampleCatalog.byKey(easy)
            val h = SampleCatalog.byKey(hard)
            assertNotNull(easy, e); assertNotNull(hard, h)
            assertTrue("$easy/$hard self", easy != hard)
            assertTrue("$easy/$hard area", e!!.primary.map { it.region }.intersect(h!!.primary.map { it.region }.toSet()).isNotEmpty())
            assertTrue("$easy/$hard level", e.level <= h.level)
        }
        assertEquals("seated_forward_fold", SampleCatalog.harder("standing_hamstring")?.key)
        assertEquals("lying_hamstring", SampleCatalog.easier("standing_hamstring")?.key)
    }

    @Suppress("unused")
    private val bodyLoaded = Body.TORSO
}
