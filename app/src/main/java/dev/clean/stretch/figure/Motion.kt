package dev.clean.stretch.figure

import kotlin.math.PI
import kotlin.math.cos

enum class MotionKind {
    /** Ease into a position and hold, gently deepening on each exhale. */
    Hold,
    /** Move rhythmically between two positions (dynamic stretches such as cat–cow). */
    Flow,
}

/**
 * The keyframes for one stretch.
 * - [start]: where the person begins (standing, sitting tall, on hands and knees...).
 * - [stretch]: the stretch position.
 * - [deep]: optional slightly deeper position reached on each exhale (Hold) or the
 *   second position of a Flow.
 * - [sequence]: Flow only. When set, the flow loops through [stretch] and then each of these
 *   positions in order, smoothly, and back to [stretch] (circles, multi-step moves) instead of
 *   swinging between two positions. [deep] is ignored by a sequence flow.
 */
data class Motion(
    val kind: MotionKind,
    val start: Pose,
    val stretch: Pose,
    val deep: Pose? = null,
    /** Hands/feet that stay planted while the body moves (solved with [Ik]). */
    val pins: Set<Pin> = emptySet(),
    /** Viewpoint the stretch is shown from first; people can turn the view in the app. */
    val view: View = View.Side,
    val sequence: List<Pose> = emptyList(),
)

object Timing {
    const val ENTER_MS = 1600L
    const val EXIT_MS = 1200L
    const val REST_MS = 700L
    const val DEMO_HOLD_MS = 7000L
    /** One slow breath: roughly 3 s in, 5 s out. */
    const val BREATH_MS = 8000L
    /** Half a cycle of a Flow movement. */
    const val FLOW_HALF_MS = 2600L
    /** Shortest time between two positions of a sequence flow, so many-step loops don't rush. */
    const val SEQUENCE_STEP_MIN_MS = 1000L

    /** Time from one sequence position to the next: one Flow cycle spread over the loop. */
    fun sequenceStepMs(positions: Int): Long = maxOf(SEQUENCE_STEP_MIN_MS, FLOW_HALF_MS * 2 / positions)

    val DEMO_LOOP_MS get() = ENTER_MS + DEMO_HOLD_MS + EXIT_MS + REST_MS
}

object Easing {
    fun inOutCubic(t: Float): Float {
        val x = t.coerceIn(0f, 1f)
        return if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).let { it * it * it } / 2f
    }

    /** 0 → 1 → 0 over one breath, smooth at both ends. */
    fun breath(tMs: Long): Float {
        val phase = (tMs % Timing.BREATH_MS).toFloat() / Timing.BREATH_MS
        return ((1f - cos(2f * PI.toFloat() * phase)) / 2f)
    }
}

object MotionPlayer {

    /** The pose while holding a stretch, [tMs] after the hold began (after entering). */
    fun holdPose(motion: Motion, tMs: Long): Pose = when (motion.kind) {
        MotionKind.Hold -> {
            val deep = motion.deep ?: motion.stretch.copy(spine = motion.stretch.spine - 2f, head = motion.stretch.head + 1.5f)
            motion.stretch.lerp(deep, Easing.breath(tMs))
        }
        MotionKind.Flow -> if (motion.sequence.isNotEmpty()) {
            val pose = sequencePose(listOf(motion.stretch) + motion.sequence, tMs)
            if (motion.pins.isEmpty()) pose else Ik.pin(pose, motion.stretch, Ik.targets(motion.stretch, motion.pins))
        } else {
            val other = motion.deep ?: motion.start
            val half = Timing.FLOW_HALF_MS
            val cycle = tMs % (half * 2)
            val t = if (cycle < half) cycle.toFloat() / half else 1f - (cycle - half).toFloat() / half
            val pose = motion.stretch.lerp(other, Easing.inOutCubic(t))
            if (motion.pins.isEmpty()) pose else Ik.pin(pose, motion.stretch, Ik.targets(motion.stretch, motion.pins))
        }
    }

    /** Enter from the start position, then hold. Used by the routine player. */
    fun sessionPose(motion: Motion, tMs: Long, reducedMotion: Boolean): Pose {
        if (reducedMotion) return motion.stretch
        if (tMs < Timing.ENTER_MS) {
            return motion.start.lerp(motion.stretch, Easing.inOutCubic(tMs.toFloat() / Timing.ENTER_MS))
        }
        return holdPose(motion, tMs - Timing.ENTER_MS)
    }

    /** Looping preview used in lists and detail screens: enter, hold, return, pause. */
    fun demoPose(motion: Motion, tMs: Long, reducedMotion: Boolean): Pose {
        if (reducedMotion) return motion.stretch
        val t = tMs % Timing.DEMO_LOOP_MS
        val holdEnd = Timing.ENTER_MS + Timing.DEMO_HOLD_MS
        return when {
            t < Timing.ENTER_MS -> motion.start.lerp(motion.stretch, Easing.inOutCubic(t.toFloat() / Timing.ENTER_MS))
            t < holdEnd -> holdPose(motion, t - Timing.ENTER_MS)
            t < holdEnd + Timing.EXIT_MS -> {
                val from = holdPose(motion, Timing.DEMO_HOLD_MS)
                from.lerp(motion.start, Easing.inOutCubic((t - holdEnd).toFloat() / Timing.EXIT_MS))
            }
            else -> motion.start
        }
    }

    /**
     * A closed loop through [frames] (at least 2), [tMs] into it. Catmull-Rom curves through
     * every value, so a circle stays round and the motion has no corners at the positions.
     */
    fun sequencePose(frames: List<Pose>, tMs: Long): Pose {
        val n = frames.size
        val step = Timing.sequenceStepMs(n)
        val loop = tMs.mod(step * n)
        val i = (loop / step).toInt()
        val t = (loop % step).toFloat() / step
        val p0 = frames[(i - 1 + n) % n]; val p1 = frames[i]; val p2 = frames[(i + 1) % n]; val p3 = frames[(i + 2) % n]
        return PoseParam.entries.fold(p1) { pose, prm ->
            val a = p0[prm]; val b = p1[prm]; val c = p2[prm]; val d = p3[prm]
            val v = 0.5f * (2f * b + (c - a) * t + (2f * a - 5f * b + 4f * c - d) * t * t + (3f * b - a - 3f * c + d) * t * t * t)
            // Values that are 0 at both ends stay 0 (e.g. no hand drawn), so nothing flickers.
            pose.with(prm, if (b == 0f && c == 0f) 0f else v)
        }
    }

    /** Every keyframe, so a screen can frame the figure once without it jumping around. */
    fun keyframes(motion: Motion): List<Pose> = listOfNotNull(motion.start, motion.stretch, motion.deep) + motion.sequence

    fun frameFor(motion: Motion): Box =
        keyframes(motion).map { Figure.solve(it).bounds() }.reduce { a, b -> a.union(b) }
}
