package com.gaintrack.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gaintrack.app.ui.theme.AmberWarning
import com.gaintrack.app.ui.theme.CyanAccent
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun HealthAssessmentBanner(
    bmi: Double,
    modifier: Modifier = Modifier
) {
    if (bmi in 0.1..18.49) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            color = AmberWarning.copy(alpha = 0.12f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "Health notice",
                    tint = AmberWarning,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Healthcare Assessment Advised",
                        style = MaterialTheme.typography.titleSmall,
                        color = AmberWarning,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your current BMI (%.1f) is below the standard adult range. We recommend arranging a check-up with a doctor or clinical dietitian. GainTrack promotes safe, gradual muscle and weight gains (+0.1 to 0.25 kg/wk) — no extreme overfeeding or unsafe supplements.".format(bmi),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector? = null,
    iconTint: Color = EmeraldPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
        color = NavySurface
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
fun NutritionProgressBar(
    label: String,
    current: Int,
    target: Int,
    unit: String,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $target $unit ($percentage%)",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = NavySurfaceVariant,
        )
    }
}

@Composable
fun RestTimerDialog(
    initialSeconds: Int = 60,
    onDismiss: () -> Unit
) {
    var remainingSeconds by remember { mutableIntStateOf(initialSeconds) }
    var isRunning by remember { androidx.compose.runtime.mutableStateOf(true) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Rest Timer",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rest Interval",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(NavySurfaceVariant)
                        .border(3.dp, if (remainingSeconds > 0) EmeraldPrimary else AmberWarning, CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingSeconds > 0) TextWhite else EmeraldPrimary
                        )
                        Text(
                            text = if (remainingSeconds > 0) "Rest & Breathe" else "Time's up! Ready!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { remainingSeconds += 30 },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+30s", color = TextWhite)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next Set", color = NavyDarkest, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
