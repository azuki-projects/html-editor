package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EditorSettings
import com.example.data.model.EditorThemeMode

@Composable
fun SettingsDialog(
    currentSettings: EditorSettings,
    onDismiss: () -> Unit,
    onSaveSettings: (EditorSettings) -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentSettings.theme) }
    var fontSize by remember { mutableFloatStateOf(currentSettings.fontSizeSp.toFloat()) }
    var showLineNumbers by remember { mutableStateOf(currentSettings.showLineNumbers) }
    var wordWrap by remember { mutableStateOf(currentSettings.wordWrap) }
    var tabSpaces by remember { mutableIntStateOf(currentSettings.tabSpaces) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editor Preferences", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Mode Selector
                Column {
                    Text(
                        text = "Syntax Theme",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        EditorThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = selectedTheme == mode,
                                onClick = { selectedTheme = mode },
                                label = { Text(mode.displayName) },
                                modifier = Modifier.fillMaxWidth().testTag("theme_chip_${mode.name}")
                            )
                        }
                    }
                }

                // Font Size Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Font Size", style = MaterialTheme.typography.titleSmall)
                        Text(text = "${fontSize.toInt()} sp", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 11f..22f,
                        steps = 10,
                        modifier = Modifier.testTag("font_size_slider")
                    )
                }

                // Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Line Numbers Gutter", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = showLineNumbers,
                        onCheckedChange = { showLineNumbers = it },
                        modifier = Modifier.testTag("line_numbers_switch")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Word Wrap", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = wordWrap,
                        onCheckedChange = { wordWrap = it },
                        modifier = Modifier.testTag("word_wrap_switch")
                    )
                }

                // Tab Spaces
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tab Indent Spaces", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = tabSpaces == 2,
                            onClick = { tabSpaces = 2 },
                            label = { Text("2") }
                        )
                        FilterChip(
                            selected = tabSpaces == 4,
                            onClick = { tabSpaces = 4 },
                            label = { Text("4") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSettings(
                        currentSettings.copy(
                            theme = selectedTheme,
                            fontSizeSp = fontSize.toInt(),
                            showLineNumbers = showLineNumbers,
                            wordWrap = wordWrap,
                            tabSpaces = tabSpaces
                        )
                    )
                },
                modifier = Modifier.testTag("save_settings_btn")
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
