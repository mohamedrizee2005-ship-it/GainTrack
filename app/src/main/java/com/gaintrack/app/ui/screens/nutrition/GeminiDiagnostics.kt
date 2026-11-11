package com.gaintrack.app.ui.screens.nutrition

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

/** Do not include API keys or full Google responses in messages or logs. */
internal fun friendlyGeminiError(e: Exception): String = when (e) {
    is java.net.UnknownHostException ->
        "AI error: Google's API address could not be found. Try Wi-Fi/mobile data or check Private DNS and VPN."
    is java.net.SocketTimeoutException ->
        "AI error: Google did not respond before timeout. Try another network or retry later."
    is javax.net.ssl.SSLException ->
        "AI error: Secure connection (TLS) failed. Check the phone date, Private DNS and VPN."
    is java.net.ConnectException ->
        "AI error: Could not connect to Google's server. Switch networks and retry."
    is java.net.SocketException ->
        "AI error: Connection was reset. Switch networks and retry."
    is org.json.JSONException ->
        "AI error: Gemini returned unreadable JSON. Try a new photo."
    else -> e.message?.takeIf {
        it.startsWith("AI error") || it.startsWith("Gemini returned") || it.startsWith("Gemini did not")
    } ?: "AI error: Request failed. Use Test AI connection for a clearer diagnosis."
}

/** One small text-only request to confirm API key, chosen model, quota and network connectivity. */
internal fun checkGeminiConnection(key: String, model: String): String {
    if (key.isBlank()) return "AI error: Save a Gemini API key first."
    val conn = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent")
        .openConnection() as HttpURLConnection
    try {
        conn.requestMethod = "POST"
        conn.connectTimeout = 10000
        conn.readTimeout = 20000
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("x-goog-api-key", key.trim())
        val body = JSONObject().put(
            "contents", JSONArray().put(JSONObject().put(
                "parts", JSONArray().put(JSONObject().put("text", "Reply with OK."))
            ))
        )
        conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val http = conn.responseCode
        return when (http) {
            in 200..299 -> "AI connection successful ($model). Try scanning food now."
            400 -> "AI error 400: Google rejected the request. Check API project and model settings."
            401, 403 -> "AI error $http: Invalid or unauthorized key. Generate a new Gemini API key."
            404 -> "AI error 404: Selected model unavailable to this project. Change the model."
            429 -> "AI error 429: Project quota/rate limit reached. Check AI Studio usage."
            in 500..599 -> "AI error $http: Google server temporarily unavailable. Retry later."
            else -> "AI error $http: Google refused the connection. Check the account."
        }
    } finally {
        conn.disconnect()
    }
}
