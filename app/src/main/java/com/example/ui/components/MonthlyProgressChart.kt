package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WorkoutLogEntity
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHighlight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPulse
import com.example.ui.theme.VividFlame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChartMetric(
    val title: String,
    val unit: String,
    val icon: ImageVector,
    val color: Color,
    val gradient: List<Color>
) {
    VOLUME(
        "Weight Volume",
        "kg",
        Icons.Default.FitnessCenter,
        NeonEmerald,
        listOf(NeonEmerald, Color(0xFF059669))
    ),
    DISTANCE(
        "Distance Run",
        "km",
        Icons.Default.DirectionsRun,
        ElectricCyan,
        listOf(ElectricCyan, Color(0xFF0284C7))
    ),
    DURATION(
        "Workout Time",
        "min",
        Icons.Default.Timer,
        AmberGlow,
        listOf(AmberGlow, Color(0xFFD97706))
    ),
    CALORIES(
        "Calories Burned",
        "kcal",
        Icons.Default.LocalFireDepartment,
        VividFlame,
        listOf(VividFlame, Color(0xFFDC2626))
    )
}

@Composable
fun MonthlyProgressChart(
    logs: List<WorkoutLogEntity>,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(ChartMetric.VOLUME) }
    var selectedLogIndex by remember { mutableIntStateOf(-1) }

    // Filter logs for the last 30 days and sort chronologically
    val thirtyDaysAgo = System.currentTimeMillis() - (86400000L * 30)
    val pastMonthLogs = remember(logs) {
        logs.filter { it.timestamp >= thirtyDaysAgo }.sortedBy { it.timestamp }
    }

    val totalMetricValue = when (selectedMetric) {
        ChartMetric.VOLUME -> pastMonthLogs.sumOf { it.totalVolumeKg }
        ChartMetric.DISTANCE -> pastMonthLogs.sumOf { it.distanceKm }
        ChartMetric.DURATION -> pastMonthLogs.sumOf { it.durationMinutes }.toDouble()
        ChartMetric.CALORIES -> pastMonthLogs.sumOf { it.totalCalories }.toDouble()
    }

    val maxMetricValue = when (selectedMetric) {
        ChartMetric.VOLUME -> pastMonthLogs.maxOfOrNull { it.totalVolumeKg } ?: 1.0
        ChartMetric.DISTANCE -> pastMonthLogs.maxOfOrNull { it.distanceKm } ?: 1.0
        ChartMetric.DURATION -> (pastMonthLogs.maxOfOrNull { it.durationMinutes } ?: 1).toDouble()
        ChartMetric.CALORIES -> (pastMonthLogs.maxOfOrNull { it.totalCalories } ?: 1).toDouble()
    }.coerceAtLeast(1.0)

    val avgMetricValue = if (pastMonthLogs.isNotEmpty()) totalMetricValue / pastMonthLogs.size else 0.0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_progress_chart"),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Title & Period
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly Progress",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Last 30 Days Trend (${pastMonthLogs.size} sessions)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = selectedMetric.color.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = selectedMetric.icon,
                            contentDescription = null,
                            tint = selectedMetric.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedMetric.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = selectedMetric.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Selector Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChartMetric.entries.forEach { metric ->
                    val isSelected = selectedMetric == metric
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedMetric = metric
                                selectedLogIndex = -1
                            }
                            .testTag("metric_tab_${metric.name.lowercase()}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) metric.color.copy(alpha = 0.2f) else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) metric.color else CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = metric.icon,
                                contentDescription = null,
                                tint = if (isSelected) metric.color else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = metric.unit,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) metric.color else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total & Average Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "30-Day Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = formatMetricValue(totalMetricValue, selectedMetric) + " " + selectedMetric.unit,
                        style = MaterialTheme.typography.headlineMedium,
                        color = selectedMetric.color,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Avg / Session",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = formatMetricValue(avgMetricValue, selectedMetric) + " " + selectedMetric.unit,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Chart Canvas
            if (pastMonthLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log workouts to start visualizing monthly progress!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val gradientColors = selectedMetric.gradient
                    val barColor = selectedMetric.color

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .pointerInput(pastMonthLogs, selectedMetric) {
                                detectTapGestures { offset ->
                                    val count = pastMonthLogs.size
                                    if (count > 0) {
                                        val barWidth = size.width / count
                                        val tappedIndex = (offset.x / barWidth).toInt().coerceIn(0, count - 1)
                                        selectedLogIndex = tappedIndex
                                    }
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val count = pastMonthLogs.size

                        // Draw horizontal background guide lines
                        val numGridLines = 3
                        for (i in 0..numGridLines) {
                            val y = canvasHeight * (i / numGridLines.toFloat())
                            drawLine(
                                color = CardBorder.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Bar spacing & widths
                        val slotWidth = canvasWidth / count
                        val barWidth = (slotWidth * 0.55f).coerceIn(6.dp.toPx(), 24.dp.toPx())

                        pastMonthLogs.forEachIndexed { index, log ->
                            val rawValue = when (selectedMetric) {
                                ChartMetric.VOLUME -> log.totalVolumeKg
                                ChartMetric.DISTANCE -> log.distanceKm
                                ChartMetric.DURATION -> log.durationMinutes.toDouble()
                                ChartMetric.CALORIES -> log.totalCalories.toDouble()
                            }

                            val fraction = (rawValue / maxMetricValue).toFloat().coerceIn(0.04f, 1f)
                            val barHeight = canvasHeight * fraction
                            val xPos = (index * slotWidth) + (slotWidth - barWidth) / 2f
                            val yPos = canvasHeight - barHeight

                            val isTapped = index == selectedLogIndex

                            // Bar glow effect if tapped
                            if (isTapped) {
                                drawRoundRect(
                                    color = barColor.copy(alpha = 0.35f),
                                    topLeft = Offset(xPos - 4.dp.toPx(), yPos - 4.dp.toPx()),
                                    size = Size(barWidth + 8.dp.toPx(), barHeight + 4.dp.toPx()),
                                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                )
                            }

                            // Main Gradient Bar
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = if (rawValue > 0) gradientColors else listOf(CardBorder, CardBorder),
                                    startY = yPos,
                                    endY = canvasHeight
                                ),
                                topLeft = Offset(xPos, yPos),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }
                    }

                    // Bottom Date Range Axis Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val firstDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(pastMonthLogs.first().timestamp))
                        val lastDate = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(pastMonthLogs.last().timestamp))
                        Text(firstDate, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("Past 30 Days", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(lastDate, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            }

            // Tapped Inspection Card Popover
            if (selectedLogIndex in pastMonthLogs.indices) {
                val tappedLog = pastMonthLogs[selectedLogIndex]
                val dateStr = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(tappedLog.timestamp))
                val tappedVal = when (selectedMetric) {
                    ChartMetric.VOLUME -> "${tappedLog.totalVolumeKg.toInt()} kg lifted"
                    ChartMetric.DISTANCE -> "${String.format("%.1f", tappedLog.distanceKm)} km run"
                    ChartMetric.DURATION -> "${tappedLog.durationMinutes} min active"
                    ChartMetric.CALORIES -> "${tappedLog.totalCalories} kcal burned"
                }

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCardHighlight,
                    border = BorderStroke(1.dp, selectedMetric.color.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tappedLog.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$dateStr • ${tappedLog.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = tappedVal,
                            style = MaterialTheme.typography.titleSmall,
                            color = selectedMetric.color,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

private fun formatMetricValue(value: Double, metric: ChartMetric): String {
    return when (metric) {
        ChartMetric.VOLUME, ChartMetric.CALORIES -> value.toInt().toString()
        ChartMetric.DISTANCE -> String.format(Locale.getDefault(), "%.1f", value)
        ChartMetric.DURATION -> value.toInt().toString()
    }
}
