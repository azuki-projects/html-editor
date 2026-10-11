package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    projects: List<Project>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenProject: (Project) -> Unit,
    onCreateNewProject: (String) -> Unit,
    onDuplicateProject: (Project) -> Unit,
    onDeleteProject: (Project) -> Unit,
    onImportHtml: (fileName: String, content: String) -> Unit,
    onExportProjectToUri: (Uri) -> Unit,
    onShareProject: (Project) -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenGuides: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var newProjectTitle by remember { mutableStateOf("") }
    var projectToExport by remember { mutableStateOf<Project?>(null) }

    // SAF File Picker for Importing HTML files from device storage
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val content = BufferedReader(InputStreamReader(inputStream)).readText()
                    val fileName = it.lastPathSegment?.substringAfterLast('/') ?: "imported.html"
                    onImportHtml(fileName, content)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // SAF File Creator for saving HTML file to external storage
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri: Uri? ->
        uri?.let { onExportProjectToUri(it) }
    }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.htmlContent.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "</>",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "WebForge Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Made with Azuki",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings, modifier = Modifier.testTag("home_settings_btn")) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Search Field
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search projects, HTML tags or code...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("home_search_input")
                )
            }

            // Quick Hub Actions
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showNewProjectDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_create_project_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("New Project")
                    }

                    FilledTonalButton(
                        onClick = { importFileLauncher.launch(arrayOf("text/html", "text/*", "*/*")) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_import_html_btn")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Import .html")
                    }

                    OutlinedButton(
                        onClick = onOpenTemplates,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_templates_btn")
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Templates")
                    }

                    OutlinedButton(
                        onClick = onOpenGuides,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_guides_btn")
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Guides & Docs")
                    }
                }
            }

            // Projects Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Projects (${filteredProjects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Projects List
            if (filteredProjects.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No matching projects found" else "No projects yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Create a project or load one of the starter templates to begin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { proj ->
                    ProjectCardItem(
                        project = proj,
                        dateFormat = dateFormat,
                        onOpen = { onOpenProject(proj) },
                        onDuplicate = { onDuplicateProject(proj) },
                        onExportSave = {
                            projectToExport = proj
                            exportFileLauncher.launch("${proj.title.replace(" ", "_")}.html")
                        },
                        onShare = { onShareProject(proj) },
                        onDelete = { onDeleteProject(proj) }
                    )
                }
            }

            // "Made with Azuki" Footer Card
            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("made_with_azuki_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Made with Azuki",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Crafted with passion for web developers on Android",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "v2.0 • 15 Pro Developer Features • Full Offline Support",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Create New Project Dialog
    if (showNewProjectDialog) {
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("New Web Project") },
            text = {
                OutlinedTextField(
                    value = newProjectTitle,
                    onValueChange = { newProjectTitle = it },
                    placeholder = { Text("Project Title (e.g. Portfolio)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_project_dialog_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateNewProject(newProjectTitle)
                        newProjectTitle = ""
                        showNewProjectDialog = false
                    },
                    modifier = Modifier.testTag("new_project_dialog_confirm")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    dateFormat: SimpleDateFormat,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onExportSave: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (project.isMultiFile) "Multi-file" else "HTML",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Metadata: chars & last updated
            Text(
                text = "${project.htmlContent.length} chars • Updated ${dateFormat.format(Date(project.updatedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(10.dp))

            // Code Preview Teaser
            val previewSnippet = project.htmlContent.lines()
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("\n")
                .take(120)

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = previewSnippet.ifBlank { "<!-- Empty document -->" },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("open_project_btn_${project.id}")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Open Editor", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Save to External Storage
                    IconButton(
                        onClick = onExportSave,
                        modifier = Modifier.testTag("export_save_btn_${project.id}")
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Save to device external storage",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Duplicate
                    IconButton(onClick = onDuplicate) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Duplicate",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Share
                    IconButton(onClick = onShare) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Delete
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
