package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConsoleLogItem

@Composable
fun ConsoleView(
    logs: List<ConsoleLogItem>,
    onClearLogs: () -> Unit,
    onEvaluateJs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var jsInput by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, selectedFilter) {
        when (selectedFilter) {
            "LOG" -> logs.filter { it.level == "LOG" || it.level == "INFO" }
            "WARN" -> logs.filter { it.level == "WARN" }
            "ERROR" -> logs.filter { it.level == "ERROR" }
            else -> logs
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Controls Header
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${logs.size})", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "LOG",
                        onClick = { selectedFilter = "LOG" },
                        label = { Text("Logs", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "WARN",
                        onClick = { selectedFilter = "WARN" },
                        label = { Text("Warn (${logs.count { it.level == "WARN" }})", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "ERROR",
                        onClick = { selectedFilter = "ERROR" },
                        label = { Text("Err (${logs.count { it.level == "ERROR" }})", fontSize = 12.sp) }
                    )
                }

                IconButton(onClick = onClearLogs, modifier = Modifier.testTag("console_clear_button")) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Console", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }

        // Logs Output Area
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Console is empty",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "console.log() output and JS errors appear here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    ConsoleLogLine(log)
                }
            }
        }

        // Interactive JS REPL Execution Input
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ">",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                OutlinedTextField(
                    value = jsInput,
                    onValueChange = { jsInput = it },
                    placeholder = { Text("Run JavaScript (e.g. document.title)", fontSize = 13.sp) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1f).testTag("console_js_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                IconButton(
                    onClick = {
                        if (jsInput.isNotBlank()) {
                            onEvaluateJs(jsInput)
                            jsInput = ""
                        }
                    },
                    modifier = Modifier.testTag("console_js_run_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run JavaScript", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ConsoleLogLine(log: ConsoleLogItem) {
    val (badgeColor, textColor) = when (log.level) {
        "ERROR" -> Color(0xFFEF4444) to Color(0xFFFCA5A5)
        "WARN" -> Color(0xFFF59E0B) to Color(0xFFFDE68A)
        "INFO" -> Color(0xFF3B82F6) to Color(0xFF93C5FD)
        else -> Color(0xFF10B981) to MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.message,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = textColor
                )
                if (log.source.isNotBlank() && log.lineNumber > 0) {
                    Text(
                        text = "${log.source}:${log.lineNumber}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
