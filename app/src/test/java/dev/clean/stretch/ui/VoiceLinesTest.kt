package dev.clean.stretch.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Keeps the spoken lines, tools/voice/phrases.txt and the clips in res/raw in sync.
 * If this fails after adding or renaming a stretch: copy app/build/voice-phrases.txt over
 * tools/voice/phrases.txt, run tools/voice/generate.py on the PC, then rebuild.
 */
class VoiceLinesTest {
    private val lines = VoiceLines.all()

    @Test
    fun phrasesFileMatchesTheApp() {
        File("build").mkdirs()
        File("build/voice-phrases.txt").writeText(lines.joinToString("\n") + "\n")
        val committed = File("../tools/voice/phrases.txt").readLines().filter { it.isNotBlank() }
        assertEquals("tools/voice/phrases.txt is out of date (see app/build/voice-phrases.txt)", lines, committed)
    }

    @Test
    fun everyLineHasAClip() {
        val missing = lines.map(VoiceLines::clipName).filterNot { File("src/main/res/raw/$it.ogg").isFile }
        assertTrue("Missing voice clips: $missing", missing.isEmpty())
    }

    @Test
    fun clipNamesAreValidAndUnique() {
        val names = lines.map(VoiceLines::clipName)
        assertEquals(names.size, names.toSet().size)
        names.forEach { assertTrue(it, it.matches(Regex("[a-z][a-z0-9_]*"))) }
    }
}
