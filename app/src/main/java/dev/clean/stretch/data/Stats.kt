package dev.clean.stretch.data

import dev.clean.stretch.catalog.Muscle
import dev.clean.stretch.catalog.Region
import dev.clean.stretch.catalog.SampleCatalog
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/** Left or right side of the body. */
enum class BodySide(val label: String) { Left("Left"), Right("Right") }

/** Minutes for one muscle on each side this week. */
data class SideMinutes(val left: Double = 0.0, val right: Double = 0.0) {
    /** The side that's further behind; goals count as met only when both sides are. */
    val weaker: Double get() = minOf(left, right)
    operator fun get(side: BodySide) = if (side == BodySide.Left) left else right
}

/** Weekly numbers, always recomputed from saved session steps (never stored). Pure Kotlin. */
object Stats {
    /**
     * Minutes each muscle was stretched this week, per side (D-039). Doses in the research are per
     * muscle on one side (EVIDENCE §1), so a 40 s hold on the right counts 40 s for the right side
     * only; a move that stretches both sides at once ("both") counts for each side. Each of a move's
     * main muscles gets the full time; muscles it only stretches lightly get nothing.
     */
    fun minutesByMuscle(steps: List<SessionStepEntity>): Map<Muscle, SideMinutes> {
        val totals = mutableMapOf<Muscle, SideMinutes>()
        steps.forEach { s ->
            val muscles = mainMuscles(s)
            val min = s.actualSeconds / 60.0
            muscles.forEach { m ->
                val t = totals[m] ?: SideMinutes()
                totals[m] = when (s.side) {
                    "left" -> t.copy(left = t.left + min)
                    "right" -> t.copy(right = t.right + min)
                    else -> SideMinutes(t.left + min, t.right + min)
                }
            }
        }
        return totals
    }

    /** A step's main stretched muscles: the saved snapshot, or for rows saved before v6, the move's current tags. */
    private fun mainMuscles(s: SessionStepEntity): Set<Muscle> {
        if (s.primaryMuscles.isNotBlank()) {
            return s.primaryMuscles.split(",").mapNotNull { n -> Muscle.entries.firstOrNull { it.name == n } }.toSet()
        }
        // Old rows: only stretch steps had areas recorded (strength and rolling had none).
        if (s.primaryRegions.isBlank()) return emptySet()
        return SampleCatalog.byKey(s.stretchKey)?.primary?.toSet() ?: emptySet()
    }

    /** Weeks start on Monday (docs/PLAN.md default) in the phone's time zone. */
    fun weekStart(today: LocalDate): LocalDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun weekStartMillis(today: LocalDate, zone: ZoneId): Long =
        weekStart(today).atStartOfDay(zone).toInstant().toEpochMilli()

    /** Minutes held per body area, counting each step once for each primary area it targets. */
    fun minutesByRegion(steps: List<SessionStepEntity>): Map<Region, Double> {
        val totals = mutableMapOf<Region, Double>()
        steps.forEach { s ->
            val regions = s.primaryRegions.split(",").mapNotNull { name -> Region.entries.firstOrNull { it.name == name } }.toSet()
            regions.forEach { r -> totals[r] = (totals[r] ?: 0.0) + s.actualSeconds / 60.0 }
        }
        return totals
    }

    /**
     * Week streak (P-7): consecutive weeks, Monday start, with at least [daysTarget] days of
     * stretching. This week only adds to the streak once it hits the target; until then the
     * streak shows the weeks before it, so an unfinished week never "breaks" anything.
     */
    fun weekStreak(activeDays: Set<LocalDate>, today: LocalDate, daysTarget: Int): Int {
        fun met(weekStart: LocalDate) = activeDays.count { !it.isBefore(weekStart) && it.isBefore(weekStart.plusDays(7)) } >= daysTarget
        var week = weekStart(today)
        var streak = 0
        if (met(week)) streak++
        week = week.minusWeeks(1)
        while (met(week)) { streak++; week = week.minusWeeks(1) }
        return streak
    }

    /** Minutes held on each local date (every hold counts: stretches, strength and rolling). */
    fun minutesByDay(sessions: List<SessionEntity>, steps: List<SessionStepEntity>, zone: ZoneId): Map<LocalDate, Double> {
        val dayOf = sessions.associate { it.id to Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate() }
        return steps.mapNotNull { s -> dayOf[s.sessionId]?.let { it to s.actualSeconds / 60.0 } }
            .groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }
    }

    /**
     * How full each day of the week starting [monday] is, 0..1 (owner, 2026-11-14). A day fills
     * toward [dayGoal] minutes with its own stretching; minutes beyond the goal fill earlier
     * unfinished days of the same week, so a missed day can be caught up later in the week.
     */
    fun weekFill(monday: LocalDate, minutes: Map<LocalDate, Double>, dayGoal: Double): List<Float> {
        val goal = dayGoal.coerceAtLeast(1.0)
        val filled = DoubleArray(7)
        for (i in 0 until 7) {
            var left = minutes[monday.plusDays(i.toLong())] ?: 0.0
            val own = minOf(left, goal)
            filled[i] += own
            left -= own
            var j = 0
            while (left > 1e-9 && j < i) {
                val room = goal - filled[j]
                if (room > 0) { val add = minOf(room, left); filled[j] += add; left -= add }
                j++
            }
        }
        return filled.map { (it / goal).toFloat().coerceIn(0f, 1f) }
    }

    /** Days of that week that reached their goal (a full circle). */
    fun daysDone(monday: LocalDate, minutes: Map<LocalDate, Double>, dayGoal: Double): Int =
        weekFill(monday, minutes, dayGoal).count { it >= 0.999f }

    /**
     * Week streak by full days: weeks where at least [daysTarget] days reached their goal. This
     * week only counts once it does, so an unfinished week never breaks anything.
     */
    fun weekStreakByGoal(minutes: Map<LocalDate, Double>, today: LocalDate, daysTarget: Int, dayGoal: Double): Int {
        fun met(monday: LocalDate) = daysDone(monday, minutes, dayGoal) >= daysTarget
        var week = weekStart(today)
        var streak = 0
        if (met(week)) streak++
        week = week.minusWeeks(1)
        while (met(week)) { streak++; week = week.minusWeeks(1) }
        return streak
    }

    fun daysFromStarts(starts: List<Long>, zone: ZoneId): Set<LocalDate> =
        starts.map { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }.toSet()

    /** Local dates (this zone) on which at least one session started. */
    fun activeDays(sessions: List<SessionEntity>, zone: ZoneId): Set<LocalDate> =
        sessions.map { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate() }.toSet()
}
