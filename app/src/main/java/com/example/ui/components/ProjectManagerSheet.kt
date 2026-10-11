package com.example.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectManagerSheet(
    projects: List<Project>,
    currentProject: Project?,
    onDismiss: () -> Unit,
    onSelectProject: (Project) -> Unit,
    onCreateProject: (String) -> Unit,
    onDuplicateCurrent: () -> Unit,
    onDeleteProject: (Project) -> Unit
) {
    var newProjectName by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

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
                text = "Projects & Documents",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage your web documents and experiments",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(16.dp))

            // Create new project row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newProjectName,
                    onValueChange = { newProjectName = it },
                    placeholder = { Text("New project title...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("new_project_name_input")
                )

                Button(
                    onClick = {
                        onCreateProject(newProjectName)
                        newProjectName = ""
                    },
                    modifier = Modifier.testTag("create_project_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.height(4.dp))
                    Text("New")
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(projects, key = { it.id }) { proj ->
                    val isSelected = proj.id == currentProject?.id
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProject(proj) }
                            .testTag("project_item_${proj.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(4.dp))
                                Column(modifier = Modifier.padding(start = 10.dp)) {
                                    Text(
                                        text = proj.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${dateFormat.format(Date(proj.updatedAt))} • ${if (proj.isMultiFile) "Multi-file" else "Single HTML"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            if (projects.size > 1) {
                                IconButton(
                                    onClick = { onDeleteProject(proj) },
                                    modifier = Modifier.testTag("delete_proj_${proj.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Project",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onDuplicateCurrent,
                modifier = Modifier.fillMaxWidth().testTag("duplicate_current_proj_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.height(4.dp))
                Text("Duplicate Current Document")
            }
        }
    }
}
