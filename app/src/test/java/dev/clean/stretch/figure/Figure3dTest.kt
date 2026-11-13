package dev.clean.stretch.figure

import dev.clean.stretch.catalog.SampleCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.max

class Figure3dTest {

    // A hand appears only when a pose bends the wrist, so every older pose draws exactly as before.
    @Test
    fun handIsDrawnOnlyWhenTheWristIsBent() {
        assertTrue(Figure3d.solve(Pose(nearShoulder = 90f)).handTips.isEmpty())
        val f = Figure3d.solve(Pose(nearShoulder = 90f, nearWrist = -70f))
        val hand = f[Joint.NearHand]
        val tip = f.handTips.getValue(Joint.NearHand)
        // Arm reaching forward, wrist -70: fingers point down (y grows downward) and a little forward.
        assertTrue("fingers down", tip.y - hand.y > 0.9f * Body.HAND)
        assertTrue("a little forward", tip.x > hand.x)
        assertEquals(Body.HAND, (tip - hand).length(), 0.01f)
        val up = Figure3d.solve(Pose(nearShoulder = 90f, nearWrist = 80f))
        assertTrue("fingers up", up.handTips.getValue(Joint.NearHand).y < up[Joint.NearHand].y)
    }

    private val sideways = listOf(PoseParam.SideBend, PoseParam.Twist, PoseParam.NearArmOut, PoseParam.FarArmOut, PoseParam.NearLegOut, PoseParam.FarLegOut, PoseParam.NearLegTurn, PoseParam.FarLegTurn, PoseParam.HeadTilt, PoseParam.NearArmTurn, PoseParam.FarArmTurn, PoseParam.Roll)

    @Test
    fun sideViewMatchesTheOriginal2dFigureForEveryCatalogPose() {
        val poses = SampleCatalog.all.flatMap { MotionPlayer.keyframes(it.motion) }.filter { p -> sideways.all { p[it] == 0f } }
        assertTrue("enough poses checked", poses.size > 50)
        poses.forEach { pose ->
            listOf(true, false).forEach { grounded ->
                val new = Figure3d.solve(pose, grounded).sideView()
                val old = Legacy2d.solve(pose, grounded)
                Joint.entries.forEach { j -> assertNear("$pose $j", old.getValue(j), new[j]) }
            }
        }
    }

    @Test
    fun limbLengthsSurviveSidewaysMovement() {
        val f = Figure3d.solve(Pose(torso = 150f, sideBend = 20f, twist = 30f, nearShoulder = 80f, nearElbow = 50f, nearArmOut = 40f, farArmOut = -30f, nearHip = 60f, nearKnee = 70f, nearLegOut = 45f, farLegOut = 20f))
        assertEquals(Body.TORSO, (f[Joint.Shoulder] - f[Joint.Hip]).length(), 0.01f)
        assertEquals(Body.UPPER_ARM, (f[Joint.NearElbow] - f.root(Segment.NearUpperArm)).length(), 0.01f)
        assertEquals(Body.FOREARM, (f[Joint.NearHand] - f[Joint.NearElbow]).length(), 0.01f)
        assertEquals(Body.THIGH, (f[Joint.NearKnee] - f.root(Segment.NearThigh)).length(), 0.01f)
        assertEquals(Body.SHIN, (f[Joint.NearAnkle] - f[Joint.NearKnee]).length(), 0.01f)
        assertEquals(Body.FOOT, (f[Joint.NearToe] - f[Joint.NearAnkle]).length(), 0.01f)
    }

