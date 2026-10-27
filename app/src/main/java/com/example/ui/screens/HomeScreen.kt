package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedRoutineEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.FitnessJsonHelper
import com.example.ui.components.LogWorkoutDialog
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldDark
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHighlight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPulse
import com.example.ui.theme.VividFlame
import com.example.ui.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: FitnessViewModel,
    onNavigateToGenerator: () -> Unit,
    onNavigateToCoach: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val savedRoutines by viewModel.savedRoutines.collectAsState()
    val workoutLogs by viewModel.workoutLogs.collectAsState()
    val totalWorkouts by viewModel.totalWorkoutsCount.collectAsState()
    val totalCalories by viewModel.totalCaloriesBurned.collectAsState()
    val totalDistance by viewModel.totalDistanceRun.collectAsState()
    val totalVolume by viewModel.totalVolumeLifted.collectAsState()

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header: Profile & Streak
            item {
                HomeHeader(
                    userName = userProfile?.name ?: "Athlete",
                    streakDays = userProfile?.streakDays ?: 1,
                    primaryGoal = userProfile?.primaryGoal ?: "Cardio Health & Muscle Building",
                    onLogClick = { showLogDialog = true }
                )
            }

            // Quick Stats Metric Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Workouts",
                        value = "$totalWorkouts",
                        icon = Icons.Default.FitnessCenter,
                        tint = NeonEmerald
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Volume",
                        value = "${(totalVolume ?: 0.0).toInt()}kg",
                        icon = Icons.Default.FitnessCenter,
                        tint = ElectricCyan
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Distance",
                        value = "${String.format(Locale.getDefault(), "%.1f", totalDistance ?: 0.0)}km",
                        icon = Icons.Default.DirectionsRun,
                        tint = AmberGlow
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Calories",
                        value = "${totalCalories ?: 0}",
                        icon = Icons.Default.LocalFireDepartment,
                        tint = VividFlame
                    )
                }
            }

            // Featured Program Banner: Beginner Cardio & Muscle (3x/week • 45m)
            item {
                BeginnerProgramCard(
                    routines = savedRoutines.filter { it.title.contains("Beginner Hybrid") },
                    onStartRoutine = { routine ->
                        viewModel.startWorkoutFromRoutine(routine)
                    }
                )
            }

            // AI Features Action Banner
            item {
                AiFeaturedBanner(
                    onGenerateClick = onNavigateToGenerator,
                    onCoachClick = onNavigateToCoach,
                    onNutritionClick = onNavigateToNutrition
                )
            }

            // Saved Routines Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workout Routines",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+ Generate New",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonEmerald,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToGenerator)
                            .testTag("home_generate_routine_text")
                    )
                }
            }

            if (savedRoutines.isEmpty()) {
                item {
                    EmptyRoutinesCard(onGenerateClick = onNavigateToGenerator)
                }
            } else {
                items(savedRoutines) { routine ->
                    RoutineCard(
                        routine = routine,
                        onStartWorkout = {
                            viewModel.startWorkoutFromRoutine(routine)
                        },
                        onDelete = {
                            viewModel.deleteRoutine(routine.id)
                        }
                    )
                }
            }

            // Recent Workout Activity Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress & Recent Sessions",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View Analytics (${workoutLogs.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricCyan,
                        modifier = Modifier
                            .clickable(onClick = onNavigateToHistory)
                            .testTag("home_view_all_history_btn")
                    )
                }
            }

            if (workoutLogs.isEmpty()) {
                item {
                    EmptyWorkoutLogsCard()
                }
            } else {
                items(workoutLogs.take(4)) { log ->
                    RecentWorkoutLogItem(log = log)
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Floating Action Button to Generate Workout
        FloatingActionButton(
            onClick = onNavigateToGenerator,
            containerColor = NeonEmerald,
            contentColor = Obsidian,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_generate_workout")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Workout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Workout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BeginnerProgramCard(
    routines: List<SavedRoutineEntity>,
    onStartRoutine: (SavedRoutineEntity) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("beginner_program_card"),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonEmerald.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("FEATURED 3-DAY PROGRAM", style = MaterialTheme.typography.labelSmall, color = NeonEmerald, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricCyan.copy(alpha = 0.12f)
                ) {
                    Text("3x / Week • 45m", style = MaterialTheme.typography.labelSmall, color = ElectricCyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Beginner Cardio Health & Muscle Building",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Designed for home training with dumbbells and resistance bands. Elevates cardiovascular fitness while building lean muscle with compound resistance lifts.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Day Session Cards
            if (routines.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    routines.forEachIndexed { idx, routine ->
                        val dayLabel = "Day ${idx + 1}"
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onStartRoutine(routine) }
                                .testTag("start_beginner_day_${idx + 1}"),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(NeonEmerald.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(dayLabel, style = MaterialTheme.typography.labelSmall, color = NeonEmerald, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = routine.title.replace("Beginner Hybrid: ", ""),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "45 mins • Dumbbells & Bands",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onStartRoutine(routine) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    streakDays: Int,
    primaryGoal: String,
    onLogClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome back, $userName",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Goal: $primaryGoal",
                style = MaterialTheme.typography.bodyMedium,
                color = ElectricCyan
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onLogClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = NeonEmerald),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("home_quick_log_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Streak Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCardHighlight,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${streakDays}d",
                        style = MaterialTheme.typography.labelMedium,
                        color = VividFlame,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
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
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AiFeaturedBanner(
    onGenerateClick: () -> Unit,
    onCoachClick: () -> Unit,
    onNutritionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_featured_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0F3A30),
                            Color(0xFF132F4C)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(NeonEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Obsidian,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "FitPulse AI Intelligence",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generate tailored resistance routines, analyze exercise form, and calculate your optimal macro balance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onGenerateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("banner_ai_routine_btn")
                    ) {
                        Text("New Workout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onCoachClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("banner_ai_coach_btn")
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Coach", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onNutritionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("banner_ai_macros_btn")
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Macros", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: SavedRoutineEntity,
    onStartWorkout: () -> Unit,
    onDelete: () -> Unit
) {
    val exercises = FitnessJsonHelper.parseExercises(routine.exercisesJson)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.id}"),
        shape = RoundedCornerShape(18.dp),
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
                    if (routine.isCustomAiGenerated) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI GENERATED",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = routine.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete routine",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = routine.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoutineChip(label = "${routine.estimatedMinutes} min", color = ElectricCyan)
                RoutineChip(label = "${exercises.size} Exercises", color = AmberGlow)
                RoutineChip(label = routine.difficulty, color = VioletPulse)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start Workout Button
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_routine_${routine.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Workout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RoutineChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RecentWorkoutLogItem(log: WorkoutLogEntity) {
    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(log.timestamp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$dateStr • ${log.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (log.notes.isNotBlank()) {
                    Text(
                        text = "\"${log.notes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.totalCalories} kcal",
                    style = MaterialTheme.typography.titleSmall,
                    color = VividFlame,
                    fontWeight = FontWeight.Bold
                )
                if (log.totalVolumeKg > 0) {
                    Text(
                        text = "${log.totalVolumeKg.toInt()} kg vol",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonEmerald
                    )
                } else if (log.distanceKm > 0) {
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", log.distanceKm)} km run",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRoutinesCard(onGenerateClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = NeonEmerald,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No saved routines yet",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Let Gemini AI generate your custom tailored workout plan in seconds.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onGenerateClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Generate Plan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyWorkoutLogsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No workouts recorded yet. Start your first session above!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
