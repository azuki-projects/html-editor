package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.EditorColorScheme
import com.example.editor.HtmlSyntaxVisualTransformation

@Composable
fun EditorGutterTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    theme: EditorColorScheme,
    fontSizeSp: Int,
    showLineNumbers: Boolean,
    wordWrap: Boolean,
    modifier: Modifier = Modifier
) {
    val visualTransformation = remember(theme) { HtmlSyntaxVisualTransformation(theme) }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.5).sp,
        color = theme.textColor
    )

    val lines = remember(value.text) {
        val count = value.text.count { it == '\n' } + 1
        (1..count).toList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            // Line numbers gutter
            if (showLineNumbers) {
                val gutterWidth = (lines.size.toString().length * 10 + 24).dp.coerceAtLeast(36.dp)
                Box(
                    modifier = Modifier
                        .width(gutterWidth)
                        .fillMaxHeight()
                        .background(theme.gutterBackground)
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                ) {
                    val lineNumbersText = lines.joinToString("\n") { "$it" }
                    Text(
                        text = lineNumbersText,
                        style = textStyle.copy(
                            color = theme.lineNumberColor,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("line_numbers_gutter")
                    )
                }
            }

            // Code Editor input field
            val codeModifier = if (wordWrap) {
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            } else {
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(horizontalScrollState)
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .widthIn(min = 800.dp)
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = codeModifier.testTag("html_code_editor_input"),
                textStyle = textStyle,
                cursorBrush = SolidColor(theme.tagColor),
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (value.text.isEmpty()) {
                            Text(
                                text = "<!-- Start writing HTML, CSS & JavaScript here... -->",
                                style = textStyle.copy(color = theme.lineNumberColor)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}
