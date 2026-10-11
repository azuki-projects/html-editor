package com.example.editor

object HtmlFormatter {

    private val VOID_TAGS = setOf(
        "area", "base", "br", "col", "embed", "hr", "img", "input",
        "link", "meta", "param", "source", "track", "wbr"
    )

    /**
     * Formats an HTML string with indentation and neat line breaks.
     */
    fun formatHtml(html: String, indentSpaces: Int = 2): String {
        if (html.isBlank()) return ""

        val indentUnit = " ".repeat(indentSpaces)
        val sb = StringBuilder()
        var indentLevel = 0

        // Normalize spaces around tags while preserving tag contents
        val cleaned = html
            .replace(Regex(">\\s*<"), ">\n<")
            .trim()

        val lines = cleaned.split("\n")
        var inPreBlock = false
        var inScriptBlock = false
        var inStyleBlock = false

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            // Check block status
            val isClosingTag = trimmed.startsWith("</")
            val isOpeningTag = trimmed.startsWith("<") && !isClosingTag && !trimmed.startsWith("<!") && !trimmed.startsWith("<!--")
            val isComment = trimmed.startsWith("<!--")
            val isDoctype = trimmed.startsWith("<!DOCTYPE", ignoreCase = true)

            // Extract tag name
            val tagName = if (isOpeningTag || isClosingTag) {
                val match = Regex("</?([a-zA-Z0-9\\-]+)").find(trimmed)
                match?.groupValues?.get(1)?.lowercase() ?: ""
            } else ""

            if (tagName == "pre") inPreBlock = isOpeningTag
            if (tagName == "script") inScriptBlock = isOpeningTag
            if (tagName == "style") inStyleBlock = isOpeningTag

            val isVoid = VOID_TAGS.contains(tagName) || trimmed.endsWith("/>")

            // Adjust indent down before closing tag
            if (isClosingTag && indentLevel > 0) {
                indentLevel--
            }

            // Indent
            val currentIndent = indentUnit.repeat(indentLevel.coerceAtLeast(0))
            sb.append(currentIndent).append(trimmed).append("\n")

            // Adjust indent up after opening non-void tag
            if (isOpeningTag && !isVoid) {
                // If the line opens AND closes the same tag on the same line, don't increment
                val closesOnSameLine = trimmed.contains("</$tagName>", ignoreCase = true)
                if (!closesOnSameLine) {
                    indentLevel++
                }
            }
        }

        return sb.toString().trimEnd()
    }
}
