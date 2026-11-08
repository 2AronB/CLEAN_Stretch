package dev.clean.stretch.figure

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class FigureTest {

    @Test
    fun standingFigureRestsOnTheFloorWithHeadOnTop() {
        val f = Figure.solve(Pose.Standing)
        val lowest = f.points.maxOf { (j, p) -> p.y + Body.radius(j) }
        assertEquals(0f, lowest, 0.001f)
        val head = f[Joint.Head]
        assertTrue("head should be highest point", f.points.values.all { it.y >= head.y })
        // Leg + torso + neck + head ≈ standing height in body units.
        val height = -(head.y - Body.HEAD_RADIUS)
        assertTrue("height $height", height in 165f..185f)
    }

    @Test
    fun directionConventionMatchesDocs() {
        assertVec(Vec(0f, 1f), Vec.dir(0f))
        assertVec(Vec(1f, 0f), Vec.dir(90f))
        assertVec(Vec(0f, -1f), Vec.dir(180f))
        assertVec(Vec(-1f, 0f), Vec.dir(-90f))
    }

    @Test
    fun segmentLengthsArePreserved() {
        val f = Figure.solve(Pose(torso = 130f, nearShoulder = 120f, nearElbow = 40f, nearHip = 70f, nearKnee = 100f, farHip = -30f))
        assertEquals(Body.TORSO, dist(f[Joint.Hip], f[Joint.Shoulder]), 0.01f)
        assertEquals(Body.UPPER_ARM, dist(f[Joint.Shoulder], f[Joint.NearElbow]), 0.01f)
        assertEquals(Body.FOREARM, dist(f[Joint.NearElbow], f[Joint.NearHand]), 0.01f)
        assertEquals(Body.THIGH, dist(f[Joint.Hip], f[Joint.NearKnee]), 0.01f)
        assertEquals(Body.SHIN, dist(f[Joint.NearKnee], f[Joint.NearAnkle]), 0.01f)
    }

    @Test
    fun lerpHitsEndpointsAndMidpoint() {
        val a = Pose(torso = 180f, nearKnee = 0f)
        val b = Pose(torso = 120f, nearKnee = 90f)
        assertEquals(a, a.lerp(b, 0f))
        assertEquals(b, a.lerp(b, 1f))
        assertEquals(150f, a.lerp(b, 0.5f).torso, 0.001f)
        assertEquals(45f, a.lerp(b, 0.5f).nearKnee, 0.001f)
    }

    @Test
    fun easingAndBreathStayInRange() {
        assertEquals(0f, Easing.inOutCubic(0f), 0.0001f)
        assertEquals(1f, Easing.inOutCubic(1f), 0.0001f)
        assertEquals(0.5f, Easing.inOutCubic(0.5f), 0.0001f)
        for (t in 0L..16000L step 250L) {
            val b = Easing.breath(t)
            assertTrue("breath $b at $t", b in 0f..1f)
        }
        assertEquals(0f, Easing.breath(0), 0.0001f)
        assertEquals(1f, Easing.breath(Timing.BREATH_MS / 2), 0.0001f)
    }

    @Test
    fun demoLoopStartsAndEndsAtStartPose() {
        val m = Motion(MotionKind.Hold, Pose.Standing, Pose(torso = 120f), Pose(torso = 110f))
        assertEquals(m.start, MotionPlayer.demoPose(m, 0, reducedMotion = false))
        assertEquals(m.start, MotionPlayer.demoPose(m, Timing.DEMO_LOOP_MS - 1, reducedMotion = false))
        assertEquals(m.stretch, MotionPlayer.demoPose(m, Timing.ENTER_MS, reducedMotion = false))
    }

    @Test
    fun reducedMotionShowsStillStretchPose() {
        val m = Motion(MotionKind.Flow, Pose.Standing, Pose(torso = 120f), Pose(torso = 110f))
        for (t in listOf(0L, 900L, 5000L, 99999L)) {
            assertEquals(m.stretch, MotionPlayer.demoPose(m, t, reducedMotion = true))
            assertEquals(m.stretch, MotionPlayer.sessionPose(m, t, reducedMotion = true))
        }
    }

    @Test
    fun flowAlternatesBetweenItsTwoPositions() {
        val m = Motion(MotionKind.Flow, Pose.Standing, Pose(spine = -10f), Pose(spine = 10f))
        assertEquals(-10f, MotionPlayer.holdPose(m, 0).spine, 0.001f)
        assertEquals(10f, MotionPlayer.holdPose(m, Timing.FLOW_HALF_MS).spine, 0.001f)
        assertEquals(-10f, MotionPlayer.holdPose(m, Timing.FLOW_HALF_MS * 2).spine, 0.001f)
    }

    @Test
    fun poseStudioCopyOmitsDefaultsAndRoundTripsValues() {
        assertEquals("Pose()", Pose().toKotlin())
        assertEquals("Pose(torso = 120f, nearKnee = 45.5f)", Pose(torso = 120f, nearKnee = 45.5f).toKotlin())
        val p = Pose().with(PoseParam.NearHip, 33f)
        assertEquals(33f, p[PoseParam.NearHip], 0f)
    }

    private fun dist(a: Vec, b: Vec): Float { val d = a - b; return kotlin.math.sqrt(d.x * d.x + d.y * d.y) }
    private fun assertVec(e: Vec, a: Vec) { assertTrue("$e vs $a", abs(e.x - a.x) < 1e-4f && abs(e.y - a.y) < 1e-4f) }
}
