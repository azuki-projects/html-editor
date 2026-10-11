package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val htmlContent: String,
    val cssContent: String = "",
    val jsContent: String = "",
    val isMultiFile: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Bundles HTML, CSS, and JS into a single runnable HTML document if multi-file is used,
     * or returns the HTML content directly.
     */
    fun getCombinedHtml(): String {
        if (!isMultiFile) return htmlContent
        val cssBlock = if (cssContent.isNotBlank()) "<style>\n$cssContent\n</style>" else ""
        val jsBlock = if (jsContent.isNotBlank()) "<script>\n$jsContent\n</script>" else ""

        var combined = htmlContent
        if (cssBlock.isNotEmpty()) {
            combined = if (combined.contains("</head>", ignoreCase = true)) {
                combined.replace("</head>", "$cssBlock\n</head>", ignoreCase = true)
            } else {
                "$cssBlock\n$combined"
            }
        }
        if (jsBlock.isNotEmpty()) {
            combined = if (combined.contains("</body>", ignoreCase = true)) {
                combined.replace("</body>", "$jsBlock\n</body>", ignoreCase = true)
            } else {
                "$combined\n$jsBlock"
            }
        }
        return combined
    }
}