    @Test
    fun sidewaysValuesMoveTowardTheNearSide() {
        fun f(p: Pose) = Figure3d.solve(p, grounded = false)
        assertTrue(f(Pose(sideBend = 20f))[Joint.Shoulder].z > 5f)
        assertTrue(f(Pose(sideBend = -20f))[Joint.Shoulder].z < -5f)
        assertTrue(f(Pose(nearArmOut = 90f))[Joint.NearHand].z > 50f)
        assertTrue(f(Pose(farArmOut = 90f))[Joint.FarHand].z < -50f)
        assertTrue("across the body", f(Pose(nearShoulder = 90f, nearArmOut = -60f))[Joint.NearHand].z < 0f)
        assertTrue(f(Pose(nearLegOut = 40f))[Joint.NearAnkle].z > 40f)
        // Twisting turns the chest (and so a forward-reaching arm) toward the near side.
        assertTrue(f(Pose(twist = 40f, nearShoulder = 90f))[Joint.NearHand].z > f(Pose(nearShoulder = 90f))[Joint.NearHand].z + 20f)
    }

    @Test
    fun levelSideCameraIsTheClassicPicture() {
        val p = Projection(Camera(0f, 0f), Vec3(10f, -80f, 0f))
        val s = p.project(Vec3(30f, -120f, 0f))
        assertEquals(30f, s.x, 0.001f)
        assertEquals(-120f, s.y, 0.001f)
        assertTrue("near side is closer", p.project(Vec3(0f, 0f, 10f)).depth > p.project(Vec3(0f, 0f, -10f)).depth)
    }

    @Test
    fun lookingDownShowsTheFloorAndHeightFromAboveIsNearer() {
        val p = Projection(View.Above.camera, Vec3(0f, -40f, 0f))
        // The near edge of the mat sits lower on screen than the far edge.
        assertTrue(p.project(Vec3(0f, 0f, 30f)).y > p.project(Vec3(0f, 0f, -30f)).y + 40f)
        // A head high above the floor is nearer to a camera that looks down.
        assertTrue(p.project(Vec3(0f, -170f, 0f)).depth > p.project(Vec3(0f, 0f, 0f)).depth)
    }

    @Test
    fun sceneFrameHoldsEveryPartFromEveryView() {
        SampleCatalog.all.forEach { s ->
            val poses = MotionPlayer.keyframes(s.motion)
            View.entries.forEach { view ->
                val scene = Scene.of(poses, view.camera)
                val b = scene.frame
                assertTrue("${s.key} $view finite", b.width.isFinite() && b.height.isFinite() && b.width > 0f && b.height > 0f)
                poses.map { Figure3d.solve(it) }.forEach { f ->
                    f.points.values.forEach { w ->
                        val q = scene.projection.project(w)
                        assertTrue("${s.key} $view in frame", q.x in b.left..b.right && q.y in b.top..b.bottom)
                    }
                }
            }
        }
    }

    @Test
    fun butterflySolesMeetWhileKneesFallOpen() {
        val m = SampleCatalog.byKey("butterfly")!!.motion
        val f = Figure3d.solve(m.stretch)
        val kneeGap = abs(f[Joint.NearKnee].z - f[Joint.FarKnee].z)
        val ankleGap = abs(f[Joint.NearAnkle].z - f[Joint.FarAnkle].z)
        assertTrue("knees apart $kneeGap", kneeGap > 60f)
        assertTrue("soles together $ankleGap", ankleGap < 20f)
        // Knees stay off the floor but low.
        assertTrue(f[Joint.NearKnee].y < 0f)
    }

    @Test
    fun rollLiesTheBodyOnItsSideNearSideUp() {
        val f = Figure3d.solve(Pose(roll = 90f, torso = 90f, nearHip = -90f, farHip = -90f))
        assertTrue(f.root(Segment.NearUpperArm).y < f.root(Segment.FarUpperArm).y - 30f)
        assertEquals(0f, f.points.maxOf { (j, p) -> p.y + Body.radius(j) }, 0.01f)
    }

    @Test
    fun armTurnBendsTheForearmUpFromAGoalpost() {
        val f = Figure3d.solve(Pose(nearArmOut = 90f, nearElbow = 90f, nearArmTurn = 90f, farArmOut = 90f, farElbow = 90f, farArmTurn = 90f), grounded = false)
        assertTrue(f[Joint.NearHand].y < f[Joint.NearElbow].y - 20f)
        assertTrue(f[Joint.FarHand].y < f[Joint.FarElbow].y - 20f)
    }

