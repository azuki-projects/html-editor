package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotHistorySheet(
    snapshots: List<ProjectSnapshot>,
    onDismiss: () -> Unit,
    onCreateSnapshot: (String) -> Unit,
    onRestoreSnapshot: (ProjectSnapshot) -> Unit,
    onDeleteSnapshot: (ProjectSnapshot) -> Unit
) {
    var snapshotLabel by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }

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
                text = "Version History & Snapshots",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Save checkpoints of your code and time-travel back anytime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(16.dp))

            // Create new snapshot input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = snapshotLabel,
                    onValueChange = { snapshotLabel = it },
                    placeholder = { Text("Snapshot name (e.g. Before refactor)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("snapshot_label_input")
                )

                Button(
                    onClick = {
                        onCreateSnapshot(snapshotLabel)
                        snapshotLabel = ""
                    },
                    modifier = Modifier.testTag("snapshot_create_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.height(4.dp))
                    Text("Save")
                }
            }

            Spacer(Modifier.height(16.dp))

            if (snapshots.isEmpty()) {
                Text(
                    text = "No saved snapshots for this project yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(snapshots, key = { it.id }) { snap ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("snapshot_item_${snap.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = snap.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${dateFormat.format(Date(snap.timestamp))} • ${snap.htmlContent.length} chars",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onRestoreSnapshot(snap) },
                                        modifier = Modifier.testTag("snapshot_restore_${snap.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Restore,
                                            contentDescription = "Restore snapshot",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteSnapshot(snap) },
                                        modifier = Modifier.testTag("snapshot_delete_${snap.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete snapshot",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
