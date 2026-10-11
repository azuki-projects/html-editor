package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.EditorSettings
import com.example.data.model.EditorThemeMode
import com.example.data.model.Project
import com.example.data.model.ProjectSnapshot
import com.example.data.repository.ProjectRepository
import com.example.editor.CdnLibrary
import com.example.editor.CodeSnippets
import com.example.editor.DomNode
import com.example.editor.DomParser
import com.example.editor.HtmlFormatter
import com.example.editor.HtmlLinter
import com.example.editor.LintIssue
import com.example.editor.QuickSnippet
import com.example.editor.StarterTemplate
import com.example.editor.WebGuideItem
import com.example.server.LocalWebServer
import com.example.server.ServedWebContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter

enum class AppScreen {
    SPLASH,
    HOME,
    EDITOR,
    GUIDES
}

enum class EditorTab(val title: String) {
    CODE("Editor"),
    PREVIEW("Preview"),
    DOM("DOM Tree"),
    CONSOLE("Console"),
    LINTER("Diagnostics")
}

enum class FileSubTab {
    HTML,
    CSS,
    JS
}

enum class ViewportMode(val title: String, val widthDp: Int?) {
    RESPONSIVE("Responsive", null),
    MOBILE("Mobile (375px)", 375),
    TABLET("Tablet (768px)", 768)
}

