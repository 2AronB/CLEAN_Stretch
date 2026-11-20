package dev.clean.stretch.ui

import dev.clean.stretch.catalog.SampleCatalog
import dev.clean.stretch.catalog.Stretch

/**
 * Every sentence the runner speaks (D-026). Each one ships as a pre-generated clip in
 * res/raw named [clipName]; tools/voice/generate.py makes them from tools/voice/phrases.txt.
 * VoiceLinesTest fails if this list, phrases.txt and the clips ever drift apart.
 */
object VoiceLines {
    const val COMPLETE = "Routine complete. Nice work."

    private fun sideText(side: Side) = if (side == Side.Both) "" else ", ${side.label.lowercase()}"

    fun setup(stretch: Stretch, side: Side) = "${stretch.name}${sideText(side)}. Get into position."

    // Each hold starts with "Begin" right after the spoken 3-2-1 (owner, D-044).
    fun hold(seconds: Long) = "Begin. Hold for $seconds seconds."

    fun roll(seconds: Long) = "Begin. Roll slowly for $seconds seconds."

    fun flow(seconds: Long) = "Begin. Keep moving for $seconds seconds."

    /** "Three." / "Two." / "One." at the end of "get into position"; null for other numbers. */
    fun count(secondsLeft: Int): String? = COUNT.getOrNull(secondsLeft - 1)

    private val COUNT = listOf("One.", "Two.", "Three.")

    /** Hold lengths a routine can use: 30 / 45 / 60 s chips (D-044), and 40 s from routines saved before that. */
    val HOLD_LENGTHS = listOf(30L, 40L, 45L, 60L)

    /** All lines for the current catalog. */
    fun all(): List<String> {
        val lines = mutableListOf(COMPLETE)
        lines += COUNT.reversed()
        HOLD_LENGTHS.forEach { lines += hold(it); lines += flow(it) }
        lines += roll(60)
        SampleCatalog.all.forEach { s ->
            val sides = if (s.sided) listOf(Side.Left, Side.Right) else listOf(Side.Both)
            sides.forEach { side -> lines += setup(s, side) }
        }
        return lines.distinct()
    }

    /** Resource name for a line: "voice_" + lowercase words joined by underscores. */
    fun clipName(text: String): String =
        "voice_" + text.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
}
