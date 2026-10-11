package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.ViewportMode

class WebAppInterface(private val onLog: (String, String) -> Unit) {
    @JavascriptInterface
    fun postLog(level: String, message: String) {
        onLog(level, message)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiveWebViewPreview(
    htmlContent: String,
    viewportMode: ViewportMode,
    onViewportChange: (ViewportMode) -> Unit,
    refreshTrigger: Long,
    onConsoleMessage: (level: String, message: String, source: String, line: Int) -> Unit,
    isLocalServerRunning: Boolean = false,
    localServerPort: Int = 8080,
    localServerUrl: String = "http://localhost:8080/",
    useLocalhost: Boolean = false,
    onOpenLocalServerDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    // Intercept console script injected at document start
    val consoleHookScript = """
      <script>
        (function() {
          const originalLog = console.log;
          const originalWarn = console.warn;
          const originalError = console.error;
          const originalInfo = console.info;

          console.log = function(...args) {
            originalLog.apply(console, args);
            if (window.AndroidBridge) {
              window.AndroidBridge.postLog('LOG', args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
            }
          };
          console.warn = function(...args) {
            originalWarn.apply(console, args);
            if (window.AndroidBridge) {
              window.AndroidBridge.postLog('WARN', args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
            }
          };
          console.error = function(...args) {
            originalError.apply(console, args);
            if (window.AndroidBridge) {
              window.AndroidBridge.postLog('ERROR', args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
            }
          };
          console.info = function(...args) {
            originalInfo.apply(console, args);
            if (window.AndroidBridge) {
              window.AndroidBridge.postLog('INFO', args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
            }
          };

          window.onerror = function(msg, url, lineNo, colNo, error) {
            if (window.AndroidBridge) {
              window.AndroidBridge.postLog('ERROR', msg + ' (Line ' + lineNo + ')');
            }
            return false;
          };
        })();
      </script>
    """.trimIndent()

    val preparedHtml = remember(htmlContent) {
        if (htmlContent.contains("<head>", ignoreCase = true)) {
            htmlContent.replace("<head>", "<head>\n$consoleHookScript\n", ignoreCase = true)
        } else {
            "$consoleHookScript\n$htmlContent"
        }
    }

    LaunchedEffect(preparedHtml, refreshTrigger, useLocalhost, localServerUrl) {
        if (useLocalhost && isLocalServerRunning) {
            webViewInstance?.loadUrl(localServerUrl)
        } else {
            webViewInstance?.loadDataWithBaseURL("https://localhost/", preparedHtml, "text/html", "UTF-8", null)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLowest)) {
        // Viewport Controls & Action Bar
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Viewport modes & Localhost Server pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { onViewportChange(ViewportMode.RESPONSIVE) },
                        label = { Text("Fluid", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(Icons.Outlined.DesktopWindows, contentDescription = "Fluid", modifier = Modifier.height(14.dp))
                        },
                        colors = if (viewportMode == ViewportMode.RESPONSIVE) {
                            AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else AssistChipDefaults.assistChipColors(),
                        modifier = Modifier.testTag("preview_viewport_responsive")
                    )

                    AssistChip(
                        onClick = { onViewportChange(ViewportMode.MOBILE) },
                        label = { Text("Mobile", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(Icons.Filled.Smartphone, contentDescription = "Mobile", modifier = Modifier.height(14.dp))
                        },
                        colors = if (viewportMode == ViewportMode.MOBILE) {
                            AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else AssistChipDefaults.assistChipColors(),
                        modifier = Modifier.testTag("preview_viewport_mobile")
                    )

                    // Localhost Server Status Chip with Port
                    AssistChip(
                        onClick = onOpenLocalServerDialog,
                        label = {
                            Text(
                                text = ":$localServerPort",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isLocalServerRunning) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isLocalServerRunning) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.testTag("preview_localhost_server_chip")
                    )
                }

                // Action buttons: Zoom, Refresh, External Browser
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f)
                            webViewInstance?.setInitialScale((zoomLevel * 100).toInt())
                        },
                        modifier = Modifier.size(32.dp).testTag("preview_zoom_out")
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = {
                            zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.0f)
                            webViewInstance?.setInitialScale((zoomLevel * 100).toInt())
                        },
                        modifier = Modifier.size(32.dp).testTag("preview_zoom_in")
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = {
                            if (useLocalhost && isLocalServerRunning) {
                                webViewInstance?.loadUrl(localServerUrl)
                            } else {
                                webViewInstance?.loadDataWithBaseURL("https://localhost/", preparedHtml, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.size(32.dp).testTag("preview_refresh_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload Preview", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = {
                            val targetUri = if (isLocalServerRunning) Uri.parse(localServerUrl) else Uri.parse("http://localhost:$localServerPort/")
                            val intent = Intent(Intent.ACTION_VIEW, targetUri)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp).testTag("preview_open_external_browser")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome/Browser", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Preview Container (simulates phone / tablet frame or full screen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(if (viewportMode != ViewportMode.RESPONSIVE) 16.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            val frameModifier = when (viewportMode) {
                ViewportMode.RESPONSIVE -> Modifier.fillMaxSize()
                ViewportMode.MOBILE -> Modifier
                    .width(375.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                ViewportMode.TABLET -> Modifier
                    .width(768.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            }

            Box(
                modifier = frameModifier
                    .background(MaterialTheme.colorScheme.surface)
                    .animateContentSize()
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                databaseEnabled = true
                                setSupportZoom(true)
                                builtInZoomControls = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }

                            addJavascriptInterface(
                                WebAppInterface { level, msg ->
                                    onConsoleMessage(level, msg, "preview", 0)
                                },
                                "AndroidBridge"
                            )

                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                                    val level = when (consoleMessage.messageLevel()) {
                                        ConsoleMessage.MessageLevel.ERROR -> "ERROR"
                                        ConsoleMessage.MessageLevel.WARNING -> "WARN"
                                        ConsoleMessage.MessageLevel.TIP -> "INFO"
                                        else -> "LOG"
                                    }
                                    onConsoleMessage(
                                        level,
                                        consoleMessage.message(),
                                        consoleMessage.sourceId() ?: "",
                                        consoleMessage.lineNumber()
                                    )
                                    return true
                                }
                            }

                            webViewClient = object : WebViewClient() {}

                            if (useLocalhost && isLocalServerRunning) {
                                loadUrl(localServerUrl)
                            } else {
                                loadDataWithBaseURL("https://localhost/", preparedHtml, "text/html", "UTF-8", null)
                            }
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    },
                    modifier = Modifier.fillMaxSize().testTag("live_webview_preview")
                )
            }
        }
    }
}
