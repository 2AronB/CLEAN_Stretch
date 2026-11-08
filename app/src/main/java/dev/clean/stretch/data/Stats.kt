package dev.clean.stretch.data

import dev.clean.stretch.catalog.Region
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/** Weekly numbers, always recomputed from saved session steps (never stored). Pure Kotlin. */
object Stats {
    /** Weekly goal per area. Tier A wording: "about 10 minutes per muscle each week" [S1][S2]. */
    const val WEEKLY_GOAL_MINUTES = 10.0

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

    fun daysFromStarts(starts: List<Long>, zone: ZoneId): Set<LocalDate> =
        starts.map { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }.toSet()

    /** Local dates (this zone) on which at least one session started. */
    fun activeDays(sessions: List<SessionEntity>, zone: ZoneId): Set<LocalDate> =
        sessions.map { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate() }.toSet()
}
