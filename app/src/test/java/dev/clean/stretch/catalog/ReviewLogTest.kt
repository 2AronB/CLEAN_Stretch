package dev.clean.stretch.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Keeps docs/REVIEW_LOG.md honest: every move has exactly one row, and a move only stays
 * "Reviewed" while it is exactly what the owner approved (its fingerprint still matches).
 * Only the owner can approve a move; see the rules at the top of the log.
 */
class ReviewLogTest {

    private data class Row(val key: String, val status: String, val date: String, val fingerprint: String, val approval: String, val feedback: String)

    private val log = File("../docs/REVIEW_LOG.md")

    private fun rows(): List<Row> {
        val lines = log.readLines()
        val start = lines.indexOfFirst { it.trim() == "## Status" }
        assertTrue("REVIEW_LOG.md needs a '## Status' section", start >= 0)
        return lines.drop(start + 1)
            .takeWhile { !it.startsWith("## ") }
            .filter { it.startsWith("| ") && !it.startsWith("| Key ") }
            .map { line ->
                val cells = line.trim().removePrefix("|").removeSuffix("|").split("|").map { it.trim() }
                assertEquals("7 cells expected (no '|' inside a cell): $line", 7, cells.size)
                Row(cells[0], cells[2], cells[3], cells[4], cells[5], cells[6])
            }
    }

    private fun history(): String = log.readText().substringAfter("## History")

    @Test
    fun everyMoveHasExactlyOneRow() {
        val keys = rows().map { it.key }
        assertEquals("duplicate rows", keys.size, keys.toSet().size)
        assertEquals("REVIEW_LOG.md rows must match the catalog (add new moves as 'Not reviewed')", SampleCatalog.all.map { it.key }.toSet(), keys.toSet())
    }

    @Test
    fun statusesAreFilledInProperly() {
        rows().forEach { r ->
            when (r.status) {
                "Not reviewed" -> assertTrue("${r.key}: a move that isn't reviewed has no approval", r.date.isEmpty() && r.fingerprint.isEmpty() && r.approval.isEmpty())
                "Changes requested" -> {
                    assertTrue("${r.key}: write the owner's feedback in 'Open feedback'", r.feedback.isNotEmpty())
                    assertTrue("${r.key}: a move with open feedback has no approval", r.approval.isEmpty() && r.fingerprint.isEmpty())
                    assertTrue("${r.key}: add a history line for the feedback", history().contains("${r.key} · Changes requested"))
                }
                "Reviewed" -> {
                    assertTrue("${r.key}: approval date must be YYYY-MM-DD", r.date.matches(Regex("""\d{4}-\d{2}-\d{2}""")))
                    assertTrue("${r.key}: quote the owner's approval verbatim, in quotes", r.approval.length > 2 && r.approval.startsWith("\"") && r.approval.endsWith("\""))
                    assertTrue("${r.key}: a reviewed move has no open feedback", r.feedback.isEmpty())
                    assertTrue("${r.key}: add a history line for the approval", history().contains("${r.key} · Reviewed"))
                }
                else -> fail("${r.key}: unknown status '${r.status}' (use Not reviewed, Changes requested or Reviewed)")
            }
        }
    }

    @Test
    fun reviewedMovesAreUnchangedSinceApproval() {
        val current = SampleCatalog.all.associate { it.key to fingerprint(it) }
        File("build").mkdirs()
        File("build/review-fingerprints.txt").writeText(current.entries.joinToString("\n") { "${it.key} ${it.value}" } + "\n")
        rows().filter { it.status == "Reviewed" }.forEach { r ->
            assertEquals(
                "${r.key} changed after the owner approved it. Set it back to 'Not reviewed' (or 'Changes requested') " +
                    "with a history line; only the owner can mark it reviewed again.",
                current[r.key], r.fingerprint,
            )
        }
    }

    companion object {
        /** Short hash of everything about a move (pose, motion, view, cues, wording, sources...). */
        fun fingerprint(s: Stretch): String =
            MessageDigest.getInstance("SHA-256").digest(s.toString().toByteArray()).take(6).joinToString("") { "%02x".format(it) }
    }
}
