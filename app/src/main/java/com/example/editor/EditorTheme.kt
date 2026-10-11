package com.example.editor

import androidx.compose.ui.graphics.Color
import com.example.data.model.EditorThemeMode

data class EditorColorScheme(
    val background: Color,
    val surface: Color,
    val gutterBackground: Color,
    val lineNumberColor: Color,
    val activeLineColor: Color,
    val textColor: Color,
    val tagColor: Color,
    val attributeNameColor: Color,
    val attributeValueColor: Color,
    val commentColor: Color,
    val doctypeColor: Color,
    val entityColor: Color,
    val keywordColor: Color,
    val selectionColor: Color
)

object EditorThemes {
    val VsCodeDark = EditorColorScheme(
        background = Color(0xFF1E1E1E),
        surface = Color(0xFF252526),
        gutterBackground = Color(0xFF1E1E1E),
        lineNumberColor = Color(0xFF858585),
        activeLineColor = Color(0xFF282828),
        textColor = Color(0xFFD4D4D4),
        tagColor = Color(0xFF569CD6),
        attributeNameColor = Color(0xFF9CDCFE),
        attributeValueColor = Color(0xFFCE9178),
        commentColor = Color(0xFF6A9955),
        doctypeColor = Color(0xFFC586C0),
        entityColor = Color(0xFFDCDCAA),
        keywordColor = Color(0xFFC586C0),
        selectionColor = Color(0xFF264F78)
    )

    val Monokai = EditorColorScheme(
        background = Color(0xFF272822),
        surface = Color(0xFF383830),
        gutterBackground = Color(0xFF272822),
        lineNumberColor = Color(0xFF90908A),
        activeLineColor = Color(0xFF3E3D32),
        textColor = Color(0xFFF8F8F2),
        tagColor = Color(0xFFF92672),
        attributeNameColor = Color(0xFFA6E22E),
        attributeValueColor = Color(0xFFE6DB74),
        commentColor = Color(0xFF75715E),
        doctypeColor = Color(0xFF66D9EF),
        entityColor = Color(0xFFAE81FF),
        keywordColor = Color(0xFFF92672),
        selectionColor = Color(0xFF49483E)
    )

    val Dracula = EditorColorScheme(
        background = Color(0xFF282A36),
        surface = Color(0xFF343746),
        gutterBackground = Color(0xFF282A36),
        lineNumberColor = Color(0xFF6272A4),
        activeLineColor = Color(0xFF44475A),
        textColor = Color(0xFFF8F8F2),
        tagColor = Color(0xFFFF79C6),
        attributeNameColor = Color(0xFF50FA7B),
        attributeValueColor = Color(0xFFF1FA8C),
        commentColor = Color(0xFF6272A4),
        doctypeColor = Color(0xFFBD93F9),
        entityColor = Color(0xFF8BE9FD),
        keywordColor = Color(0xFFFF79C6),
        selectionColor = Color(0xFF44475A)
    )

    val Cyberpunk = EditorColorScheme(
        background = Color(0xFF0F051D),
        surface = Color(0xFF1E0E38),
        gutterBackground = Color(0xFF0F051D),
        lineNumberColor = Color(0xFF7B52AB),
        activeLineColor = Color(0xFF2B1254),
        textColor = Color(0xFF00FFCC),
        tagColor = Color(0xFFFF007F),
        attributeNameColor = Color(0xFFFFCC00),
        attributeValueColor = Color(0xFF00E5FF),
        commentColor = Color(0xFF605A75),
        doctypeColor = Color(0xFFFF00AA),
        entityColor = Color(0xFFB026FF),
        keywordColor = Color(0xFFFF007F),
        selectionColor = Color(0xFF520066)
    )

    val GitHubLight = EditorColorScheme(
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF6F8FA),
        gutterBackground = Color(0xFFF6F8FA),
        lineNumberColor = Color(0xFF6E7781),
        activeLineColor = Color(0xFFEAEFF5),
        textColor = Color(0xFF24292F),
        tagColor = Color(0xFF116329),
        attributeNameColor = Color(0xFF0550AE),
        attributeValueColor = Color(0xFF0A3069),
        commentColor = Color(0xFF6E7781),
        doctypeColor = Color(0xFF8250DF),
        entityColor = Color(0xFF953800),
        keywordColor = Color(0xFFCF222E),
        selectionColor = Color(0xFFB6E3FF)
    )

    fun forMode(mode: EditorThemeMode): EditorColorScheme = when (mode) {
        EditorThemeMode.VS_CODE_DARK -> VsCodeDark
        EditorThemeMode.MONOKAI -> Monokai
        EditorThemeMode.DRACULA -> Dracula
        EditorThemeMode.CYBERPUNK -> Cyberpunk
        EditorThemeMode.GITHUB_LIGHT -> GitHubLight
    }
}
