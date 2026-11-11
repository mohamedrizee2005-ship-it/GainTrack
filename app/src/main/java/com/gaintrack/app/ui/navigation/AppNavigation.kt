package com.gaintrack.app.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gaintrack.app.data.local.SecureGeminiSettings
import com.gaintrack.app.GainTrackApp
import com.gaintrack.app.ui.screens.nutrition.NutritionDashboard
import com.gaintrack.app.ui.screens.v2.GainTrackV2Screen

@Composable
fun AppNavigation(app: GainTrackApp) {
    var dashboard by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    var geminiApiKey by remember { mutableStateOf(SecureGeminiSettings.loadKey(context)) }
    var geminiModel by remember { mutableStateOf(SecureGeminiSettings.model(context)) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            TabRow(selectedTabIndex = dashboard) {
                Tab(selected = dashboard == 0, onClick = { dashboard = 0 }, text = { Text("WORKOUT") })
                Tab(selected = dashboard == 1, onClick = { dashboard = 1 }, text = { Text("NUTRITION") })
            }
            if (dashboard == 0) GainTrackV2Screen() else NutritionDashboard(
                app = app,
                apiKey = geminiApiKey,
                model = geminiModel,
                onSaveKey = { newKey ->
                    try {
                        SecureGeminiSettings.saveKey(context, newKey)
                        geminiApiKey = newKey.trim()
                        true
                    } catch (_: Exception) {
                        false
                    }
                },
                onForgetKey = {
                    SecureGeminiSettings.clearKey(context)
                    geminiApiKey = ""
                },
                onModelChange = { selected ->
                    SecureGeminiSettings.saveModel(context, selected)
                    geminiModel = SecureGeminiSettings.model(context)
                }
            )
        }
    }
}
