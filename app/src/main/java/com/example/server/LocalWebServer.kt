package com.example.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets

data class ServedWebContent(
    val html: String,
    val css: String = "",
    val js: String = ""
)

class LocalWebServer(
    private val scope: CoroutineScope,
    private val onLog: (level: String, message: String) -> Unit = { _, _ -> }
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _port = MutableStateFlow(8080)
    val port: StateFlow<Int> = _port.asStateFlow()

    private val _serverUrl = MutableStateFlow("http://localhost:8080/")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private var contentProvider: (() -> ServedWebContent)? = null

    fun setContentProvider(provider: () -> ServedWebContent) {
        contentProvider = provider
    }

    fun startServer(targetPort: Int = _port.value) {
        stopServer()

        serverJob = scope.launch(Dispatchers.IO) {
            try {
                _lastError.value = null
                val socket = ServerSocket()
                socket.reuseAddress = true
                socket.bind(InetSocketAddress("127.0.0.1", targetPort))
                serverSocket = socket

                _port.value = targetPort
                _serverUrl.value = "http://localhost:$targetPort/"
                _isRunning.value = true

                onLog("INFO", "🚀 Localhost server started on port $targetPort ($serverUrl)")

                while (isActive && !socket.isClosed) {
                    try {
                        val client = socket.accept()
                        launch(Dispatchers.IO) {
                            handleClient(client)
                        }
                    } catch (e: Exception) {
                        if (!socket.isClosed) {
                            onLog("WARN", "Server accept notice: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _isRunning.value = false
                val errorMsg = "Failed to bind to port $targetPort: ${e.message}"
                _lastError.value = errorMsg
                onLog("ERROR", errorMsg)
            }
        }
    }

    fun stopServer() {
        try {
            serverJob?.cancel()
            serverJob = null
            serverSocket?.close()
            serverSocket = null
            _isRunning.value = false
            onLog("INFO", "🛑 Localhost server stopped.")
        } catch (e: Exception) {
            onLog("WARN", "Error stopping server: ${e.message}")
        }
    }

    fun changePort(newPort: Int) {
        val validatedPort = newPort.coerceIn(1024, 65535)
        _port.value = validatedPort
        _serverUrl.value = "http://localhost:$validatedPort/"
        if (_isRunning.value) {
            startServer(validatedPort)
        }
    }

    private fun handleClient(client: Socket) {
        try {
            client.use { s ->
                val reader = BufferedReader(InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))
                val firstLine = reader.readLine() ?: return
                val parts = firstLine.split(" ")
                val method = parts.getOrNull(0) ?: "GET"
                val path = parts.getOrNull(1) ?: "/"

                val content = contentProvider?.invoke() ?: ServedWebContent(
                    html = "<!DOCTYPE html><html><body><h1>WebForge Localhost</h1><p>Ready</p></body></html>"
                )

                val out = s.getOutputStream()

                when {
                    method.equals("OPTIONS", ignoreCase = true) -> {
                        sendResponse(out, 204, "No Content", "text/plain", byteArrayOf())
                    }
                    path == "/style.css" -> {
                        val bytes = content.css.toByteArray(StandardCharsets.UTF_8)
                        sendResponse(out, 200, "OK", "text/css; charset=UTF-8", bytes)
                    }
                    path == "/script.js" -> {
                        val bytes = content.js.toByteArray(StandardCharsets.UTF_8)
                        sendResponse(out, 200, "OK", "application/javascript; charset=UTF-8", bytes)
                    }
                    path == "/favicon.ico" -> {
                        sendResponse(out, 204, "No Content", "image/x-icon", byteArrayOf())
                    }
                    else -> {
                        // Serve HTML
                        val htmlString = content.html
                        val bytes = htmlString.toByteArray(StandardCharsets.UTF_8)
                        sendResponse(out, 200, "OK", "text/html; charset=UTF-8", bytes)
                    }
                }

                onLog("LOG", "Served $path to ${s.inetAddress.hostAddress} [200 OK]")
            }
        } catch (e: Exception) {
            // Client disconnect or reset
        }
    }

    private fun sendResponse(
        out: OutputStream,
        statusCode: Int,
        statusText: String,
        contentType: String,
        body: ByteArray
    ) {
        val header = "HTTP/1.1 $statusCode $statusText\r\n" +
                "Content-Type: $contentType\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Connection: close\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n" +
                "Access-Control-Allow-Headers: *\r\n" +
                "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                "\r\n"

        out.write(header.toByteArray(StandardCharsets.UTF_8))
        if (body.isNotEmpty()) {
            out.write(body)
        }
        out.flush()
    }
}
