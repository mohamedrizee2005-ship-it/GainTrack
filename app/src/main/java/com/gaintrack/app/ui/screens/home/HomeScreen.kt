package com.gaintrack.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.ui.components.HealthAssessmentBanner
import com.gaintrack.app.ui.components.NutritionProgressBar
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
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartWorkout: (Long) -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToProgress: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GainTrack",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Muscle & Weight Gain Tracker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NavySurfaceVariant)
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Health Assessment Recommendation Banner (for BMI < 18.5)
        HealthAssessmentBanner(bmi = uiState.userProfile.bmi)

        Spacer(modifier = Modifier.height(16.dp))

        // Overview Weight & BMI Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Current Weight",
                value = "%.1f kg".format(uiState.userProfile.currentWeightKg),
                subtitle = "Goal: %.1f kg".format(uiState.userProfile.targetWeightKg),
                icon = Icons.Default.TrendingUp,
                iconTint = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            val deltaPrefix = if (uiState.weightChangeSinceLastWeek >= 0) "+" else ""
            StatCard(
                title = "BMI / Delta",
                value = "%.1f".format(uiState.userProfile.bmi),
                subtitle = "$deltaPrefix%.2f kg vs last wk".format(uiState.weightChangeSinceLastWeek),
                icon = Icons.Default.CheckCircle,
                iconTint = CyanAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scheduled Workout Card with START WORKOUT Button
        val activeWorkoutPlan = uiState.todayWorkout ?: uiState.nextScheduledWorkout
        val isTodaySession = uiState.todayWorkout != null

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            color = NavySurface
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmeraldPrimary.copy(alpha = 0.12f),
                                NavySurface
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTodaySession) "TODAY'S SCHEDULED WORKOUT" else "UPCOMING WORKOUT (${uiState.nextWorkoutDayText})",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "35 Mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activeWorkoutPlan?.title ?: "Full Body Foundation",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activeWorkoutPlan?.description
                        ?: "Progressive home workout with backpack resistance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        activeWorkoutPlan?.let { onStartWorkout(it.id) } ?: onNavigateToWorkouts()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = NavyDarkest
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Workout",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START WORKOUT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Nutrition Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                .clickable { onNavigateToNutrition() },
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Today's Nutrition Intake",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Nutrition",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                NutritionProgressBar(
                    label = "Calories (Surplus Target)",
                    current = uiState.todayCalories,
                    target = uiState.userProfile.calorieTarget,
                    unit = "kcal",
                    barColor = EmeraldPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                NutritionProgressBar(
                    label = "Protein Target",
                    current = uiState.todayProtein.toInt(),
                    target = uiState.userProfile.proteinTarget,
                    unit = "g",
                    barColor = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Consistency & Next Check-in
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .clickable { onNavigateToProgress() },
                color = NavySurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WEEKLY SESSIONS",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${uiState.completedWorkoutsCountThisWeek} / 4 Done",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.weeklyCompletionPercentage}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    .clickable { onNavigateToProgress() },
                color = NavySurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "NEXT CHECK-IN",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.nextCheckInDateText.ifBlank { "Sunday" },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Weight & Energy review",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Motivational Insight
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
            color = NavySurfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.motivationalQuote,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextWhite.copy(alpha = 0.95f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
