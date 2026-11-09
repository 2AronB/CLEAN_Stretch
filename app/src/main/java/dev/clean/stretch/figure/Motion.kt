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
 */
data class Motion(
    val kind: MotionKind,
    val start: Pose,
    val stretch: Pose,
    val deep: Pose? = null,
    /** Hands/feet that stay planted while the body moves (solved with [Ik]). */
    val pins: Set<Pin> = emptySet(),
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
        MotionKind.Flow -> {
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

    /** Every keyframe, so a screen can frame the figure once without it jumping around. */
    fun keyframes(motion: Motion): List<Pose> = listOfNotNull(motion.start, motion.stretch, motion.deep)

    fun frameFor(motion: Motion): Box =
        keyframes(motion).map { Figure.solve(it).bounds() }.reduce { a, b -> a.union(b) }
}
