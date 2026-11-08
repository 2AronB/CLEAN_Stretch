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

    /** Local dates (this zone) on which at least one session started. */
    fun activeDays(sessions: List<SessionEntity>, zone: ZoneId): Set<LocalDate> =
        sessions.map { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate() }.toSet()
}
