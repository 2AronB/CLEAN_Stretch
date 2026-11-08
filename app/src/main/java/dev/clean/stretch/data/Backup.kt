package dev.clean.stretch.data

/** Everything a backup holds (S7, R-009). The unfinished-run checkpoint is not included. */
data class BackupData(
    val sessions: List<SessionEntity>,
    val steps: List<SessionStepEntity>,
    val profile: ProfileEntity?,
    /** Small settings (theme, cues, plan, reminder) as text. */
    val settings: Map<String, String>,
)

class BackupException(message: String) : Exception(message)

/**
 * Versioned backup file. Import validates the whole file before anything is changed; a file
 * from a newer app version, or a damaged one, is refused and current data stays as it was.
 */
object BackupCodec {
    const val FORMAT = "clean-stretch-backup"
    const val VERSION = 1

    fun encode(data: BackupData, exportedAt: Long): String = Json.write(
        mapOf(
            "format" to FORMAT,
            "version" to VERSION,
            "exportedAt" to exportedAt,
            "sessions" to data.sessions.map { s ->
                mapOf("id" to s.id, "startedAt" to s.startedAt, "endedAt" to s.endedAt, "routineTitle" to s.routineTitle, "completed" to s.completed)
            },
            "steps" to data.steps.map { s ->
                mapOf(
                    "id" to s.id, "sessionId" to s.sessionId, "position" to s.position, "stretchKey" to s.stretchKey,
                    "stretchName" to s.stretchName, "side" to s.side, "primaryRegions" to s.primaryRegions, "muscles" to s.muscles,
                    "plannedSeconds" to s.plannedSeconds, "actualSeconds" to s.actualSeconds, "skipped" to s.skipped,
                )
            },
            "profile" to data.profile?.let { p ->
                mapOf(
                    "goal" to p.goal, "olderAdult" to p.olderAdult, "focus" to p.focus, "avoid" to p.avoid, "noFloor" to p.noFloor,
                    "noKneeling" to p.noKneeling, "hasWall" to p.hasWall, "conditions" to p.conditions, "minutes" to p.minutes,
                    "setupDone" to p.setupDone, "updatedAt" to p.updatedAt,
                )
            },
            "settings" to data.settings,
        ),
    )

    fun decode(text: String): BackupData {
        val root = try { Json.parse(text) } catch (e: IllegalArgumentException) {
            throw BackupException("This file isn't a readable backup (${e.message}).")
        } as? Map<*, *> ?: throw BackupException("This file isn't a Stretch backup.")
        if (root["format"] != FORMAT) throw BackupException("This file isn't a Stretch backup.")
        val version = (root["version"] as? Long)?.toInt() ?: throw BackupException("The backup has no version number.")
        if (version > VERSION) throw BackupException("This backup was made by a newer version of Stretch. Update the app, then try again.")
        if (version < 1) throw BackupException("Unknown backup version $version.")

        fun Map<*, *>.long(k: String) = this[k] as? Long ?: throw BackupException("Damaged backup: '$k' is missing.")
        fun Map<*, *>.int(k: String) = long(k).toInt()
        fun Map<*, *>.str(k: String) = this[k] as? String ?: throw BackupException("Damaged backup: '$k' is missing.")
        fun Map<*, *>.bool(k: String) = this[k] as? Boolean ?: throw BackupException("Damaged backup: '$k' is missing.")
        fun list(k: String) = (root[k] as? List<*> ?: throw BackupException("Damaged backup: '$k' is missing.")).map {
            it as? Map<*, *> ?: throw BackupException("Damaged backup: bad entry in '$k'.")
        }

        val sessions = list("sessions").map {
            SessionEntity(id = it.long("id"), startedAt = it.long("startedAt"), endedAt = it.long("endedAt"), routineTitle = it.str("routineTitle"), completed = it.bool("completed"))
        }
        val ids = sessions.map { it.id }.toSet()
        if (ids.size != sessions.size) throw BackupException("Damaged backup: repeated session ids.")
        val steps = list("steps").map {
            SessionStepEntity(
                id = it.long("id"), sessionId = it.long("sessionId"), position = it.int("position"), stretchKey = it.str("stretchKey"),
                stretchName = it.str("stretchName"), side = it.str("side"), primaryRegions = it.str("primaryRegions"), muscles = it.str("muscles"),
                plannedSeconds = it.int("plannedSeconds"), actualSeconds = it.int("actualSeconds"), skipped = it.bool("skipped"),
            )
        }
        if (steps.any { it.sessionId !in ids }) throw BackupException("Damaged backup: a step belongs to a missing session.")
        val profile = (root["profile"] as? Map<*, *>)?.let {
            ProfileEntity(
                goal = it.str("goal"), olderAdult = it.bool("olderAdult"), focus = it.str("focus"), avoid = it.str("avoid"),
                noFloor = it.bool("noFloor"), noKneeling = it.bool("noKneeling"), hasWall = it.bool("hasWall"),
                conditions = it.str("conditions"), minutes = it.int("minutes"), setupDone = it.bool("setupDone"), updatedAt = it.long("updatedAt"),
            )
        }
        val settings = (root["settings"] as? Map<*, *>)?.entries?.associate { (k, v) -> k.toString() to v.toString() } ?: emptyMap()
        return BackupData(sessions, steps, profile, settings)
    }
}
