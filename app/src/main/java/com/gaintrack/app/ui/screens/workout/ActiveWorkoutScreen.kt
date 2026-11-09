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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.ui.components.RestTimerDialog
import com.gaintrack.app.ui.theme.AmberWarning
import com.gaintrack.app.ui.theme.CyanAccent
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBackground
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.RedError
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun ActiveWorkoutScreen(
    planId: Long,
    viewModel: WorkoutViewModel,
    onWorkoutFinished: (Long) -> Unit,
    onCancel: () -> Unit
) {
    val activeState by viewModel.activeState.collectAsState()
    val scrollState = rememberScrollState()
    var showEndEarlyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        viewModel.startWorkoutSession(planId)
    }

    LaunchedEffect(activeState.isSessionFinished, activeState.savedSessionId) {
        if (activeState.isSessionFinished && activeState.savedSessionId != null) {
            onWorkoutFinished(activeState.savedSessionId!!)
        }
    }

    val currentEx = activeState.exercises.getOrNull(activeState.currentExerciseIndex)

    if (activeState.showRestTimer) {
        RestTimerDialog(
            initialSeconds = activeState.restTimerDuration,
            onDismiss = { viewModel.dismissRestTimer() }
        )
    }

    if (showEndEarlyDialog) {
        AlertDialog(
            onDismissRequest = { showEndEarlyDialog = false },
            title = { Text("End Session Early?", color = TextWhite) },
            text = {
                Text(
                    "You can finish now and save all completed sets to your training history.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndEarlyDialog = false
                        viewModel.finishAndSaveWorkout(isComplete = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save & Finish", color = NavyDarkest, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndEarlyDialog = false }) {
                    Text("Keep Training", color = TextWhite)
                }
            },
            containerColor = NavySurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Toolbar: Pause/Resume, Timer, End Early
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.togglePauseTimer() }) {
                    Icon(
                        imageVector = if (activeState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Resume",
                        tint = EmeraldPrimary
                    )
                }
                val duration = activeState.sessionDurationSeconds
                Text(
                    text = "%02d:%02d".format(duration / 60, duration % 60),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (activeState.isPaused) AmberWarning else TextWhite,
                    fontWeight = FontWeight.Bold
                )
                if (activeState.isPaused) {
                    Text(
                        text = " (Paused)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberWarning
                    )
                }
            }

            TextButton(onClick = { showEndEarlyDialog = true }) {
                Text("End Early", color = TextMuted, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Workout Progress Bar
        val totalExpectedSets = activeState.exercises.sumOf { it.defaultSets }.coerceAtLeast(1)
        val completedCount = activeState.completedSetRecords.size
        val progressFraction = (completedCount.toFloat() / totalExpectedSets).coerceIn(0f, 1f)

        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = EmeraldPrimary,
            trackColor = NavySurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (currentEx != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Exercise Counter & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXERCISE ${activeState.currentExerciseIndex + 1} OF ${activeState.exercises.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentEx.targetMuscles,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentEx.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progressive Overload Alert Banner
                if (activeState.progressiveOverloadAlert != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, EmeraldLight, RoundedCornerShape(12.dp)),
                        color = EmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeState.progressiveOverloadAlert!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Previous Performance Indicator
                if (activeState.previousExercisePerformance.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                        color = NavySurface
                    ) {
                        Text(
                            text = activeState.previousExercisePerformance,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanAccent,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Current Set Logging Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                    color = NavySurface
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Set ${activeState.currentSetNumber} of ${currentEx.defaultSets}",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            val targetRepsText = if (currentEx.minReps == currentEx.maxReps) "${currentEx.minReps} reps" else "${currentEx.minReps}–${currentEx.maxReps} reps"
                            Text(
                                text = "Target: $targetRepsText",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = activeState.inputReps,
                                onValueChange = { viewModel.updateInputReps(it) },
                                label = { Text("Reps Completed", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = NavyBorder
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = activeState.inputWeightKg,
                                onValueChange = { viewModel.updateInputWeight(it) },
                                label = { Text("Backpack (kg)", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = NavyBorder
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.completeCurrentSet() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Complete Set")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMPLETE SET ${activeState.currentSetNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Technique & Safety Form Guide
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                    color = NavySurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Form Instructions:",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentEx.formInstructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentEx.safetyTips,
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberWarning
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Navigation Footer: Previous & Next Exercise
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.goToPreviousExercise() },
                enabled = activeState.currentExerciseIndex > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous", color = if (activeState.currentExerciseIndex > 0) TextWhite else TextMuted)
            }

            OutlinedButton(
                onClick = { viewModel.goToNextExercise() },
                enabled = activeState.currentExerciseIndex < activeState.exercises.size - 1,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next Ex", color = if (activeState.currentExerciseIndex < activeState.exercises.size - 1) TextWhite else TextMuted)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
    }
}
