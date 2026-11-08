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

    fun hold(seconds: Long) = "Hold for $seconds seconds."

    fun next(stretch: Stretch, side: Side) = "Next: ${stretch.name}${sideText(side)}."

    /** All lines for the current catalog. */
    fun all(): List<String> {
        val lines = mutableListOf(COMPLETE, hold(HOLD_MS / 1000))
        SampleCatalog.all.forEach { s ->
            val sides = if (s.sided) listOf(Side.Left, Side.Right) else listOf(Side.Both)
            sides.forEach { side -> lines += setup(s, side); lines += next(s, side) }
        }
        return lines.distinct()
    }

    /** Resource name for a line: "voice_" + lowercase words joined by underscores. */
    fun clipName(text: String): String =
        "voice_" + text.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
}
