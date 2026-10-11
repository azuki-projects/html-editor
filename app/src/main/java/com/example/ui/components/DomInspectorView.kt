package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.DomNode

@Composable
fun DomInspectorView(
    nodes: List<DomNode>,
    onJumpToLine: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (nodes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No DOM elements parsed yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add HTML tags in the editor to inspect node tree",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(nodes, key = { it.id }) { rootNode ->
                DomTreeNodeItem(
                    node = rootNode,
                    onJumpToLine = onJumpToLine
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DomTreeNodeItem(
    node: DomNode,
    onJumpToLine: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(node.depth < 2) }
    val hasChildren = node.children.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (node.depth * 14).dp.coerceAtMost(80.dp))
                .clickable { if (hasChildren) isExpanded = !isExpanded }
                .testTag("dom_node_${node.tagName}_${node.line}")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand / Collapse Chevron
                if (hasChildren) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                    }
                }

                Spacer(Modifier.width(6.dp))

                // Tag Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = "<${node.tagName}>",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Attributes summary / Text content
                Column(modifier = Modifier.weight(1f)) {
                    if (node.attributes.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            node.attributes.forEach { (key, value) ->
                                Text(
                                    text = "$key=\"$value\"",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (key == "id") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    if (node.textContent.isNotBlank()) {
                        Text(
                            text = "\"${node.textContent}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                }

                // Jump to line in editor
                IconButton(
                    onClick = { onJumpToLine(node.line) },
                    modifier = Modifier.size(32.dp).testTag("dom_jump_line_${node.line}")
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = "Jump to line ${node.line}",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // Children recursive render
        AnimatedVisibility(visible = isExpanded && hasChildren) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                node.children.forEach { child ->
                    DomTreeNodeItem(
                        node = child,
                        onJumpToLine = onJumpToLine
                    )
                }
            }
        }
    }
}
