package com.example.editor

data class DomNode(
    val id: String,
    val tagName: String,
    val attributes: Map<String, String>,
    val textContent: String,
    val children: List<DomNode>,
    val line: Int,
    val depth: Int
) {
    val attributeSummary: String
        get() = attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }

    val selectorSummary: String
        get() {
            val idStr = attributes["id"]?.let { "#$it" } ?: ""
            val classStr = attributes["class"]?.split(" ")?.filter { it.isNotBlank() }?.joinToString("") { ".$it" } ?: ""
            return "$tagName$idStr$classStr"
        }
}

object DomParser {

    private val VOID_TAGS = setOf(
        "area", "base", "br", "col", "embed", "hr", "img", "input",
        "link", "meta", "param", "source", "track", "wbr"
    )

    fun parse(html: String): List<DomNode> {
        if (html.isBlank()) return emptyList()

        val tokens = tokenize(html)
        val rootNodes = mutableListOf<DomNode>()
        val stack = ArrayDeque<MutableDomNode>()
        var nodeCounter = 0

        for (token in tokens) {
            when (token) {
                is TagToken.Open -> {
                    val node = MutableDomNode(
                        id = "node_${nodeCounter++}",
                        tagName = token.name,
                        attributes = token.attributes,
                        line = token.line,
                        depth = stack.size
                    )
                    if (stack.isEmpty()) {
                        stack.addLast(node)
                    } else {
                        stack.last().children.add(node)
                        stack.addLast(node)
                    }
                }
                is TagToken.SelfClosing -> {
                    val node = MutableDomNode(
                        id = "node_${nodeCounter++}",
                        tagName = token.name,
                        attributes = token.attributes,
                        line = token.line,
                        depth = stack.size
                    )
                    if (stack.isEmpty()) {
                        rootNodes.add(node.toImmutable())
                    } else {
                        stack.last().children.add(node)
                    }
                }
                is TagToken.Close -> {
                    if (stack.isNotEmpty()) {
                        val matchingIndex = stack.indexOfLast { it.tagName.equals(token.name, ignoreCase = true) }
                        if (matchingIndex >= 0) {
                            while (stack.size > matchingIndex + 1) {
                                stack.removeLast()
                            }
                            val completed = stack.removeLast()
                            if (stack.isEmpty()) {
                                rootNodes.add(completed.toImmutable())
                            }
                        }
                    }
                }
                is TagToken.Text -> {
                    if (stack.isNotEmpty() && token.text.isNotBlank()) {
                        stack.last().textPieces.add(token.text.trim())
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val remaining = stack.removeFirst()
            rootNodes.add(remaining.toImmutable())
        }

        return rootNodes
    }

    private class MutableDomNode(
        val id: String,
        val tagName: String,
        val attributes: Map<String, String>,
        val line: Int,
        val depth: Int,
        val textPieces: MutableList<String> = mutableListOf(),
        val children: MutableList<MutableDomNode> = mutableListOf()
    ) {
        fun toImmutable(): DomNode = DomNode(
            id = id,
            tagName = tagName,
            attributes = attributes,
            textContent = textPieces.joinToString(" ").take(80),
            children = children.map { it.toImmutable() },
            line = line,
            depth = depth
        )
    }

    private sealed class TagToken {
        data class Open(val name: String, val attributes: Map<String, String>, val line: Int) : TagToken()
        data class Close(val name: String, val line: Int) : TagToken()
        data class SelfClosing(val name: String, val attributes: Map<String, String>, val line: Int) : TagToken()
        data class Text(val text: String) : TagToken()
    }

    private fun tokenize(html: String): List<TagToken> {
        val tokens = mutableListOf<TagToken>()
        val regex = Regex("""(<!--[\s\S]*?-->)|(<!DOCTYPE[^>]*>)|(<(/)?([a-zA-Z0-9\-]+)([^>]*)>)""")
        var lastIdx = 0

        val lines = html.lines()
        fun getLineNumber(offset: Int): Int {
            var charCount = 0
            for ((idx, line) in lines.withIndex()) {
                charCount += line.length + 1
                if (charCount > offset) return idx + 1
            }
            return lines.size
        }

        for (match in regex.findAll(html)) {
            val textBefore = html.substring(lastIdx, match.range.first)
            if (textBefore.isNotBlank()) {
                tokens.add(TagToken.Text(textBefore))
            }
            lastIdx = match.range.last + 1

            val isComment = match.groups[1] != null
            val isDoctype = match.groups[2] != null
            if (isComment || isDoctype) continue

            val isClosing = match.groups[4] != null
            val tagName = match.groups[5]?.value?.lowercase() ?: ""
            val attrStr = match.groups[6]?.value ?: ""
            val lineNum = getLineNumber(match.range.first)

            if (isClosing) {
                tokens.add(TagToken.Close(tagName, lineNum))
            } else {
                val isSelf = attrStr.trimEnd().endsWith("/") || VOID_TAGS.contains(tagName)
                val attrs = parseAttributes(attrStr)
                if (isSelf) {
                    tokens.add(TagToken.SelfClosing(tagName, attrs, lineNum))
                } else {
                    tokens.add(TagToken.Open(tagName, attrs, lineNum))
                }
            }
        }

        if (lastIdx < html.length) {
            val rem = html.substring(lastIdx)
            if (rem.isNotBlank()) {
                tokens.add(TagToken.Text(rem))
            }
        }

        return tokens
    }

    private fun parseAttributes(attrString: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val attrRegex = Regex("""([a-zA-Z0-9_:@.\-]+)(?:=\s*(?:"([^"]*)"|'([^']*)'|([^\s>]+)))?""")
        for (match in attrRegex.findAll(attrString)) {
            val key = match.groupValues[1]
            if (key == "/") continue
            val value = match.groupValues[2].ifEmpty {
                match.groupValues[3].ifEmpty {
                    match.groupValues[4]
                }
            }
            map[key] = value
        }
        return map
    }
}
