package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.WebGuideItem
import com.example.editor.WebGuides

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    onBack: () -> Unit,
    onCreateProjectFromGuide: (WebGuideItem) -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = remember {
        listOf("ALL") + WebGuides.GUIDES.map { it.category }.distinct()
    }

    val filteredGuides = remember(selectedCategory) {
        if (selectedCategory == "ALL") WebGuides.GUIDES
        else WebGuides.GUIDES.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Web Development Guides",
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
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("guides_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
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
            // Category Filter Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("guide_filter_$cat")
                        )
                    }
                }
            }

            // Guides List
            items(filteredGuides, key = { it.id }) { guide ->
                GuideCardItem(
                    guide = guide,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Guide Code", guide.codeSnippet))
                        Toast.makeText(context, "Snippet copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    onCreateProject = { onCreateProjectFromGuide(guide) }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GuideCardItem(
    guide: WebGuideItem,
    onCopy: () -> Unit,
    onCreateProject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().testTag("guide_card_${guide.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Badge & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = guide.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = guide.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            // Code Snippet Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = guide.codeSnippet,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Pro Tips
            if (guide.tips.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    guide.tips.forEach { tip ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Actions: Copy & Create Project
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("guide_copy_${guide.id}")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copy Code", fontSize = 12.sp)
                }

                Button(
                    onClick = onCreateProject,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).testTag("guide_create_proj_${guide.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Create Project", fontSize = 12.sp)
                }
            }
        }
    }
}
