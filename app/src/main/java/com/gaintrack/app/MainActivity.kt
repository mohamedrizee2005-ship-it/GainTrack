package com.gaintrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gaintrack.app.ui.navigation.AppNavigation
import com.gaintrack.app.ui.theme.GainTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GainTrackApp

        setContent {
            GainTrackTheme {
                AppNavigation(app = app)
            }
        }
    }
}
