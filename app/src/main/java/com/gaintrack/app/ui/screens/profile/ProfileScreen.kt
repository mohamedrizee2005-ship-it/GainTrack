package com.gaintrack.app.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.gaintrack.app.ui.components.HealthAssessmentBanner
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
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }

    // Form states
    var currentWeight by remember(uiState.profile) { mutableStateOf(uiState.profile.currentWeightKg.toString()) }
    var targetWeight by remember(uiState.profile) { mutableStateOf(uiState.profile.targetWeightKg.toString()) }
    var heightCm by remember(uiState.profile) { mutableStateOf(uiState.profile.heightCm.toString()) }
    var calorieTarget by remember(uiState.profile) { mutableStateOf(uiState.profile.calorieTarget.toString()) }
    var proteinTarget by remember(uiState.profile) { mutableStateOf(uiState.profile.proteinTarget.toString()) }
    var workoutTime by remember(uiState.profile) { mutableStateOf(uiState.profile.preferredWorkoutTime) }

    var workoutReminders by remember(uiState.profile) { mutableStateOf(uiState.profile.workoutReminderEnabled) }
    var checkInReminders by remember(uiState.profile) { mutableStateOf(uiState.profile.checkInReminderEnabled) }
    var nutritionReminders by remember(uiState.profile) { mutableStateOf(uiState.profile.nutritionReminderEnabled) }
    var waterReminders by remember(uiState.profile) { mutableStateOf(uiState.profile.waterReminderEnabled) }
    var sleepReminders by remember(uiState.profile) { mutableStateOf(uiState.profile.sleepReminderEnabled) }

    // Permission launcher for Android 13+ notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPerm) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Application Data?", color = TextWhite) },
            text = {
                Text(
                    "This will restore all GainTrack workout plans, Sri Lankan foods, and user profiles back to original defaults. All custom logs will be reset.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("Confirm Reset", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Personal fitness targets and reminder schedules",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Health Assessment Advice Banner
        HealthAssessmentBanner(bmi = uiState.profile.bmi)

        Spacer(modifier = Modifier.height(18.dp))

        // Personal Stats Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Body & Target Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = currentWeight,
                        onValueChange = { currentWeight = it },
                        label = { Text("Weight (kg)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        label = { Text("Goal (kg)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = { Text("Height (cm)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = calorieTarget,
                        onValueChange = { calorieTarget = it },
                        label = { Text("Calories (kcal)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    OutlinedTextField(
                        value = proteinTarget,
                        onValueChange = { proteinTarget = it },
                        label = { Text("Protein (g)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = workoutTime,
                    onValueChange = { workoutTime = it },
                    label = { Text("Preferred Workout Time (HH:mm)", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = NavyBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Summary Details
                Text(
                    text = "Fitness Level: ${uiState.profile.fitnessLevel} • Push-up ability: ${uiState.profile.pushupAbility}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "Equipment: ${uiState.profile.trainingLocation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "Food: ${uiState.profile.foodPreference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val cw = currentWeight.toDoubleOrNull() ?: uiState.profile.currentWeightKg
                        val tw = targetWeight.toDoubleOrNull() ?: uiState.profile.targetWeightKg
                        val h = heightCm.toDoubleOrNull() ?: uiState.profile.heightCm
                        val cal = calorieTarget.toIntOrNull() ?: uiState.profile.calorieTarget
                        val prot = proteinTarget.toIntOrNull() ?: uiState.profile.proteinTarget
                        viewModel.updateProfile(
                            currentWeight = cw,
                            targetWeight = tw,
                            height = h,
                            calorieTarget = cal,
                            proteinTarget = prot,
                            workoutTime = workoutTime,
                            workoutReminder = workoutReminders,
                            checkInReminder = checkInReminders,
                            nutritionReminder = nutritionReminders,
                            waterReminder = waterReminders,
                            sleepReminder = sleepReminders
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reminders & Local Notifications Settings
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reminders & Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ReminderToggleRow(
                    title = "Night Workout Reminder",
                    subtitle = "7:30 PM on scheduled training days",
                    checked = workoutReminders,
                    onCheckedChange = { workoutReminders = it }
                )

                ReminderToggleRow(
                    title = "Sunday Weight Check-in",
                    subtitle = "7:00 PM every Sunday evening",
                    checked = checkInReminders,
                    onCheckedChange = { checkInReminders = it }
                )

                ReminderToggleRow(
                    title = "Daily Nutrition Tracking",
                    subtitle = "Prompt to log lunch, dinner & weight-gain shake",
                    checked = nutritionReminders,
                    onCheckedChange = { nutritionReminders = it }
                )

                ReminderToggleRow(
                    title = "Drinking Water & Hydration",
                    subtitle = "Periodic hydration reminders throughout day",
                    checked = waterReminders,
                    onCheckedChange = { waterReminders = it }
                )

                ReminderToggleRow(
                    title = "Sleep & Recovery",
                    subtitle = "10:30 PM bedtime notification for muscle repair",
                    checked = sleepReminders,
                    onCheckedChange = { sleepReminders = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Data Management & Backup
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Data Management",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { viewModel.exportDataToCsv(context) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = TextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Training & Weight Data to CSV", color = TextWhite)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = RedError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Application Data", color = RedError)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ReminderToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmeraldPrimary,
                checkedTrackColor = NavySurfaceVariant,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = NavyDarkest
            )
        )
    }
}
