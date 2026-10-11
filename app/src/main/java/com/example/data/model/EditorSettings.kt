package com.example.data.model

enum class EditorThemeMode(val displayName: String) {
    VS_CODE_DARK("VS Code Dark"),
    MONOKAI("Monokai Pro"),
    DRACULA("Dracula Night"),
    CYBERPUNK("Cyberpunk Neon"),
    GITHUB_LIGHT("GitHub Light")
}

data class EditorSettings(
    val theme: EditorThemeMode = EditorThemeMode.VS_CODE_DARK,
    val fontSizeSp: Int = 14,
    val showLineNumbers: Boolean = true,
    val wordWrap: Boolean = false,
    val tabSpaces: Int = 2,
    val autoCloseTags: Boolean = true
)
