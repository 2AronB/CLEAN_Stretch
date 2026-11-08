package dev.clean.stretch.data

/**
 * Tiny JSON reader/writer for backups (S7). Pure Kotlin so the backup format is unit-tested on
 * the JVM, and no new library is needed. Values: Map, List, String, Long, Double, Boolean, null.
 */
object Json {
    fun write(value: Any?): String = StringBuilder().also { write(value, it) }.toString()

    private fun write(v: Any?, out: StringBuilder) {
        when (v) {
            null -> out.append("null")
            is Boolean -> out.append(v)
            is Int, is Long -> out.append(v)
            is Double -> out.append(if (v.isFinite()) v.toString() else "null")
            is String -> string(v, out)
            is Map<*, *> -> {
                out.append('{')
                v.entries.forEachIndexed { i, (k, value) ->
                    if (i > 0) out.append(',')
                    string(k.toString(), out); out.append(':'); write(value, out)
                }
                out.append('}')
            }
            is List<*> -> {
                out.append('[')
                v.forEachIndexed { i, value -> if (i > 0) out.append(','); write(value, out) }
                out.append(']')
            }
            else -> throw IllegalArgumentException("Can't write ${v::class.simpleName}")
        }
    }

    private fun string(s: String, out: StringBuilder) {
        out.append('"')
        s.forEach { c ->
            when (c) {
                '"' -> out.append("\\\"")
                '\\' -> out.append("\\\\")
                '\n' -> out.append("\\n")
                '\r' -> out.append("\\r")
                '\t' -> out.append("\\t")
                else -> if (c < ' ') out.append("\\u%04x".format(c.code)) else out.append(c)
            }
        }
        out.append('"')
    }

    /** Parses [text]; throws [IllegalArgumentException] on anything malformed. */
    fun parse(text: String): Any? {
        val p = Parser(text)
        val v = p.value()
        p.skipSpace()
        require(p.done()) { "Unexpected text after JSON at ${p.pos}" }
        return v
    }

    private class Parser(val s: String) {
        var pos = 0
        fun done() = pos >= s.length
        fun skipSpace() { while (!done() && s[pos].isWhitespace()) pos++ }
        private fun peek(): Char { skipSpace(); require(!done()) { "Unexpected end of file" }; return s[pos] }
        private fun expect(c: Char) { require(peek() == c) { "Expected '$c' at $pos" }; pos++ }

        fun value(): Any? = when (val c = peek()) {
            '{' -> obj()
            '[' -> arr()
            '"' -> str()
            't' -> word("true", true)
            'f' -> word("false", false)
            'n' -> word("null", null)
            else -> if (c == '-' || c.isDigit()) num() else throw IllegalArgumentException("Unexpected '$c' at $pos")
        }

        private fun word(w: String, v: Any?): Any? {
            require(s.startsWith(w, pos)) { "Expected $w at $pos" }
            pos += w.length
            return v
        }

        private fun num(): Any {
            val start = pos
            while (!done() && (s[pos].isDigit() || s[pos] in "+-.eE")) pos++
            val t = s.substring(start, pos)
            return t.toLongOrNull() ?: t.toDoubleOrNull() ?: throw IllegalArgumentException("Bad number '$t'")
        }

        private fun str(): String {
            expect('"')
            val out = StringBuilder()
            while (true) {
                require(!done()) { "Unterminated string" }
                when (val c = s[pos++]) {
                    '"' -> return out.toString()
                    '\\' -> {
                        require(!done()) { "Bad escape" }
                        when (val e = s[pos++]) {
                            '"', '\\', '/' -> out.append(e)
                            'n' -> out.append('\n'); 'r' -> out.append('\r'); 't' -> out.append('\t')
                            'b' -> out.append('\b'); 'f' -> out.append('\u000C')
                            'u' -> {
                                require(pos + 4 <= s.length) { "Bad unicode escape" }
                                out.append(s.substring(pos, pos + 4).toInt(16).toChar()); pos += 4
                            }
                            else -> throw IllegalArgumentException("Bad escape \\$e")
                        }
                    }
                    else -> out.append(c)
                }
            }
        }

        private fun arr(): List<Any?> {
            expect('[')
            val list = mutableListOf<Any?>()
            if (peek() == ']') { pos++; return list }
            while (true) {
                list += value()
                if (peek() == ',') { pos++; continue }
                expect(']'); return list
            }
        }

        private fun obj(): Map<String, Any?> {
            expect('{')
            val map = LinkedHashMap<String, Any?>()
            if (peek() == '}') { pos++; return map }
            while (true) {
                skipSpace()
                val k = str()
                expect(':')
                map[k] = value()
                if (peek() == ',') { pos++; continue }
                expect('}'); return map
            }
        }
    }
}
