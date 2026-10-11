package com.example.editor

enum class LintSeverity {
    ERROR,
    WARNING,
    INFO
}

data class LintIssue(
    val lineNumber: Int,
    val severity: LintSeverity,
    val message: String,
    val rule: String,
    val snippet: String = ""
)

object HtmlLinter {

    private val VOID_TAGS = setOf(
        "area", "base", "br", "col", "embed", "hr", "img", "input",
        "link", "meta", "param", "source", "track", "wbr"
    )

    private val DEPRECATED_TAGS = mapOf(
        "center" to "Use CSS text-align: center or flexbox instead",
        "font" to "Use CSS font-family, font-size, and color instead",
        "marquee" to "Deprecated and non-standard. Use CSS animations instead",
        "strike" to "Use CSS text-decoration: line-through or <s> / <del> instead",
        "big" to "Use CSS font-size instead",
        "frame" to "Framesets are obsolete. Use CSS layout or <iframe> if necessary",
        "frameset" to "Framesets are obsolete."
    )

    fun lint(html: String): List<LintIssue> {
        val issues = mutableListOf<LintIssue>()
        if (html.isBlank()) return issues

        val lines = html.lines()
        val openTagStack = ArrayDeque<Pair<String, Int>>() // tagName to lineNumber
        val seenIds = mutableMapOf<String, Int>() // id to lineNumber

        var hasViewportMeta = false
        var hasTitleTag = false
        var inComment = false

        for ((index, rawLine) in lines.withIndex()) {
            val lineNum = index + 1
            val trimmed = rawLine.trim()

            // Comment tracking
            if (trimmed.contains("<!--")) inComment = true
            if (trimmed.contains("-->")) {
                inComment = false
                continue
            }
            if (inComment) continue

            // 1. Check for unclosed attribute quotes on this line
            val quotesCheck = Regex("""<[^>]*(?:=\s*"[^"]*$|=\s*'[^']*$)""")
            if (quotesCheck.containsMatchIn(rawLine)) {
                issues.add(
                    LintIssue(
                        lineNumber = lineNum,
                        severity = LintSeverity.ERROR,
                        message = "Unclosed attribute quote detected",
                        rule = "attribute-quotes",
                        snippet = trimmed.take(40)
                    )
                )
            }

            // 2. Scan tags on this line
            val tagRegex = Regex("""</?([a-zA-Z0-9\-]+)([^>]*)>""")
            for (match in tagRegex.findAll(rawLine)) {
                val fullTag = match.value
                val tagName = match.groupValues[1].lowercase()
                val attrsStr = match.groupValues[2]
                val isClosing = fullTag.startsWith("</")
                val isSelfClosing = fullTag.endsWith("/>") || VOID_TAGS.contains(tagName)

                if (tagName == "meta" && attrsStr.contains("viewport", ignoreCase = true)) {
                    hasViewportMeta = true
                }
                if (tagName == "title" && !isClosing) {
                    hasTitleTag = true
                }

                // Check deprecated tags
                if (DEPRECATED_TAGS.containsKey(tagName) && !isClosing) {
                    issues.add(
                        LintIssue(
                            lineNumber = lineNum,
                            severity = LintSeverity.WARNING,
                            message = "<$tagName> is deprecated. ${DEPRECATED_TAGS[tagName]}",
                            rule = "deprecated-tag",
                            snippet = fullTag
                        )
                    )
                }

                // Check img alt attribute
                if (tagName == "img" && !attrsStr.contains(Regex("""\balt\s*=""", RegexOption.IGNORE_CASE))) {
                    issues.add(
                        LintIssue(
                            lineNumber = lineNum,
                            severity = LintSeverity.WARNING,
                            message = "<img> tag is missing an 'alt' attribute for accessibility",
                            rule = "img-alt-required",
                            snippet = fullTag.take(40)
                        )
                    )
                }

                // Check duplicate IDs
                val idMatch = Regex("""\bid\s*=\s*["']([^"']+)["']""", RegexOption.IGNORE_CASE).find(attrsStr)
                if (idMatch != null) {
                    val idVal = idMatch.groupValues[1]
                    if (seenIds.containsKey(idVal)) {
                        issues.add(
                            LintIssue(
                                lineNumber = lineNum,
                                severity = LintSeverity.ERROR,
                                message = "Duplicate id attribute '$idVal' (previously defined at line ${seenIds[idVal]})",
                                rule = "unique-id",
                                snippet = "id=\"$idVal\""
                            )
                        )
                    } else {
                        seenIds[idVal] = lineNum
                    }
                }

                // Tag pairing stack
                if (isClosing) {
                    if (openTagStack.isNotEmpty()) {
                        val last = openTagStack.last()
                        if (last.first == tagName) {
                            openTagStack.removeLast()
                        } else {
                            // Check if matching tag is deeper down the stack
                            val matchingIndex = openTagStack.indexOfLast { it.first == tagName }
                            if (matchingIndex >= 0) {
                                val skipped = openTagStack.subList(matchingIndex + 1, openTagStack.size).toList()
                                for (skip in skipped) {
                                    issues.add(
                                        LintIssue(
                                            lineNumber = skip.second,
                                            severity = LintSeverity.ERROR,
                                            message = "Unclosed tag <${skip.first}> before closing </$tagName>",
                                            rule = "unclosed-tag"
                                        )
                                    )
                                }
                                while (openTagStack.size > matchingIndex) {
                                    openTagStack.removeLast()
                                }
                            } else {
                                issues.add(
                                    LintIssue(
                                        lineNumber = lineNum,
                                        severity = LintSeverity.ERROR,
                                        message = "Unexpected closing tag </$tagName> without matching opening tag",
                                        rule = "mismatched-closing-tag",
                                        snippet = fullTag
                                    )
                                )
                            }
                        }
                    } else {
                        issues.add(
                            LintIssue(
                                lineNumber = lineNum,
                                severity = LintSeverity.ERROR,
                                message = "Unexpected closing tag </$tagName> with no open tags",
                                rule = "orphan-closing-tag",
                                snippet = fullTag
                            )
                        )
                    }
                } else if (!isSelfClosing) {
                    openTagStack.addLast(tagName to lineNum)
                }
            }
        }

        // Check any remaining unclosed tags
        for ((unclosedTag, openLine) in openTagStack) {
            issues.add(
                LintIssue(
                    lineNumber = openLine,
                    severity = LintSeverity.ERROR,
                    message = "Unclosed <$unclosedTag> tag (missing </$unclosedTag>)",
                    rule = "unclosed-tag"
                )
            )
        }

        // Global HTML structure checks if full page
        if (html.contains("<html", ignoreCase = true)) {
            if (!hasViewportMeta) {
                issues.add(
                    LintIssue(
                        lineNumber = 1,
                        severity = LintSeverity.INFO,
                        message = "Missing <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> for responsive mobile scaling",
                        rule = "viewport-meta"
                    )
                )
            }
            if (!hasTitleTag) {
                issues.add(
                    LintIssue(
                        lineNumber = 1,
                        severity = LintSeverity.INFO,
                        message = "Missing <title> tag inside <head>",
                        rule = "title-required"
                    )
                )
            }
        }

        return issues.sortedBy { it.lineNumber }
    }
}
