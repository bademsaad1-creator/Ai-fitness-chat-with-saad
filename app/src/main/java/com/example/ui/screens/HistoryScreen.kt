package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WorkoutLogEntity
import com.example.ui.components.LogWorkoutDialog
import com.example.ui.components.MonthlyProgressChart
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividFlame
import com.example.ui.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: FitnessViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.workoutLogs.collectAsState()
    val totalWorkouts by viewModel.totalWorkoutsCount.collectAsState()
    val totalCalories by viewModel.totalCaloriesBurned.collectAsState()
    val totalDistance by viewModel.totalDistanceRun.collectAsState()
    val totalVolume by viewModel.totalVolumeLifted.collectAsState()
    val totalDuration by viewModel.totalDurationMinutes.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }

    if (showLogDialog) {
        LogWorkoutDialog(
            onDismiss = { showLogDialog = false },
            onSave = { title, category, duration, volume, distance, calories, notes ->
                showLogDialog = false
                viewModel.logManualWorkout(
                    title = title,
                    category = category,
                    durationMinutes = duration,
                    volumeKg = volume,
                    distanceKm = distance,
                    calories = calories,
                    notes = notes
                )
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Back & + Log Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("history_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Progress & Analytics",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$totalWorkouts logged sessions to date",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    Button(
                        onClick = { showLogDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("log_workout_open_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // 1. Monthly Progress Chart (Last 30 Days visualization)
            item {
                MonthlyProgressChart(logs = logs)
            }

            // 2. Lifetime Aggregate Metric Cards Grid
            item {
                Text(
                    text = "Lifetime Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MilestoneCard(
                        title = "Weight Lifted",
                        value = "${(totalVolume ?: 0.0).toInt()} kg",
                        icon = Icons.Default.FitnessCenter,
                        tint = NeonEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    MilestoneCard(
                        title = "Distance Run",
                        value = "${String.format(Locale.getDefault(), "%.1f", totalDistance ?: 0.0)} km",
                        icon = Icons.Default.DirectionsRun,
                        tint = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MilestoneCard(
                        title = "Workout Time",
                        value = "${totalDuration ?: 0} min",
                        icon = Icons.Default.Timer,
                        tint = AmberGlow,
                        modifier = Modifier.weight(1f)
                    )
                    MilestoneCard(
                        title = "Calories Burned",
                        value = "${totalCalories ?: 0} kcal",
                        icon = Icons.Default.LocalFireDepartment,
                        tint = VividFlame,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Completed Sessions History List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed Workouts (${logs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (logs.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Box(
                            modifier = Modifier.padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No workouts recorded yet. Start training or tap Log Session above!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(logs) { log ->
                    DetailedLogCard(
                        log = log,
                        onDelete = { viewModel.deleteWorkoutLog(log.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DetailedLogCard(
    log: WorkoutLogEntity,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("EEEE, MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(log.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_log_card_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$dateStr • ${log.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Log",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row: Duration, Weight Volume, Distance Run, Calories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text("Duration", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${log.durationMinutes} min", style = MaterialTheme.typography.bodyMedium, color = ElectricCyan, fontWeight = FontWeight.SemiBold)
                }
                if (log.totalVolumeKg > 0) {
                    Column {
                        Text("Weight Lifted", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${log.totalVolumeKg.toInt()} kg", style = MaterialTheme.typography.bodyMedium, color = NeonEmerald, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (log.distanceKm > 0) {
                    Column {
                        Text("Distance", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("${String.format(Locale.getDefault(), "%.1f", log.distanceKm)} km", style = MaterialTheme.typography.bodyMedium, color = ElectricCyan, fontWeight = FontWeight.SemiBold)
                    }
                }
                Column {
                    Text("Burned", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text("${log.totalCalories} kcal", style = MaterialTheme.typography.bodyMedium, color = VividFlame, fontWeight = FontWeight.SemiBold)
                }
            }

            if (log.exercisesSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = log.exercisesSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📝 Notes: ${log.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
