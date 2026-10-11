package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PRESET_COLORS = listOf(
    // Tailwind & Web Modern
    Color(0xFF3B82F6), // Blue 500
    Color(0xFF10B981), // Emerald 500
    Color(0xFF8B5CF6), // Purple 500
    Color(0xFFF59E0B), // Amber 500
    Color(0xFFEF4444), // Red 500
    Color(0xFF06B6D4), // Cyan 500
    Color(0xFFEC4899), // Pink 500
    Color(0xFF64748B), // Slate 500
    // Cyber & Neon
    Color(0xFF00FFCC), // Neon Cyan
    Color(0xFFFF007F), // Neon Magenta
    Color(0xFFFFEA00), // Neon Yellow
    Color(0xFF7928CA), // Electric Purple
    // Dark & Light neutrals
    Color(0xFF0F172A), // Dark slate
    Color(0xFF1E293B), // Medium slate
    Color(0xFFF8FAFC), // White smoke
    Color(0xFF000000)  // Pure black
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorPickerSheet(
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    var r by remember { mutableFloatStateOf(59f) }
    var g by remember { mutableFloatStateOf(130f) }
    var b by remember { mutableFloatStateOf(246f) }
    var formatMode by remember { mutableStateOf("HEX") } // HEX, RGB

    val currentColor = Color(r.toInt(), g.toInt(), b.toInt())
    val hexCode = String.format("#%02X%02X%02X", r.toInt(), g.toInt(), b.toInt())
    val rgbCode = "rgb(${r.toInt()}, ${g.toInt()}, ${b.toInt()})"

    val formattedString = if (formatMode == "HEX") hexCode else rgbCode

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "CSS Color Inspector",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pick or customize a color to inject directly into your HTML/CSS",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(16.dp))

            // Swatch Preview & Format Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(currentColor)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formattedString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = formatMode == "HEX",
                            onClick = { formatMode = "HEX" },
                            label = { Text("HEX") }
                        )
                        FilterChip(
                            selected = formatMode == "RGB",
                            onClick = { formatMode = "RGB" },
                            label = { Text("RGB") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sliders: R, G, B
            Text(text = "R: ${r.toInt()}", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            Slider(
                value = r,
                onValueChange = { r = it },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "G: ${g.toInt()}", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            Slider(
                value = g,
                onValueChange = { g = it },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "B: ${b.toInt()}", fontSize = 12.sp, color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
            Slider(
                value = b,
                onValueChange = { b = it },
                valueRange = 0f..255f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Quick Palettes
            Text(text = "Quick Palettes", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PRESET_COLORS.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable {
                                r = color.red * 255f
                                g = color.green * 255f
                                b = color.blue * 255f
                            }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onColorSelected(formattedString) },
                modifier = Modifier.fillMaxWidth().testTag("color_picker_insert_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Insert $formattedString into Code")
            }
        }
    }
}
