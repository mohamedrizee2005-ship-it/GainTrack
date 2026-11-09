package com.gaintrack.app.ui.screens.progress

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.data.local.entity.WeeklyCheckInEntity
import com.gaintrack.app.ui.components.StatCard
import com.gaintrack.app.ui.components.WeightTrendChart
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
fun ProgressScreen(
    viewModel: ProgressViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showCheckInDialog by remember { mutableStateOf(false) }
    var showQuickWeightDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("%.1f".format(uiState.userProfile.currentWeightKg)) }
    var newWeightNotes by remember { mutableStateOf("") }

    if (showCheckInDialog) {
        CheckInDialog(
            initialWeightKg = uiState.userProfile.currentWeightKg,
            onSubmitted = { w, workouts, energy, app, sleep, sore, notes ->
                viewModel.submitSundayCheckIn(w, workouts, energy, app, sleep, sore, notes)
            },
            onDismiss = { showCheckInDialog = false }
        )
    }

    if (showQuickWeightDialog) {
        AlertDialog(
            onDismissRequest = { showQuickWeightDialog = false },
            title = { Text("Log Body Weight", color = TextWhite) },
            text = {
                Column {
                    Text("Enter today's morning weight (kg):", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newWeightInput,
                        onValueChange = { newWeightInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newWeightNotes,
                        onValueChange = { newWeightNotes = it },
                        placeholder = { Text("Notes (optional)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = newWeightInput.toDoubleOrNull() ?: uiState.userProfile.currentWeightKg
                        viewModel.logNewWeight(w, newWeightNotes)
                        showQuickWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save Weight", color = NavyDarkest, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickWeightDialog = false }) {
                    Text("Cancel", color = TextWhite)
                }
            },
            containerColor = NavySurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Text(
            text = "Progress & Analytics",
            style = MaterialTheme.typography.headlineLarge,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Track safe, steady weight & strength accumulation",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 12-Week Fitness Journey Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "12-WEEK JOURNEY",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Safe & Gradual",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiState.currentPhase,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Phase 1: Foundation & technique • Phase 2: Overload • Phase 3: Muscle consistency",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Timeframe selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                7 to "7 Days",
                30 to "30 Days",
                90 to "90 Days",
                365 to "All Time"
            ).forEach { (days, label) ->
                FilterChip(
                    selected = uiState.selectedTimeframeDays == days,
                    onClick = { viewModel.setTimeframe(days) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = NavyDarkest,
                        containerColor = NavySurfaceVariant,
                        labelColor = TextWhite
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Weight Trend Chart
        WeightTrendChart(weightLogs = uiState.filteredWeightLogs)

        Spacer(modifier = Modifier.height(14.dp))

        // Quick log weight & Sunday check-in buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { showQuickWeightDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Weight", color = TextWhite)
            }

            Button(
                onClick = { showCheckInDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = NavyDarkest
                )
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sunday Check-in", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Overall Training Stats Grid
        Text(
            text = "Training Accumulation",
            style = MaterialTheme.typography.titleMedium,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Workouts",
                value = "${uiState.totalWorkoutsCompleted} sessions",
                subtitle = "Home consistency",
                icon = Icons.Default.FitnessCenter,
                iconTint = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Sets",
                value = "${uiState.totalSetsCompleted} sets",
                subtitle = "Progressive overload",
                icon = Icons.Default.CheckCircle,
                iconTint = CyanAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Reps",
                value = "${uiState.totalRepsCompleted} reps",
                subtitle = "Total muscle volume",
                icon = Icons.Default.Repeat,
                iconTint = EmeraldLight,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "This Week",
                value = "${uiState.weeklyWorkoutsDoneThisWeek} / 4 sessions",
                subtitle = "Target: 4 days/wk",
                icon = Icons.Default.DateRange,
                iconTint = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Sunday Check-ins List
        if (uiState.recentCheckIns.isNotEmpty()) {
            Text(
                text = "Sunday Check-in History",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            uiState.recentCheckIns.take(4).forEach { checkIn ->
                CheckInHistoryRow(checkIn = checkIn)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun CheckInHistoryRow(checkIn: WeeklyCheckInEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        color = NavySurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = checkIn.dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%.1f kg".format(checkIn.weightKg),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Workouts: ${checkIn.workoutsCompleted}/4 • Energy: ${checkIn.energyLevel} • Appetite: ${checkIn.appetite} • Sleep: ${checkIn.averageSleepHours}h",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            if (checkIn.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${checkIn.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}
