package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FindReplaceBar(
    isVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    replaceQuery: String,
    onReplaceQueryChange: (String) -> Unit,
    matchCount: Int,
    currentMatchIndex: Int,
    onNextMatch: () -> Unit,
    onPreviousMatch: () -> Unit,
    onReplaceCurrent: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = isVisible, modifier = modifier) {
        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Search row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Find in code...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("search_input_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    // Match count indicator
                    val countText = if (matchCount > 0) {
                        "${currentMatchIndex + 1}/$matchCount"
                    } else if (searchQuery.isNotEmpty()) {
                        "0/0"
                    } else ""

                    if (countText.isNotEmpty()) {
                        Text(
                            text = countText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onPreviousMatch,
                        enabled = matchCount > 0,
                        modifier = Modifier.size(32.dp).testTag("search_prev_btn")
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous Match")
                    }

                    IconButton(
                        onClick = onNextMatch,
                        enabled = matchCount > 0,
                        modifier = Modifier.size(32.dp).testTag("search_next_btn")
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next Match")
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp).testTag("search_close_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Search")
                    }
                }

                // Replace row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replaceQuery,
                        onValueChange = onReplaceQueryChange,
                        placeholder = { Text("Replace with...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("replace_input_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onReplaceCurrent,
                        enabled = matchCount > 0,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("replace_single_btn")
                    ) {
                        Text("Replace", fontSize = 11.sp)
                    }

                    Spacer(Modifier.width(4.dp))

                    Button(
                        onClick = onReplaceAll,
                        enabled = matchCount > 0,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("replace_all_btn")
                    ) {
                        Text("All", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
