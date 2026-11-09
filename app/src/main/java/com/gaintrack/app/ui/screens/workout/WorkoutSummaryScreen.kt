package com.gaintrack.app.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.ui.components.StatCard
import com.gaintrack.app.ui.theme.CyanAccent
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBackground
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun WorkoutSummaryScreen(
    sessionId: Long,
    viewModel: WorkoutViewModel,
    onReturnHome: () -> Unit
) {
    val activeState by viewModel.activeState.collectAsState()
    val durationMinutes = activeState.sessionDurationSeconds / 60
    val durationSeconds = activeState.sessionDurationSeconds % 60
    val totalSets = activeState.completedSetRecords.size
    val totalReps = activeState.completedSetRecords.sumOf { it.completedReps }
    val uniqueExercisesCount = activeState.completedSetRecords.map { it.exerciseName }.distinct().size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.15f))
                .border(2.dp, EmeraldPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Success",
                tint = EmeraldPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Workout Completed!",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Excellent effort! Every rep stimulates muscle protein synthesis.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Exercises",
                value = "$uniqueExercisesCount completed",
                subtitle = "Great muscle focus",
                icon = Icons.Default.FitnessCenter,
                iconTint = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Volume",
                value = "$totalSets sets",
                subtitle = "$totalReps total reps",
                icon = Icons.Default.CheckCircle,
                iconTint = CyanAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            title = "Duration",
            value = "%02d:%02d".format(durationMinutes, durationSeconds),
            subtitle = "Focused, efficient home session",
            icon = Icons.Default.Timer,
            iconTint = EmeraldLight,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Post-Workout Nutrition & Recovery Advice
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
            color = NavySurfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Post-Workout Nourishment",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Have a glass of milk with a banana, or a bowl of rice with dhal and egg. Hydrate well and rest!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onReturnHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = NavyDarkest
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "RETURN TO HOME",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