    @Test
    fun everyBodyPartGetsTheSameOutlineInEveryView() {
        val s = SampleCatalog.byKey("seated_twist")!!
        View.entries.forEach { view ->
            val stage = Stage.of(MotionPlayer.keyframes(s.motion), view.camera, null)
            val shapes = FigureDrawing.shapes(s.motion.stretch, stage, s.highlight, 300f, 300f)
            // One outline per body segment plus the head, whichever parts are in front.
            assertEquals("$view", Segment.entries.size + 1, shapes.count { it.ink == Ink.Outline })
            assertTrue(shapes.filter { it.ink == Ink.Outline }.all { it.fade == 0f && it.alpha == 1f })
        }
    }

    private fun assertNear(msg: String, e: Vec, a: Vec) =
        assertTrue("$msg: $e vs $a", max(abs(e.x - a.x), abs(e.y - a.y)) < 0.01f)
}

/** The original 2D solver, kept here so the 3D figure can be checked against it. */
private object Legacy2d {
    fun solve(pose: Pose, grounded: Boolean): Map<Joint, Vec> {
        val hip = Vec(pose.shiftX, 0f)
        val shoulder = hip + Vec.dir(pose.torso) * Body.TORSO
        val neckAngle = pose.torso + pose.head
        val neck = shoulder + Vec.dir(neckAngle) * Body.NECK
        val head = neck + Vec.dir(neckAngle) * Body.HEAD_RADIUS
        val armBase = pose.torso - 180f
        val nearUpper = armBase + pose.nearShoulder
        val nearElbow = shoulder + Vec.dir(nearUpper) * Body.UPPER_ARM
        val nearHand = nearElbow + Vec.dir(nearUpper + pose.nearElbow) * Body.FOREARM
        val farUpper = armBase + pose.farShoulder
        val farElbow = shoulder + Vec.dir(farUpper) * Body.UPPER_ARM
        val farHand = farElbow + Vec.dir(farUpper + pose.farElbow) * Body.FOREARM
        val nearKnee = hip + Vec.dir(pose.nearHip) * Body.THIGH
        val nearShin = pose.nearHip - pose.nearKnee
        val nearAnkle = nearKnee + Vec.dir(nearShin) * Body.SHIN
        val nearToe = nearAnkle + Vec.dir(nearShin + 90f + pose.nearAnkle) * Body.FOOT
        val farKnee = hip + Vec.dir(pose.farHip) * Body.THIGH
        val farShin = pose.farHip - pose.farKnee
        val farAnkle = farKnee + Vec.dir(farShin) * Body.SHIN
        val farToe = farAnkle + Vec.dir(farShin + 90f + pose.farAnkle) * Body.FOOT
        val mid = (hip + shoulder) * 0.5f
        val spineControl = mid + Vec.dir(pose.torso + 90f) * pose.spine
        val raw = mapOf(
            Joint.Hip to hip, Joint.Shoulder to shoulder, Joint.Neck to neck, Joint.Head to head,
            Joint.NearElbow to nearElbow, Joint.NearHand to nearHand, Joint.FarElbow to farElbow, Joint.FarHand to farHand,
            Joint.NearKnee to nearKnee, Joint.NearAnkle to nearAnkle, Joint.NearToe to nearToe,
            Joint.FarKnee to farKnee, Joint.FarAnkle to farAnkle, Joint.FarToe to farToe,
        )
        if (!grounded) return raw
        val spineBottom = Figure.spineMidpoint(hip, spineControl, shoulder).y + Body.width(Segment.Torso) / 2f
        val lowest = max(raw.maxOf { (j, p) -> p.y + Body.radius(j) }, spineBottom)
        return raw.mapValues { it.value + Vec(0f, -lowest) }
    }
}
