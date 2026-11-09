package dev.clean.stretch.figure

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

/** A hand or foot that stays planted on the floor while the body moves (e.g. foam rolling). */
enum class Pin { NearHand, FarHand, NearFoot, FarFoot }

/**
 * Two-bone inverse kinematics. Given a pose and where a pinned hand or foot must stay, it
 * re-solves that limb's shoulder/elbow (or hip/knee) so the limb reaches the spot. Arms pivot
 * at the shoulder and bend at the elbow; legs pivot at the hip and bend at the knee.
 */
object Ik {
    private fun deg(rad: Double) = (rad * 180.0 / PI).toFloat()

    /** Absolute angle (Pose convention: 0 = down, 90 = forward) of a vector. */
    private fun angleOf(v: Vec) = deg(atan2(v.x.toDouble(), v.y.toDouble()))

    private data class TwoBone(val toTarget: Float, val rootOffset: Float, val bend: Float)

    private fun solve(root: Vec, target: Vec, a: Float, b: Float): TwoBone {
        val v = target - root
        val raw = sqrt(v.x * v.x + v.y * v.y)
        val d = raw.coerceIn(abs(a - b) + 0.01f, a + b - 0.01f)
        val alpha = deg(acos(((a * a + d * d - b * b) / (2 * a * d)).toDouble().coerceIn(-1.0, 1.0)))
        val beta = deg(acos(((a * a + b * b - d * d) / (2 * a * b)).toDouble().coerceIn(-1.0, 1.0)))
        return TwoBone(angleOf(v), alpha, 180f - beta)
    }

    /** Where each pinned hand/foot is in [anchor] (unground coordinates, hip at shiftX). */
    fun targets(anchor: Pose, pins: Set<Pin>): Map<Pin, Vec> {
        val f = Figure.solve(anchor, grounded = false)
        return pins.associateWith { pin ->
            when (pin) {
                Pin.NearHand -> f[Joint.NearHand]
                Pin.FarHand -> f[Joint.FarHand]
                Pin.NearFoot -> f[Joint.NearAnkle]
                Pin.FarFoot -> f[Joint.FarAnkle]
            }
        }
    }

    /** Re-solves the pinned limbs of [pose]; feet keep [anchor]'s foot angle so they stay flat. */
    fun pin(pose: Pose, anchor: Pose, targets: Map<Pin, Vec>): Pose {
        if (targets.isEmpty()) return pose
        val f = Figure.solve(pose, grounded = false)
        var p = pose
        val armBase = pose.torso - 180f
        targets.forEach { (pin, target) ->
            when (pin) {
                Pin.NearHand, Pin.FarHand -> {
                    val s = solve(f[Joint.Shoulder], target, Body.UPPER_ARM, Body.FOREARM)
                    val shoulder = s.toTarget - s.rootOffset - armBase
                    p = if (pin == Pin.NearHand) p.copy(nearShoulder = shoulder, nearElbow = s.bend) else p.copy(farShoulder = shoulder, farElbow = s.bend)
                }
                Pin.NearFoot, Pin.FarFoot -> {
                    val s = solve(f[Joint.Hip], target, Body.THIGH, Body.SHIN)
                    val thigh = s.toTarget + s.rootOffset
                    val shin = thigh - s.bend
                    p = if (pin == Pin.NearFoot) {
                        val foot = anchor.nearHip - anchor.nearKnee + 90f + anchor.nearAnkle
                        p.copy(nearHip = thigh, nearKnee = s.bend, nearAnkle = foot - shin - 90f)
                    } else {
                        val foot = anchor.farHip - anchor.farKnee + 90f + anchor.farAnkle
                        p.copy(farHip = thigh, farKnee = s.bend, farAnkle = foot - shin - 90f)
                    }
                }
            }
        }
        return p
    }
}
