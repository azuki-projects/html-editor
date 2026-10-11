package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LocalServerDialog(
    isRunning: Boolean,
    currentPort: Int,
    serverUrl: String,
    useLocalhostInPreview: Boolean,
    onTogglePreviewMode: (Boolean) -> Unit,
    onStartServer: (Int) -> Unit,
    onStopServer: () -> Unit,
    onChangePort: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var portInputText by remember { mutableStateOf(currentPort.toString()) }
    val presetPorts = listOf(3000, 5500, 8000, 8080, 8888)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Dns,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Localhost Web Server", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Serve web projects directly over HTTP on your device",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status & Live URL Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isRunning) Color(0xFF10B981) else Color(0xFFEF4444))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isRunning) "SERVER ACTIVE" else "SERVER STOPPED",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRunning) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            }

                            if (isRunning) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(serverUrl))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(32.dp).testTag("open_external_browser_btn")
                                ) {
                                    Icon(
                                        Icons.Default.OpenInBrowser,
                                        contentDescription = "Open in Chrome/Browser",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // URL box with copy button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRunning) serverUrl else "http://localhost:$portInputText/ (Stopped)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Localhost URL", serverUrl))
                                        Toast.makeText(context, "URL copied: $serverUrl", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp).testTag("copy_url_btn")
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy URL",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Port Configuration
                Column {
                    Text(
                        text = "Server Port (1024 - 65535)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = portInputText,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() } && input.length <= 5) {
                                    portInputText = input
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("localhost_port_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                val p = portInputText.toIntOrNull() ?: 8080
                                onChangePort(p)
                                Toast.makeText(context, "Port set to $p", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("apply_port_btn")
                        ) {
                            Text("Apply")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Preset Quick Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        presetPorts.forEach { preset ->
                            FilterChip(
                                selected = portInputText == preset.toString(),
                                onClick = {
                                    portInputText = preset.toString()
                                    onChangePort(preset)
                                },
                                label = { Text(":$preset", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                                modifier = Modifier.testTag("port_preset_$preset")
                            )
                        }
                    }
                }

                // Toggle Preview mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Route Live Preview via Localhost",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Loads preview over http://localhost:$currentPort/ instead of raw HTML data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Switch(
                        checked = useLocalhostInPreview,
                        onCheckedChange = onTogglePreviewMode,
                        modifier = Modifier.testTag("route_preview_localhost_switch")
                    )
                }

                // Start / Stop Server Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isRunning) {
                        Button(
                            onClick = onStopServer,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("stop_server_btn")
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Stop Server")
                        }

                        OutlinedButton(
                            onClick = {
                                val p = portInputText.toIntOrNull() ?: currentPort
                                onStartServer(p)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("restart_server_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Restart")
                        }
                    } else {
                        Button(
                            onClick = {
                                val p = portInputText.toIntOrNull() ?: currentPort
                                onStartServer(p)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("start_server_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Start Localhost Server")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
