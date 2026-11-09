package com.gaintrack.app.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.data.local.entity.WeightLogEntity
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun WeightTrendChart(
    weightLogs: List<WeightLogEntity>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
        color = NavySurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEIGHT PROGRESS (KG)",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                if (weightLogs.isNotEmpty()) {
                    val current = weightLogs.last().weightKg
                    Text(
                        text = "Latest: %.1f kg".format(current),
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No weight records logged yet", color = TextMuted)
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 30f
                    val chartHeight = height - paddingBottom

                    val weights = weightLogs.map { it.weightKg }
                    val minW = (weights.minOrNull() ?: 45.0) - 0.5
                    val maxW = (weights.maxOrNull() ?: 50.0) + 0.5
                    val rangeW = (maxW - minW).coerceAtLeast(1.0)

                    // Draw 3 horizontal guide lines
                    val stepY = chartHeight / 2f
                    for (i in 0..2) {
                        val y = i * stepY
                        drawLine(
                            color = NavyBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (weights.size == 1) {
                        val singleY = chartHeight - ((weights[0] - minW) / rangeW * chartHeight).toFloat()
                        drawCircle(
                            color = EmeraldPrimary,
                            radius = 6.dp.toPx(),
                            center = Offset(width / 2f, singleY)
                        )
                    } else {
                        val points = mutableListOf<Offset>()
                        val stepX = width / (weights.size - 1).coerceAtLeast(1)

                        weights.forEachIndexed { index, w ->
                            val x = index * stepX
                            val y = chartHeight - ((w - minW) / rangeW * chartHeight).toFloat()
                            points.add(Offset(x, y))
                        }

                        // Gradient fill path
                        val fillPath = Path().apply {
                            moveTo(points.first().x, chartHeight)
                            for (p in points) {
                                lineTo(p.x, p.y)
                            }
                            lineTo(points.last().x, chartHeight)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldPrimary.copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = chartHeight
                            )
                        )

                        // Line stroke path
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        drawPath(
                            path = strokePath,
                            color = EmeraldPrimary,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Data circles
                        for (p in points) {
                            drawCircle(
                                color = NavySurface,
                                radius = 5.dp.toPx(),
                                center = p
                            )
                            drawCircle(
                                color = EmeraldLight,
                                radius = 3.5.dp.toPx(),
                                center = p
                            )
                        }
                    }
                }

                // X-axis date subtitles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weightLogs.first().dateString.takeLast(5),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    if (weightLogs.size > 1) {
                        Text(
                            text = weightLogs.last().dateString.takeLast(5),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyWorkoutConsistencyBar(
    completedDaysOfWeek: Set<String>, // e.g. ["MONDAY", "TUESDAY"]
    modifier: Modifier = Modifier
) {
    val days = listOf(
        "Mon" to "MONDAY",
        "Tue" to "TUESDAY",
        "Wed" to "WEDNESDAY",
        "Thu" to "THURSDAY",
        "Fri" to "FRIDAY",
        "Sat" to "SATURDAY",
        "Sun" to "SUNDAY"
    )

    val targetWorkoutDays = setOf("MONDAY", "TUESDAY", "THURSDAY", "SATURDAY")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
        color = NavySurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEKLY CONSISTENCY",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                val count = completedDaysOfWeek.count { targetWorkoutDays.contains(it) }
                Text(
                    text = "$count / 4 Workouts",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { (abbr, fullDay) ->
                    val isDone = completedDaysOfWeek.contains(fullDay)
                    val isScheduled = targetWorkoutDays.contains(fullDay)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isDone -> EmeraldPrimary
                                        isScheduled -> NavySurfaceVariant
                                        else -> NavySurfaceVariant.copy(alpha = 0.35f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isDone) EmeraldLight else if (isScheduled) NavyBorder else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isDone) "✓" else if (isScheduled) "•" else "-",
                                color = if (isDone) NavySurface else if (isScheduled) TextWhite else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = abbr,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isScheduled) TextWhite else TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
