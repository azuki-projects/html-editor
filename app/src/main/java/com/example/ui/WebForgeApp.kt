package com.example.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.editor.EditorThemes
import com.example.ui.components.CdnLibraryDialog
import com.example.ui.components.ColorPickerSheet
import com.example.ui.components.ConsoleView
import com.example.ui.components.DomInspectorView
import com.example.ui.components.EditorGutterTextField
import com.example.ui.components.FindReplaceBar
import com.example.ui.components.LinterView
import com.example.ui.components.LiveWebViewPreview
import com.example.ui.components.LocalServerDialog
import com.example.ui.components.ProjectManagerSheet
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SnapshotHistorySheet
import com.example.ui.components.TagToolbar
import com.example.ui.components.TemplateChooserDialog
import com.example.ui.screens.GuidesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebForgeApp(
    viewModel: EditorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isLoadingProject by viewModel.isLoadingProject.collectAsState()
    val loadingProgress by viewModel.loadingProgress.collectAsState()
    val loadingStatusText by viewModel.loadingStatusText.collectAsState()
    val homeSearchQuery by viewModel.homeSearchQuery.collectAsState()

    val currentProject by viewModel.currentProject.collectAsState()
    val allProjects by viewModel.projects.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val activeFileTab by viewModel.activeFileTab.collectAsState()
    val textFieldValue by viewModel.textFieldValue.collectAsState()
    val cssTextFieldValue by viewModel.cssTextFieldValue.collectAsState()
    val jsTextFieldValue by viewModel.jsTextFieldValue.collectAsState()

    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val viewportMode by viewModel.viewportMode.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()
    val linterIssues by viewModel.linterIssues.collectAsState()
    val domTree by viewModel.domTree.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val snapshots by viewModel.snapshots.collectAsState()

    val isSearchVisible by viewModel.isSearchVisible.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val replaceQuery by viewModel.replaceQuery.collectAsState()
    val searchMatches by viewModel.searchMatchIndices.collectAsState()
    val currentMatchIndex by viewModel.currentMatchIndex.collectAsState()

    val showTemplates by viewModel.showTemplatesDialog.collectAsState()
    val showCdn by viewModel.showCdnDialog.collectAsState()
    val showColorPicker by viewModel.showColorPicker.collectAsState()
    val showSettings by viewModel.showSettingsDialog.collectAsState()
    val showProjectsSheet by viewModel.showProjectsSheet.collectAsState()
    val showSnapshotsSheet by viewModel.showSnapshotsSheet.collectAsState()
    val previewRefreshTrigger by viewModel.previewRefreshTrigger.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val isLocalServerRunning by viewModel.isLocalServerRunning.collectAsState()
    val localServerPort by viewModel.localServerPort.collectAsState()
    val localServerUrl by viewModel.localServerUrl.collectAsState()
    val useLocalhostInPreview by viewModel.useLocalhostInPreview.collectAsState()
    val showLocalServerDialog by viewModel.showLocalServerDialog.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    // External Save Document Launcher
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportHtmlToUri(context, it) }
    }

    val editorTheme = remember(settings.theme) { EditorThemes.forMode(settings.theme) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // 1. Initial Splash Screen
    if (currentScreen == AppScreen.SPLASH) {
        LoadingScreen(
            statusText = "Loading WebForge Studio...",
            progress = 0f,
            isInitialSplash = true,
            onSplashComplete = { viewModel.finishSplash() }
        )
        return
    }

    // 2. Project Loading Screen
    if (isLoadingProject) {
        LoadingScreen(
            statusText = loadingStatusText,
            progress = loadingProgress,
            isInitialSplash = false
        )
        return
    }

    // 3. Home Screen (Projects Hub)
    if (currentScreen == AppScreen.HOME) {
        HomeScreen(
            projects = allProjects,
            searchQuery = homeSearchQuery,
            onSearchChange = { viewModel.setHomeSearchQuery(it) },
            onOpenProject = { viewModel.loadAndOpenProject(it) },
            onCreateNewProject = { title -> viewModel.createNewProject(title) },
            onDuplicateProject = { proj ->
                viewModel.selectProject(proj)
                viewModel.duplicateCurrentProject()
            },
            onDeleteProject = { viewModel.deleteProject(it) },
            onImportHtml = { name, content -> viewModel.importHtmlFile(name, content) },
            onExportProjectToUri = { uri -> viewModel.exportHtmlToUri(context, uri) },
            onShareProject = { proj ->
                viewModel.selectProject(proj)
                viewModel.shareHtmlCode(context)
            },
            onOpenTemplates = { viewModel.setTemplatesDialogVisible(true) },
            onOpenGuides = { viewModel.navigateTo(AppScreen.GUIDES) },
            onOpenSettings = { viewModel.setSettingsDialogVisible(true) }
        )

        // Show modals if opened from Home
        if (showTemplates) {
            TemplateChooserDialog(
                onDismiss = { viewModel.setTemplatesDialogVisible(false) },
                onSelectTemplate = { template ->
                    viewModel.loadTemplate(template)
                    viewModel.navigateTo(AppScreen.EDITOR)
                }
            )
        }

        if (showSettings) {
            SettingsDialog(
                currentSettings = settings,
                onDismiss = { viewModel.setSettingsDialogVisible(false) },
                onSaveSettings = { updated -> viewModel.updateSettings(updated) }
            )
        }
        return
    }

    // 4. Guides Screen
    if (currentScreen == AppScreen.GUIDES) {
        GuidesScreen(
            onBack = { viewModel.navigateTo(AppScreen.HOME) },
            onCreateProjectFromGuide = { guide -> viewModel.createProjectFromGuide(guide) }
        )
        return
    }

    // 5. Editor Screen (with BackHandler to return to Home)
    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("editor_back_to_home_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.setProjectsSheetVisible(true) }
                            .padding(vertical = 4.dp)
                            .testTag("app_bar_title_row")
                    ) {
                        Column {
                            Text(
                                text = currentProject?.title ?: "WebForge Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(9.dp)
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = "Made with Azuki",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Undo
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.testTag("action_undo_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    // Redo
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.testTag("action_redo_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }

                    // Search & Replace
                    IconButton(
                        onClick = { viewModel.toggleSearch() },
                        modifier = Modifier.testTag("action_search_btn")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search & Replace")
                    }

                    // Format code
                    IconButton(
                        onClick = { viewModel.formatCurrentCode() },
                        modifier = Modifier.testTag("action_format_code_btn")
                    ) {
                        Icon(Icons.Default.FormatAlignLeft, contentDescription = "Beautify HTML")
                    }

                    // Overflow Menu
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("action_more_menu_btn")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Features")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save to Device (.html)") },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                val defaultName = "${currentProject?.title?.replace(" ", "_") ?: "webforge"}.html"
                                exportFileLauncher.launch(defaultName)
                            },
                            modifier = Modifier.testTag("menu_save_to_device")
                        )

                        DropdownMenuItem(
                            text = { Text("Localhost Server (:$localServerPort)") },
                            leadingIcon = { Icon(Icons.Default.Dns, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setLocalServerDialogVisible(true)
                            },
                            modifier = Modifier.testTag("menu_localhost_server")
                        )

                        DropdownMenuItem(
                            text = { Text("Share / Export HTML") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.shareHtmlCode(context)
                            },
                            modifier = Modifier.testTag("menu_share")
                        )

                        DropdownMenuItem(
                            text = { Text("Starter Templates") },
                            leadingIcon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setTemplatesDialogVisible(true)
                            },
                            modifier = Modifier.testTag("menu_templates")
                        )

                        DropdownMenuItem(
                            text = { Text("CDN Libraries & Fonts") },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setCdnDialogVisible(true)
                            },
                            modifier = Modifier.testTag("menu_cdn")
                        )

                        DropdownMenuItem(
                            text = { Text("CSS Color Picker") },
                            leadingIcon = { Icon(Icons.Default.ColorLens, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setColorPickerVisible(true)
                            },
                            modifier = Modifier.testTag("menu_color_picker")
                        )

                        DropdownMenuItem(
                            text = { Text("Snapshots & History (${snapshots.size})") },
                            leadingIcon = { Icon(Icons.Default.BookmarkBorder, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setSnapshotsSheetVisible(true)
                            },
                            modifier = Modifier.testTag("menu_snapshots")
                        )

                        DropdownMenuItem(
                            text = { Text(if (currentProject?.isMultiFile == true) "Switch to Single HTML" else "Switch to Multi-file (HTML/CSS/JS)") },
                            leadingIcon = { Icon(Icons.Default.ViewQuilt, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.toggleMultiFileMode()
                            },
                            modifier = Modifier.testTag("menu_toggle_multifile")
                        )

                        DropdownMenuItem(
                            text = { Text("Save Document") },
                            leadingIcon = { Icon(Icons.Default.Save, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.saveCurrentProjectExplicitly()
                            },
                            modifier = Modifier.testTag("menu_save")
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("Preferences & Themes") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.setSettingsDialogVisible(true)
                            },
                            modifier = Modifier.testTag("menu_settings")
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                // Tab 1: Editor
                NavigationBarItem(
                    selected = activeTab == EditorTab.CODE,
                    onClick = { viewModel.setActiveTab(EditorTab.CODE) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Editor") },
                    label = { Text("Editor") },
                    modifier = Modifier.testTag("nav_tab_editor")
                )

                // Tab 2: Live Preview
                NavigationBarItem(
                    selected = activeTab == EditorTab.PREVIEW,
                    onClick = { viewModel.setActiveTab(EditorTab.PREVIEW) },
                    icon = { Icon(Icons.Outlined.Visibility, contentDescription = "Preview") },
                    label = { Text("Preview") },
                    modifier = Modifier.testTag("nav_tab_preview")
                )

                // Tab 3: DOM Inspector
                NavigationBarItem(
                    selected = activeTab == EditorTab.DOM,
                    onClick = { viewModel.setActiveTab(EditorTab.DOM) },
                    icon = { Icon(Icons.Default.ViewQuilt, contentDescription = "DOM Inspector") },
                    label = { Text("DOM") },
                    modifier = Modifier.testTag("nav_tab_dom")
                )

                // Tab 4: Console
                val errorCount = consoleLogs.count { it.level == "ERROR" }
                NavigationBarItem(
                    selected = activeTab == EditorTab.CONSOLE,
                    onClick = { viewModel.setActiveTab(EditorTab.CONSOLE) },
                    icon = {
                        if (errorCount > 0) {
                            BadgedBox(badge = { Badge { Text("$errorCount") } }) {
                                Icon(Icons.Default.Terminal, contentDescription = "Console")
                            }
                        } else {
                            Icon(Icons.Default.Terminal, contentDescription = "Console")
                        }
                    },
                    label = { Text("Console") },
                    modifier = Modifier.testTag("nav_tab_console")
                )

                // Tab 5: Linter Diagnostics
                val issuesCount = linterIssues.size
                NavigationBarItem(
                    selected = activeTab == EditorTab.LINTER,
                    onClick = { viewModel.setActiveTab(EditorTab.LINTER) },
                    icon = {
                        if (issuesCount > 0) {
                            BadgedBox(badge = { Badge { Text("$issuesCount") } }) {
                                Icon(Icons.Default.BugReport, contentDescription = "Diagnostics")
                            }
                        } else {
                            Icon(Icons.Default.BugReport, contentDescription = "Diagnostics")
                        }
                    },
                    label = { Text("Audit") },
                    modifier = Modifier.testTag("nav_tab_linter")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Find and Replace Bar
            FindReplaceBar(
                isVisible = isSearchVisible,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                replaceQuery = replaceQuery,
                onReplaceQueryChange = { viewModel.setReplaceQuery(it) },
                matchCount = searchMatches.size,
                currentMatchIndex = currentMatchIndex,
                onNextMatch = { viewModel.nextMatch() },
                onPreviousMatch = { viewModel.previousMatch() },
                onReplaceCurrent = { viewModel.replaceCurrentMatch() },
                onReplaceAll = { viewModel.replaceAllMatches() },
                onClose = { viewModel.toggleSearch() }
            )

            // Multi-file tabs if enabled
            if (activeTab == EditorTab.CODE && currentProject?.isMultiFile == true) {
                TabRow(
                    selectedTabIndex = activeFileTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Tab(
                        selected = activeFileTab == FileSubTab.HTML,
                        onClick = { viewModel.setActiveFileTab(FileSubTab.HTML) },
                        text = { Text("index.html") }
                    )
                    Tab(
                        selected = activeFileTab == FileSubTab.CSS,
                        onClick = { viewModel.setActiveFileTab(FileSubTab.CSS) },
                        text = { Text("style.css") }
                    )
                    Tab(
                        selected = activeFileTab == FileSubTab.JS,
                        onClick = { viewModel.setActiveFileTab(FileSubTab.JS) },
                        text = { Text("script.js") }
                    )
                }
            }

            // Main Active View Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (activeTab) {
                    EditorTab.CODE -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Code Editor
                            val currentEditValue = when (activeFileTab) {
                                FileSubTab.HTML -> textFieldValue
                                FileSubTab.CSS -> cssTextFieldValue
                                FileSubTab.JS -> jsTextFieldValue
                            }
                            val onCurrentEditChange = when (activeFileTab) {
                                FileSubTab.HTML -> viewModel::onHtmlChange
                                FileSubTab.CSS -> viewModel::onCssChange
                                FileSubTab.JS -> viewModel::onJsChange
                            }

                            EditorGutterTextField(
                                value = currentEditValue,
                                onValueChange = onCurrentEditChange,
                                theme = editorTheme,
                                fontSizeSp = settings.fontSizeSp,
                                showLineNumbers = settings.showLineNumbers,
                                wordWrap = settings.wordWrap,
                                modifier = Modifier.weight(1f)
                            )

                            // Tag & Snippets Toolbar
                            TagToolbar(
                                onSnippetClick = { viewModel.insertSnippet(it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    EditorTab.PREVIEW -> {
                        LiveWebViewPreview(
                            htmlContent = viewModel.getRunnableHtml(),
                            viewportMode = viewportMode,
                            onViewportChange = { viewModel.setViewportMode(it) },
                            refreshTrigger = previewRefreshTrigger,
                            onConsoleMessage = { level, msg, src, line ->
                                viewModel.addConsoleLog(level, msg, src, line)
                            },
                            isLocalServerRunning = isLocalServerRunning,
                            localServerPort = localServerPort,
                            localServerUrl = localServerUrl,
                            useLocalhost = useLocalhostInPreview,
                            onOpenLocalServerDialog = { viewModel.setLocalServerDialogVisible(true) }
                        )
                    }

                    EditorTab.DOM -> {
                        DomInspectorView(
                            nodes = domTree,
                            onJumpToLine = { line -> viewModel.jumpToLine(line) }
                        )
                    }

                    EditorTab.CONSOLE -> {
                        ConsoleView(
                            logs = consoleLogs,
                            onClearLogs = { viewModel.clearConsole() },
                            onEvaluateJs = { expr ->
                                viewModel.addConsoleLog("INFO", "> $expr", "repl", 0)
                                viewModel.showToast("Command sent to preview")
                            }
                        )
                    }

                    EditorTab.LINTER -> {
                        LinterView(
                            issues = linterIssues,
                            onJumpToLine = { line -> viewModel.jumpToLine(line) }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs & Sheets
    if (showTemplates) {
        TemplateChooserDialog(
            onDismiss = { viewModel.setTemplatesDialogVisible(false) },
            onSelectTemplate = { template -> viewModel.loadTemplate(template) }
        )
    }

    if (showCdn) {
        CdnLibraryDialog(
            onDismiss = { viewModel.setCdnDialogVisible(false) },
            onInject = { cdn -> viewModel.injectCdnLibrary(cdn) }
        )
    }

    if (showColorPicker) {
        ColorPickerSheet(
            onDismiss = { viewModel.setColorPickerVisible(false) },
            onColorSelected = { colorStr -> viewModel.insertColorCode(colorStr) }
        )
    }

    if (showSettings) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { viewModel.setSettingsDialogVisible(false) },
            onSaveSettings = { updated -> viewModel.updateSettings(updated) }
        )
    }

    if (showProjectsSheet) {
        ProjectManagerSheet(
            projects = allProjects,
            currentProject = currentProject,
            onDismiss = { viewModel.setProjectsSheetVisible(false) },
            onSelectProject = { proj ->
                viewModel.selectProject(proj)
                viewModel.setProjectsSheetVisible(false)
            },
            onCreateProject = { name -> viewModel.createNewProject(name) },
            onDuplicateCurrent = { viewModel.duplicateCurrentProject() },
            onDeleteProject = { proj -> viewModel.deleteProject(proj) }
        )
    }

    if (showSnapshotsSheet) {
        SnapshotHistorySheet(
            snapshots = snapshots,
            onDismiss = { viewModel.setSnapshotsSheetVisible(false) },
            onCreateSnapshot = { label -> viewModel.createSnapshot(label) },
            onRestoreSnapshot = { snap -> viewModel.restoreSnapshot(snap) },
            onDeleteSnapshot = { snap -> viewModel.deleteSnapshot(snap) }
        )
    }

    if (showLocalServerDialog) {
        LocalServerDialog(
            isRunning = isLocalServerRunning,
            currentPort = localServerPort,
            serverUrl = localServerUrl,
            useLocalhostInPreview = useLocalhostInPreview,
            onTogglePreviewMode = { viewModel.setUseLocalhostInPreview(it) },
            onStartServer = { port -> viewModel.startLocalServer(port) },
            onStopServer = { viewModel.stopLocalServer() },
            onChangePort = { port -> viewModel.changeLocalServerPort(port) },
            onDismiss = { viewModel.setLocalServerDialogVisible(false) }
        )
    }
}
