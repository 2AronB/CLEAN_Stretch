package dev.clean.stretch.plan

import dev.clean.stretch.catalog.Region

/**
 * One of up to three daily reminders (owner, D-044), each with the routine it opens: e.g. a
 * morning strengthen routine, a noon neck-and-shoulders quick break, an evening foam-roller
 * cool-down. Stored on the phone as one line of text per reminder (settings file, backed up).
 *
 * [roller] / [strengthen]: null = follow the Today card's chips.
 */
data class ReminderPlan(
    /** 1..[MAX]; also the alarm's request code, so each reminder has its own alarm. */
    val id: Int,
    /** Minutes after midnight. */
    val time: Int,
    val kind: RoutineKind = RoutineKind.Full,
    /** Areas the routine sticks to; empty = follow the weekly goals. */
    val areas: Set<Region> = emptySet(),
    val roller: Boolean? = null,
    val strengthen: Boolean? = null,
) {
    /** "Cool-down · neck, shoulders & chest". */
    val label: String
        get() = kind.label + if (areas.isEmpty()) "" else " · " + areaText(areas)

    /** Routine title shown on Today and saved in history. */
    val title: String
        get() = if (areas.isEmpty()) kind.title else "${kind.title}: ${areaText(areas)}"

    fun encode(): String = listOf(
        id, time, kind.name, areas.joinToString(",") { it.name },
        roller?.toString() ?: "", strengthen?.toString() ?: "",
    ).joinToString(";")

    /** The Today profile with this reminder's foam roller / strengthen choices applied. */
    fun applyTo(profile: Profile): Profile = profile.copy(
        hasRoller = roller ?: profile.hasRoller,
        strengthen = strengthen ?: profile.strengthen,
    )

    companion object {
        const val MAX = 3

        private fun areaText(areas: Set<Region>): String {
            val names = Region.entries.filter { it in areas }.map { it.label.lowercase() }
            // "and" before the last, since some area names already contain "&" ("shoulders & chest").
            return if (names.size <= 1) names.joinToString() else names.dropLast(1).joinToString() + " and " + names.last()
        }

        fun decode(line: String): ReminderPlan? {
            val f = line.split(";")
            val id = f.getOrNull(0)?.toIntOrNull()?.takeIf { it in 1..MAX } ?: return null
            val time = f.getOrNull(1)?.toIntOrNull()?.takeIf { it in 0 until 24 * 60 } ?: return null
            return ReminderPlan(
                id = id,
                time = time,
                kind = RoutineKind.entries.firstOrNull { it.name == f.getOrNull(2) } ?: RoutineKind.Full,
                areas = f.getOrNull(3).orEmpty().split(",").mapNotNull { n -> Region.entries.firstOrNull { it.name == n } }.toSet(),
                roller = f.getOrNull(4)?.toBooleanStrictOrNull(),
                strengthen = f.getOrNull(5)?.toBooleanStrictOrNull(),
            )
        }

        fun encodeAll(plans: List<ReminderPlan>): String = plans.joinToString("\n") { it.encode() }

        /** Unreadable lines and repeated ids are dropped, never guessed. */
        fun decodeAll(text: String?): List<ReminderPlan> =
            text.orEmpty().lines().mapNotNull(::decode).distinctBy { it.id }.take(MAX).sortedBy { it.time }

        /** The lowest free id, or null when all [MAX] are used. */
        fun nextId(plans: List<ReminderPlan>): Int? = (1..MAX).firstOrNull { id -> plans.none { it.id == id } }
    }
}
