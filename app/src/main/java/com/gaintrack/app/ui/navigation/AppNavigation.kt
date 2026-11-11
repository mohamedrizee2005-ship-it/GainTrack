package com.gaintrack.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gaintrack.app.GainTrackApp
import com.gaintrack.app.ui.screens.v2.GainTrackV2Screen

/** GainTrack 4 is a focused, offline-first workout app. */
@Composable
fun AppNavigation(app: GainTrackApp) {
    Surface(modifier = Modifier.fillMaxSize()) {
        GainTrackV2Screen(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding())
    }
}
