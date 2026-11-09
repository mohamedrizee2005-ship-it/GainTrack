package com.gaintrack.app.ui.screens.progress

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun CheckInDialog(
    initialWeightKg: Double,
    onSubmitted: (
        weightKg: Double,
        workoutsCompleted: Int,
        energyLevel: String,
        appetite: String,
        sleepHours: Double,
        soreness: String,
        notes: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var weightInput by remember { mutableStateOf("%.1f".format(initialWeightKg)) }
    var workoutsCompleted by remember { mutableIntStateOf(4) }
    var energyLevel by remember { mutableStateOf("Moderate") }
    var appetite by remember { mutableStateOf("Normal") }
    var sleepInput by remember { mutableStateOf("7.5") }
    var soreness by remember { mutableStateOf("Mild") }
    var notes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(20.dp)),
            color = NavySurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sunday Weekly Check-in",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your consistency, appetite & gradual progress",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldLight
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Safe Progress Tip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = NavySurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Safe muscle gain initial guide: +0.1 to 0.25 kg per week. Temporary water fluctuations are natural.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Current Weight
                Text("1. Current Body Weight (kg):", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Workouts Completed out of 4
                Text("2. Workouts completed this week (out of 4):", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (0..4).forEach { count ->
                        FilterChip(
                            selected = workoutsCompleted == count,
                            onClick = { workoutsCompleted = count },
                            label = { Text("$count / 4") },
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

                // 3. Energy Level
                Text("3. Energy Level:", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Low", "Moderate", "High", "Very High").forEach { opt ->
                        FilterChip(
                            selected = energyLevel == opt,
                            onClick = { energyLevel = opt },
                            label = { Text(opt, fontSize = 11.sp) },
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

                // 4. Appetite
                Text("4. Appetite & Eating:", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Low", "Normal", "High").forEach { opt ->
                        FilterChip(
                            selected = appetite == opt,
                            onClick = { appetite = opt },
                            label = { Text(opt) },
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

                // 5. Average Sleep
                Text("5. Average Sleep (hours per night):", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sleepInput,
                    onValueChange = { sleepInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Muscle Soreness
                Text("6. Muscle Soreness or Joint Pain:", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("None", "Mild", "Moderate", "High").forEach { opt ->
                        FilterChip(
                            selected = soreness == opt,
                            onClick = { soreness = opt },
                            label = { Text(opt, fontSize = 11.sp) },
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

                // 7. Additional Notes
                Text("7. Personal Notes (optional):", color = TextWhite, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("e.g. Hit all push-up reps, felt energetic after Sri Lankan meals", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val w = weightInput.toDoubleOrNull() ?: initialWeightKg
                        val s = sleepInput.toDoubleOrNull() ?: 7.5
                        onSubmitted(w, workoutsCompleted, energyLevel, appetite, s, soreness, notes)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save Sunday Check-in", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
