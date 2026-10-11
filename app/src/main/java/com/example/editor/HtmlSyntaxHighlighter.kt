package com.example.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.regex.Pattern

class HtmlSyntaxVisualTransformation(
    private val theme: EditorColorScheme
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = highlightHtml(text.text, theme)
        return TransformedText(highlighted, OffsetMapping.Identity)
    }

    companion object {
        private val DOCTYPE_REGEX = Pattern.compile("(?i)<!DOCTYPE[^>]*>", Pattern.CASE_INSENSITIVE)
        private val COMMENT_REGEX = Pattern.compile("<!--[\\s\\S]*?-->")
        private val TAG_REGEX = Pattern.compile("</?[a-zA-Z0-9\\-]+(?:\\s+[^>]*)?/?>")
        private val ATTR_NAME_REGEX = Pattern.compile("\\s+([a-zA-Z0-9_\\-:@.]+)\\s*=")
        private val ATTR_VAL_REGEX = Pattern.compile("=\\s*(\"[^\"]*\"|'[^']*')")
        private val ENTITY_REGEX = Pattern.compile("&[a-zA-Z0-9#]+;")
        private val SCRIPT_BLOCK_REGEX = Pattern.compile("<script[\\s\\S]*?>([\\s\\S]*?)</script>", Pattern.CASE_INSENSITIVE)
        private val STYLE_BLOCK_REGEX = Pattern.compile("<style[\\s\\S]*?>([\\s\\S]*?)</style>", Pattern.CASE_INSENSITIVE)

        // Basic JS keywords in script blocks
        private val JS_KEYWORD_REGEX = Pattern.compile("\\b(const|let|var|function|return|if|else|for|while|new|class|import|export|from|async|await|try|catch|finally|this)\\b")
        // Basic CSS properties in style blocks
        private val CSS_PROP_REGEX = Pattern.compile("(?m)^\\s*([a-zA-Z\\-]+)\\s*:")

        fun highlightHtml(raw: String, theme: EditorColorScheme): AnnotatedString {
            if (raw.isEmpty()) return AnnotatedString("")

            return buildAnnotatedString {
                append(raw)
                // Default base text style
                addStyle(SpanStyle(color = theme.textColor), 0, raw.length)

                // 1. Highlight HTML tags
                val tagMatcher = TAG_REGEX.matcher(raw)
                while (tagMatcher.find()) {
                    val start = tagMatcher.start()
                    val end = tagMatcher.end()
                    val tagStr = tagMatcher.group()

                    // Check if comment or doctype first
                    if (tagStr.startsWith("<!--") || tagStr.startsWith("<!DOCTYPE", ignoreCase = true)) {
                        continue
                    }

                    // Highlight brackets and tag name
                    val tagNameMatcher = Pattern.compile("</?([a-zA-Z0-9\\-]+)").matcher(tagStr)
                    if (tagNameMatcher.find()) {
                        val nameStart = start + tagNameMatcher.start()
                        val nameEnd = start + tagNameMatcher.end()
                        addStyle(SpanStyle(color = theme.tagColor), nameStart, nameEnd)
                    }
                    // Closing bracket > or />
                    val closeBracketStart = if (tagStr.endsWith("/>")) end - 2 else end - 1
                    if (closeBracketStart >= start) {
                        addStyle(SpanStyle(color = theme.tagColor), closeBracketStart, end)
                    }

                    // Attribute names within this tag
                    val attrMatcher = ATTR_NAME_REGEX.matcher(tagStr)
                    while (attrMatcher.find()) {
                        val attrName = attrMatcher.group(1)
                        if (attrName != null) {
                            val aStart = start + attrMatcher.start(1)
                            val aEnd = start + attrMatcher.end(1)
                            if (aStart in 0..raw.length && aEnd in 0..raw.length) {
                                addStyle(SpanStyle(color = theme.attributeNameColor), aStart, aEnd)
                            }
                        }
                    }

                    // Attribute values within this tag
                    val valMatcher = ATTR_VAL_REGEX.matcher(tagStr)
                    while (valMatcher.find()) {
                        val vGroup = valMatcher.group(1)
                        if (vGroup != null) {
                            val vStart = start + valMatcher.start(1)
                            val vEnd = start + valMatcher.end(1)
                            if (vStart in 0..raw.length && vEnd in 0..raw.length) {
                                addStyle(SpanStyle(color = theme.attributeValueColor), vStart, vEnd)
                            }
                        }
                    }
                }

                // 2. CSS blocks inside <style>
                val styleMatcher = STYLE_BLOCK_REGEX.matcher(raw)
                while (styleMatcher.find()) {
                    val cssBody = styleMatcher.group(1) ?: continue
                    val cssStart = styleMatcher.start(1)
                    val propMatcher = CSS_PROP_REGEX.matcher(cssBody)
                    while (propMatcher.find()) {
                        val pStart = cssStart + propMatcher.start(1)
                        val pEnd = cssStart + propMatcher.end(1)
                        if (pStart in 0..raw.length && pEnd in 0..raw.length) {
                            addStyle(SpanStyle(color = theme.attributeNameColor), pStart, pEnd)
                        }
                    }
                }

                // 3. JS blocks inside <script>
                val scriptMatcher = SCRIPT_BLOCK_REGEX.matcher(raw)
                while (scriptMatcher.find()) {
                    val jsBody = scriptMatcher.group(1) ?: continue
                    val jsStart = scriptMatcher.start(1)
                    val kwMatcher = JS_KEYWORD_REGEX.matcher(jsBody)
                    while (kwMatcher.find()) {
                        val kStart = jsStart + kwMatcher.start(1)
                        val kEnd = jsStart + kwMatcher.end(1)
                        if (kStart in 0..raw.length && kEnd in 0..raw.length) {
                            addStyle(SpanStyle(color = theme.keywordColor), kStart, kEnd)
                        }
                    }
                }

                // 4. HTML Entities
                val entityMatcher = ENTITY_REGEX.matcher(raw)
                while (entityMatcher.find()) {
                    addStyle(
                        SpanStyle(color = theme.entityColor),
                        entityMatcher.start(),
                        entityMatcher.end()
                    )
                }

                // 5. DOCTYPE
                val docMatcher = DOCTYPE_REGEX.matcher(raw)
                while (docMatcher.find()) {
                    addStyle(
                        SpanStyle(color = theme.doctypeColor),
                        docMatcher.start(),
                        docMatcher.end()
                    )
                }

                // 6. Comments (highest precedence - overrides styles)
                val commentMatcher = COMMENT_REGEX.matcher(raw)
                while (commentMatcher.find()) {
                    addStyle(
                        SpanStyle(color = theme.commentColor),
                        commentMatcher.start(),
                        commentMatcher.end()
                    )
                }
            }
        }
    }
}
