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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.LintIssue
import com.example.editor.LintSeverity

@Composable
fun LinterView(
    issues: List<LintIssue>,
    onJumpToLine: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val errorCount = issues.count { it.severity == LintSeverity.ERROR }
    val warningCount = issues.count { it.severity == LintSeverity.WARNING }
    val infoCount = issues.count { it.severity == LintSeverity.INFO }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Summary Card
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status pill
                if (issues.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "No HTML syntax issues found",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF10B981)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SeverityCountChip("Errors", errorCount, Color(0xFFEF4444))
                        SeverityCountChip("Warnings", warningCount, Color(0xFFF59E0B))
                        SeverityCountChip("Info", infoCount, Color(0xFF3B82F6))
                    }
                }
            }
        }

        // Issues List
        if (issues.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Clean Codebase!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "All tags, quotes, accessibility alts and structure look solid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(issues) { issue ->
                    LintIssueCard(issue = issue, onJumpToLine = onJumpToLine)
                }
            }
        }
    }
}

@Composable
private fun SeverityCountChip(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "$label: $count",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun LintIssueCard(
    issue: LintIssue,
    onJumpToLine: (Int) -> Unit
) {
    val (icon, color) = when (issue.severity) {
        LintSeverity.ERROR -> Icons.Default.Error to Color(0xFFEF4444)
        LintSeverity.WARNING -> Icons.Default.Warning to Color(0xFFF59E0B)
        LintSeverity.INFO -> Icons.Default.Info to Color(0xFF3B82F6)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth().testTag("linter_issue_line_${issue.lineNumber}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp).padding(top = 2.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                    ) {
                        Text(
                            text = "Line ${issue.lineNumber}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "[${issue.rule}]",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = issue.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                if (issue.snippet.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = issue.snippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { onJumpToLine(issue.lineNumber) },
                modifier = Modifier.size(36.dp).testTag("linter_jump_btn_${issue.lineNumber}")
            ) {
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = "Jump to line ${issue.lineNumber}",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
