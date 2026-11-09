package com.gaintrack.app.ui.screens.workout

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.data.local.PrepopulatedData
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.local.entity.WorkoutSessionEntity
import com.gaintrack.app.ui.theme.EmeraldDark
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBackground
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutsScreen(
    viewModel: WorkoutViewModel,
    onStartWorkout: (Long) -> Unit
) {
    val plans by viewModel.allPlans.collectAsState()
    val recentSessions by viewModel.recentSessions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Weekly Workout Plans",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "4-Day progressive home routine with backpack resistance & bodyweight.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }

        items(plans) { plan ->
            WorkoutPlanCard(
                plan = plan,
                onStart = { onStartWorkout(plan.id) }
            )
        }

        if (recentSessions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Completed Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(recentSessions) { session ->
                WorkoutHistoryCard(session = session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun WorkoutPlanCard(
    plan: WorkoutPlanEntity,
    onStart: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val exercises = remember(plan.id) { PrepopulatedData.getExercisesForPlan(plan.id) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
        color = NavySurface
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.dayOfWeek,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${plan.estimatedDurationMinutes} mins • Home",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plan.targetMuscleGroups,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start Workout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Exercises",
                        tint = TextMuted
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Exercises Included:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    exercises.forEachIndexed { index, ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}. ${ex.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                            val repText = if (ex.minReps == ex.maxReps) "${ex.minReps} reps" else "${ex.minReps}–${ex.maxReps} reps"
                            Text(
                                text = "${ex.defaultSets} × $repText",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutHistoryCard(session: WorkoutSessionEntity) {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val dateText = sdf.format(Date(session.startTimeEpochMs))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        color = NavySurfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.workoutTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.completedSets} sets • ${session.totalReps} reps",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "%02d:%02d".format(session.durationSeconds / 60, session.durationSeconds % 60),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
