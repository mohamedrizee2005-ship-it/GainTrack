package com.gaintrack.app.ui.navigation

import androidx.compose.runtime.Composable
import com.gaintrack.app.GainTrackApp
import com.gaintrack.app.ui.screens.v2.GainTrackV2Screen

@Composable
fun AppNavigation(app: GainTrackApp) {
    // GainTrack 2.0 is a focused bodyweight training app.
    // The application singleton remains initialized for the existing local database.
    GainTrackV2Screen()
}