data class ConsoleLogItem(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val level: String, // "LOG", "WARN", "ERROR", "INFO"
    val message: String,
    val source: String = "",
    val lineNumber: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository

    private val localWebServer = LocalWebServer(viewModelScope) { level, msg ->
        addConsoleLog(level, msg, "localhost", 0)
    }

    val isLocalServerRunning: StateFlow<Boolean> = localWebServer.isRunning
    val localServerPort: StateFlow<Int> = localWebServer.port
    val localServerUrl: StateFlow<String> = localWebServer.serverUrl

    private val _useLocalhostInPreview = MutableStateFlow(false)
    val useLocalhostInPreview: StateFlow<Boolean> = _useLocalhostInPreview.asStateFlow()

    private val _showLocalServerDialog = MutableStateFlow(false)
    val showLocalServerDialog: StateFlow<Boolean> = _showLocalServerDialog.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao(), db.snapshotDao())

        localWebServer.setContentProvider {
            ServedWebContent(
                html = getExportHtml(),
                css = _cssTextFieldValue.value.text,
                js = _jsTextFieldValue.value.text
            )
        }
        localWebServer.startServer(8080)
    }

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isLoadingProject = MutableStateFlow(false)
    val isLoadingProject: StateFlow<Boolean> = _isLoadingProject.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    private val _loadingStatusText = MutableStateFlow("Initializing WebForge...")
    val loadingStatusText: StateFlow<String> = _loadingStatusText.asStateFlow()

    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    private val _activeTab = MutableStateFlow(EditorTab.CODE)
    val activeTab: StateFlow<EditorTab> = _activeTab.asStateFlow()

    private val _activeFileTab = MutableStateFlow(FileSubTab.HTML)
    val activeFileTab: StateFlow<FileSubTab> = _activeFileTab.asStateFlow()

    private val _textFieldValue = MutableStateFlow(TextFieldValue(""))
    val textFieldValue: StateFlow<TextFieldValue> = _textFieldValue.asStateFlow()

    private val _cssTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val cssTextFieldValue: StateFlow<TextFieldValue> = _cssTextFieldValue.asStateFlow()

    private val _jsTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val jsTextFieldValue: StateFlow<TextFieldValue> = _jsTextFieldValue.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private val _viewportMode = MutableStateFlow(ViewportMode.RESPONSIVE)
    val viewportMode: StateFlow<ViewportMode> = _viewportMode.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<ConsoleLogItem>>(emptyList())
    val consoleLogs: StateFlow<List<ConsoleLogItem>> = _consoleLogs.asStateFlow()

    private val _linterIssues = MutableStateFlow<List<LintIssue>>(emptyList())
    val linterIssues: StateFlow<List<LintIssue>> = _linterIssues.asStateFlow()

    private val _domTree = MutableStateFlow<List<DomNode>>(emptyList())
    val domTree: StateFlow<List<DomNode>> = _domTree.asStateFlow()

    private val _settings = MutableStateFlow(EditorSettings())
    val settings: StateFlow<EditorSettings> = _settings.asStateFlow()

    private val _snapshots = MutableStateFlow<List<ProjectSnapshot>>(emptyList())
    val snapshots: StateFlow<List<ProjectSnapshot>> = _snapshots.asStateFlow()

    // Search and Replace State
    private val _isSearchVisible = MutableStateFlow(false)
    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _replaceQuery = MutableStateFlow("")
    val replaceQuery: StateFlow<String> = _replaceQuery.asStateFlow()

    private val _searchMatchIndices = MutableStateFlow<List<Int>>(emptyList())
    val searchMatchIndices: StateFlow<List<Int>> = _searchMatchIndices.asStateFlow()

    private val _currentMatchIndex = MutableStateFlow(0)
    val currentMatchIndex: StateFlow<Int> = _currentMatchIndex.asStateFlow()

    // Dialogs
    private val _showTemplatesDialog = MutableStateFlow(false)
    val showTemplatesDialog: StateFlow<Boolean> = _showTemplatesDialog.asStateFlow()

    private val _showCdnDialog = MutableStateFlow(false)
    val showCdnDialog: StateFlow<Boolean> = _showCdnDialog.asStateFlow()

    private val _showColorPicker = MutableStateFlow(false)
    val showColorPicker: StateFlow<Boolean> = _showColorPicker.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showProjectsSheet = MutableStateFlow(false)
    val showProjectsSheet: StateFlow<Boolean> = _showProjectsSheet.asStateFlow()

    private val _showSnapshotsSheet = MutableStateFlow(false)
    val showSnapshotsSheet: StateFlow<Boolean> = _showSnapshotsSheet.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _previewRefreshTrigger = MutableStateFlow(0L)
    val previewRefreshTrigger: StateFlow<Long> = _previewRefreshTrigger.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            repository.allProjects.collectLatest { list ->
                _projects.value = list
                if (list.isEmpty()) {
                    createDefaultProject()
                } else if (_currentProject.value == null) {
                    selectProject(list.first())
                }
            }
        }
    }

    private suspend fun createDefaultProject() {
        val defaultTemplate = CodeSnippets.STARTER_TEMPLATES.first()
        val defaultProj = Project(
            title = "My Landing Page",
            htmlContent = defaultTemplate.htmlContent
        )
        val id = repository.insertProject(defaultProj)
        val saved = defaultProj.copy(id = id)
        _currentProject.value = saved
        _textFieldValue.value = TextFieldValue(saved.htmlContent)
        runAnalysis(saved.htmlContent)
    }

    fun selectProject(project: Project) {
        _currentProject.value = project
        _textFieldValue.value = TextFieldValue(project.htmlContent)
        _cssTextFieldValue.value = TextFieldValue(project.cssContent)
        _jsTextFieldValue.value = TextFieldValue(project.jsContent)
        undoStack.clear()
        redoStack.clear()
        updateUndoRedoState()
        runAnalysis(project.htmlContent)
        loadSnapshots(project.id)
        refreshPreview()
    }

    private fun loadSnapshots(projectId: Long) {
        viewModelScope.launch {
            repository.getSnapshotsForProject(projectId).collectLatest {
                _snapshots.value = it
            }
        }
    }

    fun setActiveTab(tab: EditorTab) {
        _activeTab.value = tab
        if (tab == EditorTab.DOM || tab == EditorTab.LINTER) {
            runAnalysis(getActiveText())
        }
        if (tab == EditorTab.PREVIEW) {
            refreshPreview()
        }
    }

    fun setActiveFileTab(tab: FileSubTab) {
        _activeFileTab.value = tab
    }

    fun setViewportMode(mode: ViewportMode) {
        _viewportMode.value = mode
    }

    fun getActiveText(): String {
        val proj = _currentProject.value ?: return _textFieldValue.value.text
        return if (proj.isMultiFile) {
            when (_activeFileTab.value) {
                FileSubTab.HTML -> _textFieldValue.value.text
                FileSubTab.CSS -> _cssTextFieldValue.value.text
                FileSubTab.JS -> _jsTextFieldValue.value.text
            }
        } else {
            _textFieldValue.value.text
        }
    }

    fun getRunnableHtml(): String {
        val proj = _currentProject.value
        val html = _textFieldValue.value.text
        val css = _cssTextFieldValue.value.text
        val js = _jsTextFieldValue.value.text

        if (proj != null && proj.isMultiFile) {
            val cssTag = if (css.isNotBlank()) "<style>\n$css\n</style>" else ""
            val jsTag = if (js.isNotBlank()) "<script>\n$js\n</script>" else ""
            var combined = html
            if (cssTag.isNotEmpty()) {
                combined = if (combined.contains("</head>", ignoreCase = true)) {
                    combined.replace("</head>", "$cssTag\n</head>", ignoreCase = true)
                } else {
                    "$cssTag\n$combined"
                }
            }
            if (jsTag.isNotEmpty()) {
                combined = if (combined.contains("</body>", ignoreCase = true)) {
                    combined.replace("</body>", "$jsTag\n</body>", ignoreCase = true)
                } else {
                    "$combined\n$jsTag"
                }
            }
            return combined
        }
        return html
    }

    // 1. Text change handling with undo/redo
    fun onHtmlChange(newValue: TextFieldValue) {
        val oldText = _textFieldValue.value.text
        if (oldText != newValue.text) {
            undoStack.add(oldText)
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
            updateUndoRedoState()
            scheduleAutoSave()
        }
        _textFieldValue.value = newValue
        if (_isSearchVisible.value) {
            updateSearchMatches(_searchQuery.value)
        }
    }

    fun onCssChange(newValue: TextFieldValue) {
        _cssTextFieldValue.value = newValue
        scheduleAutoSave()
    }

    fun onJsChange(newValue: TextFieldValue) {
        _jsTextFieldValue.value = newValue
        scheduleAutoSave()
    }

    private fun updateUndoRedoState() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val current = _textFieldValue.value.text
            val prev = undoStack.removeLast()
            redoStack.add(current)
            _textFieldValue.value = TextFieldValue(prev, TextRange(prev.length))
            updateUndoRedoState()
            scheduleAutoSave()
            runAnalysis(prev)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val current = _textFieldValue.value.text
            val next = redoStack.removeLast()
            undoStack.add(current)
            _textFieldValue.value = TextFieldValue(next, TextRange(next.length))
            updateUndoRedoState()
            scheduleAutoSave()
            runAnalysis(next)
        }
    }

    private fun scheduleAutoSave() {
        val proj = _currentProject.value ?: return
        val updated = proj.copy(
            htmlContent = _textFieldValue.value.text,
            cssContent = _cssTextFieldValue.value.text,
            jsContent = _jsTextFieldValue.value.text,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        viewModelScope.launch {
            repository.updateProject(updated)
        }
    }

    fun saveCurrentProjectExplicitly() {
        scheduleAutoSave()
        showToast("Project saved")
    }

    // 2. Format HTML
    fun formatCurrentCode() {
        val currentText = _textFieldValue.value.text
        val formatted = HtmlFormatter.formatHtml(currentText, _settings.value.tabSpaces)
        if (formatted != currentText) {
            undoStack.add(currentText)
            redoStack.clear()
            updateUndoRedoState()
            _textFieldValue.value = TextFieldValue(formatted, TextRange(formatted.length))
            scheduleAutoSave()
            runAnalysis(formatted)
            showToast("Code beautified")
        }
    }

    // 3. Quick Snippets insertion
    fun insertSnippet(snippet: QuickSnippet) {
        val current = _textFieldValue.value
        val text = current.text
        val selStart = current.selection.min
        val selEnd = current.selection.max

        val before = text.substring(0, selStart)
        val after = text.substring(selEnd)
        val inserted = snippet.textToInsert

        val newText = before + inserted + after
        val newCursor = (selStart + inserted.length - snippet.cursorOffsetFromEnd).coerceIn(0, newText.length)

        undoStack.add(text)
        redoStack.clear()
        updateUndoRedoState()

        _textFieldValue.value = TextFieldValue(newText, TextRange(newCursor))
        scheduleAutoSave()
    }

    // 4. Color insertion
    fun insertColorCode(hexColor: String) {
        val current = _textFieldValue.value
        val text = current.text
        val selStart = current.selection.min
        val selEnd = current.selection.max

        val before = text.substring(0, selStart)
        val after = text.substring(selEnd)

        val newText = before + hexColor + after
        val newCursor = selStart + hexColor.length

        undoStack.add(text)
        redoStack.clear()
        updateUndoRedoState()

        _textFieldValue.value = TextFieldValue(newText, TextRange(newCursor))
        scheduleAutoSave()
        _showColorPicker.value = false
        showToast("Inserted color $hexColor")
    }

    // 5. CDN Injection
    fun injectCdnLibrary(cdn: CdnLibrary) {
        val current = _textFieldValue.value.text
        val tag = cdn.tagToInject
        val updated = if (current.contains("</head>", ignoreCase = true)) {
            current.replace("</head>", "  $tag\n</head>", ignoreCase = true)
        } else {
            "$tag\n$current"
        }
        undoStack.add(current)
        redoStack.clear()
        updateUndoRedoState()
        _textFieldValue.value = TextFieldValue(updated, TextRange(updated.length))
        scheduleAutoSave()
        _showCdnDialog.value = false
        showToast("Injected ${cdn.name}")
        refreshPreview()
    }

    // 6. Template Loading
    fun loadTemplate(template: StarterTemplate) {
        val current = _textFieldValue.value.text
        undoStack.add(current)
        redoStack.clear()
        updateUndoRedoState()

        _textFieldValue.value = TextFieldValue(template.htmlContent, TextRange(0))
        val proj = _currentProject.value
        if (proj != null) {
            val updated = proj.copy(
                title = template.title,
                htmlContent = template.htmlContent,
                updatedAt = System.currentTimeMillis()
            )
            _currentProject.value = updated
            viewModelScope.launch {
                repository.updateProject(updated)
            }
        }
        runAnalysis(template.htmlContent)
        _showTemplatesDialog.value = false
        refreshPreview()
        showToast("Loaded '${template.title}'")
    }

    // 7. Analysis (Linter & DOM)
    private fun runAnalysis(html: String) {
        _linterIssues.value = HtmlLinter.lint(html)
        _domTree.value = DomParser.parse(html)
    }

    fun jumpToLine(lineNumber: Int) {
        val lines = _textFieldValue.value.text.lines()
        var charIndex = 0
        for (i in 0 until (lineNumber - 1).coerceAtMost(lines.size - 1)) {
            charIndex += lines[i].length + 1
        }
        val targetLineLength = lines.getOrNull(lineNumber - 1)?.length ?: 0
        _textFieldValue.value = _textFieldValue.value.copy(
            selection = TextRange(charIndex, (charIndex + targetLineLength).coerceAtMost(_textFieldValue.value.text.length))
        )
        _activeTab.value = EditorTab.CODE
    }

    // 8. JS Console
    fun addConsoleLog(level: String, message: String, source: String = "", lineNumber: Int = 0) {
        val item = ConsoleLogItem(
            level = level,
            message = message,
            source = source,
            lineNumber = lineNumber
        )
        _consoleLogs.value = _consoleLogs.value + item
    }

    fun clearConsole() {
        _consoleLogs.value = emptyList()
        showToast("Console cleared")
    }

    // 9. Search and Replace
    fun toggleSearch() {
        _isSearchVisible.value = !_isSearchVisible.value
        if (_isSearchVisible.value) {
            updateSearchMatches(_searchQuery.value)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateSearchMatches(query)
    }

    fun setReplaceQuery(query: String) {
        _replaceQuery.value = query
    }

    private fun updateSearchMatches(query: String) {
        if (query.isEmpty()) {
            _searchMatchIndices.value = emptyList()
            _currentMatchIndex.value = 0
            return
        }
        val text = _textFieldValue.value.text
        val matches = mutableListOf<Int>()
        var index = text.indexOf(query, 0, ignoreCase = true)
        while (index >= 0) {
            matches.add(index)
            index = text.indexOf(query, index + query.length.coerceAtLeast(1), ignoreCase = true)
        }
        _searchMatchIndices.value = matches
        if (matches.isNotEmpty()) {
            _currentMatchIndex.value = _currentMatchIndex.value.coerceIn(0, matches.size - 1)
            highlightMatch(matches[_currentMatchIndex.value], query.length)
        } else {
            _currentMatchIndex.value = 0
        }
    }

    fun nextMatch() {
        val matches = _searchMatchIndices.value
        if (matches.isEmpty()) return
        val nextIdx = (_currentMatchIndex.value + 1) % matches.size
        _currentMatchIndex.value = nextIdx
        highlightMatch(matches[nextIdx], _searchQuery.value.length)
    }

    fun previousMatch() {
        val matches = _searchMatchIndices.value
        if (matches.isEmpty()) return
        val prevIdx = if (_currentMatchIndex.value - 1 < 0) matches.size - 1 else _currentMatchIndex.value - 1
        _currentMatchIndex.value = prevIdx
        highlightMatch(matches[prevIdx], _searchQuery.value.length)
    }

    private fun highlightMatch(index: Int, length: Int) {
        _textFieldValue.value = _textFieldValue.value.copy(
            selection = TextRange(index, (index + length).coerceAtMost(_textFieldValue.value.text.length))
        )
    }

    fun replaceCurrentMatch() {
        val matches = _searchMatchIndices.value
        val query = _searchQuery.value
        val replace = _replaceQuery.value
        if (matches.isEmpty() || query.isEmpty()) return

        val matchPos = matches[_currentMatchIndex.value]
        val text = _textFieldValue.value.text
        val newText = text.substring(0, matchPos) + replace + text.substring(matchPos + query.length)

        undoStack.add(text)
        redoStack.clear()
        updateUndoRedoState()

        _textFieldValue.value = TextFieldValue(newText, TextRange(matchPos + replace.length))
        scheduleAutoSave()
        updateSearchMatches(query)
    }

    fun replaceAllMatches() {
        val query = _searchQuery.value
        val replace = _replaceQuery.value
        if (query.isEmpty()) return

        val text = _textFieldValue.value.text
        val newText = text.replace(query, replace, ignoreCase = true)
        if (newText != text) {
            undoStack.add(text)
            redoStack.clear()
            updateUndoRedoState()
            _textFieldValue.value = TextFieldValue(newText, TextRange(0))
            scheduleAutoSave()
            updateSearchMatches(query)
            showToast("Replaced all occurrences")
        }
    }

    // 10. Snapshots / History
    fun createSnapshot(label: String) {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            val snap = ProjectSnapshot(
                projectId = proj.id,
                label = label.ifBlank { "Checkpoint ${System.currentTimeMillis() % 10000}" },
                htmlContent = _textFieldValue.value.text,
                cssContent = _cssTextFieldValue.value.text,
                jsContent = _jsTextFieldValue.value.text
            )
            repository.createSnapshot(snap)
            showToast("Snapshot saved: '${snap.label}'")
        }
    }

    fun restoreSnapshot(snap: ProjectSnapshot) {
        undoStack.add(_textFieldValue.value.text)
        redoStack.clear()
        updateUndoRedoState()

        _textFieldValue.value = TextFieldValue(snap.htmlContent, TextRange(0))
        _cssTextFieldValue.value = TextFieldValue(snap.cssContent, TextRange(0))
        _jsTextFieldValue.value = TextFieldValue(snap.jsContent, TextRange(0))
        scheduleAutoSave()
        runAnalysis(snap.htmlContent)
        _showSnapshotsSheet.value = false
        refreshPreview()
        showToast("Restored '${snap.label}'")
    }

    fun deleteSnapshot(snap: ProjectSnapshot) {
        viewModelScope.launch {
            repository.deleteSnapshot(snap)
            showToast("Snapshot deleted")
        }
    }

    // 11. Project Management
    fun createNewProject(title: String, useTemplate: Boolean = false) {
        viewModelScope.launch {
            val content = if (useTemplate) CodeSnippets.STARTER_TEMPLATES.first().htmlContent else "<!DOCTYPE html>\n<html>\n<head>\n  <title>$title</title>\n</head>\n<body>\n  <h1>$title</h1>\n</body>\n</html>"
            val newProj = Project(
                title = title.ifBlank { "Untitled Project" },
                htmlContent = content
            )
            val id = repository.insertProject(newProj)
            val saved = newProj.copy(id = id)
            selectProject(saved)
            _showProjectsSheet.value = false
            showToast("Project created")
        }
    }

    fun duplicateCurrentProject() {
        val current = _currentProject.value ?: return
        viewModelScope.launch {
            val dup = current.copy(
                id = 0,
                title = "${current.title} (Copy)",
                updatedAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
            val newId = repository.insertProject(dup)
            selectProject(dup.copy(id = newId))
            _showProjectsSheet.value = false
            showToast("Project duplicated")
        }
    }

    fun renameCurrentProject(newTitle: String) {
        val current = _currentProject.value ?: return
        val updated = current.copy(title = newTitle.ifBlank { current.title })
        _currentProject.value = updated
        viewModelScope.launch {
            repository.updateProject(updated)
            showToast("Project renamed")
        }
    }

    fun deleteProject(proj: Project) {
        viewModelScope.launch {
            repository.deleteProject(proj)
            if (_currentProject.value?.id == proj.id) {
                val remaining = _projects.value.filter { it.id != proj.id }
                if (remaining.isNotEmpty()) {
                    selectProject(remaining.first())
                } else {
                    createDefaultProject()
                }
            }
            showToast("Project deleted")
        }
    }

    fun toggleMultiFileMode() {
        val current = _currentProject.value ?: return
        val toggled = !current.isMultiFile
        val updated = current.copy(isMultiFile = toggled)
        _currentProject.value = updated
        viewModelScope.launch {
            repository.updateProject(updated)
            showToast(if (toggled) "Multi-file enabled (HTML, CSS, JS)" else "Single HTML mode enabled")
        }
    }

    // 12. Settings
    fun updateSettings(newSettings: EditorSettings) {
        _settings.value = newSettings
        _showSettingsDialog.value = false
        showToast("Settings updated")
    }

    // 13. Sharing & Export with Made with Azuki
    fun getExportHtml(): String {
        val baseHtml = getRunnableHtml()
        val azukiComment = "<!--\n  ======================================================\n  Made with Azuki\n  WebForge Studio • Lightweight Mobile HTML IDE\n  ======================================================\n-->\n"
        return if (!baseHtml.contains("Made with Azuki")) {
            "$azukiComment$baseHtml"
        } else {
            baseHtml
        }
    }

    fun shareHtmlCode(context: Context) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getExportHtml())
            putExtra(Intent.EXTRA_SUBJECT, "${_currentProject.value?.title ?: "WebForge"}.html")
            type = "text/html"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share HTML Code • Made with Azuki")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun exportHtmlToUri(context: Context, uri: Uri) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(getExportHtml())
                    writer.flush()
                }
            }
            showToast("Project exported successfully to device!")
        } catch (e: Exception) {
            showToast("Failed to export: ${e.message}")
        }
    }

    fun importHtmlFile(fileName: String, content: String) {
        viewModelScope.launch {
            val title = fileName.removeSuffix(".html").removeSuffix(".htm").ifBlank { "Imported Project" }
            val newProj = Project(
                title = title,
                htmlContent = content
            )
            val newId = repository.insertProject(newProj)
            val saved = newProj.copy(id = newId)
            loadAndOpenProject(saved)
            showToast("Imported '$title'")
        }
    }

    fun createProjectFromGuide(guide: WebGuideItem) {
        viewModelScope.launch {
            val newProj = Project(
                title = guide.title,
                htmlContent = if (guide.category == "CSS") {
                    "<!DOCTYPE html>\n<html>\n<head>\n  <style>\n${guide.codeSnippet}\n  </style>\n</head>\n<body>\n  <div class=\"flex-container\">\n    <div class=\"flex-item\">Card 1</div>\n    <div class=\"flex-item\">Card 2</div>\n  </div>\n</body>\n</html>"
                } else if (guide.category == "JavaScript") {
                    "<!DOCTYPE html>\n<html>\n<head>\n  <title>${guide.title}</title>\n</head>\n<body>\n  <input id=\"usernameInput\" placeholder=\"Type here...\">\n  <button id=\"submitBtn\">Add</button>\n  <ul id=\"outputList\"></ul>\n  <script>\n${guide.codeSnippet}\n  </script>\n</body>\n</html>"
                } else {
                    guide.codeSnippet
                }
            )
            val newId = repository.insertProject(newProj)
            val saved = newProj.copy(id = newId)
            loadAndOpenProject(saved)
            showToast("Created project from guide: ${guide.title}")
        }
    }

    fun finishSplash() {
        _currentScreen.value = AppScreen.HOME
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setHomeSearchQuery(query: String) {
        _homeSearchQuery.value = query
    }

    fun loadAndOpenProject(project: Project) {
        viewModelScope.launch {
            _isLoadingProject.value = true
            _loadingProgress.value = 0.15f
            _loadingStatusText.value = "Opening '${project.title}'..."
            delay(180)

            _loadingProgress.value = 0.45f
            _loadingStatusText.value = "Parsing HTML syntax & DOM tree..."
            delay(200)

            selectProject(project)

            _loadingProgress.value = 0.85f
            _loadingStatusText.value = "Compiling live preview engine..."
            delay(180)

            _loadingProgress.value = 1.0f
            _loadingStatusText.value = "Workspace Ready • Made with Azuki"
            delay(150)

            _isLoadingProject.value = false
            _currentScreen.value = AppScreen.EDITOR
        }
    }

    fun refreshPreview() {
        _previewRefreshTrigger.value = System.currentTimeMillis()
    }

    // Dialog state setters
    fun setTemplatesDialogVisible(visible: Boolean) { _showTemplatesDialog.value = visible }
    fun setCdnDialogVisible(visible: Boolean) { _showCdnDialog.value = visible }
    fun setColorPickerVisible(visible: Boolean) { _showColorPicker.value = visible }
    fun setSettingsDialogVisible(visible: Boolean) { _showSettingsDialog.value = visible }
    fun setProjectsSheetVisible(visible: Boolean) { _showProjectsSheet.value = visible }
    fun setSnapshotsSheetVisible(visible: Boolean) { _showSnapshotsSheet.value = visible }

    fun startLocalServer(port: Int) {
        localWebServer.startServer(port)
        showToast("Localhost started on port $port")
    }

    fun stopLocalServer() {
        localWebServer.stopServer()
        showToast("Localhost stopped")
    }

    fun changeLocalServerPort(port: Int) {
        localWebServer.changePort(port)
        showToast("Localhost port set to $port")
    }

    fun setUseLocalhostInPreview(enabled: Boolean) {
        _useLocalhostInPreview.value = enabled
        showToast(if (enabled) "Preview routed to http://localhost:${localServerPort.value}/" else "Preview routed to direct sandbox")
        refreshPreview()
    }

    fun setLocalServerDialogVisible(visible: Boolean) {
        _showLocalServerDialog.value = visible
    }

    override fun onCleared() {
        super.onCleared()
        localWebServer.stopServer()
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
