package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutSetRecord
import com.example.ui.components.FormTipDialog
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.Crimson
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHighlight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividFlame
import com.example.ui.viewmodel.ActiveWorkoutSession
import com.example.ui.viewmodel.FitnessViewModel
import kotlinx.coroutines.delay

@Composable
fun ActiveWorkoutScreen(
    viewModel: FitnessViewModel,
    session: ActiveWorkoutSession,
    modifier: Modifier = Modifier
) {
    val formTipState by viewModel.formTipState.collectAsState()

    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var workoutNotes by remember { mutableStateOf("") }

    // Live Elapsed Seconds Timer
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    LaunchedEffect(session.startTimestamp) {
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - session.startTimestamp) / 1000L
            delay(1000L)
        }
    }

    val elapsedMinutes = elapsedSeconds / 60
    val elapsedSecondsRemainder = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", elapsedMinutes, elapsedSecondsRemainder)

    val currentEx = session.exercises.getOrNull(session.currentExerciseIndex) ?: session.exercises.first()
    val setsForCurrent = session.completedSetsByExercise[session.currentExerciseIndex] ?: emptyList()

    FormTipDialog(
        state = formTipState,
        onDismiss = { viewModel.dismissFormTipDialog() }
    )

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Quit Workout?", color = TextPrimary) },
            text = { Text("Your progress in this active session will be discarded.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelActiveWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson)
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("Resume")
                }
            },
            containerColor = SurfaceDark
        )
    }

    if (showFinishDialog) {
        var totalSets = 0
        var totalVol = 0.0
        session.completedSetsByExercise.values.flatten().forEach { s ->
            if (s.isCompleted) {
                totalSets++
                totalVol += (s.weightKg * s.reps)
            }
        }
        val estimatedKcal = maxOf(20, (elapsedMinutes * 8.0).toInt())

        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AmberGlow,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Workout Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonEmerald,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column {
                    Text(
                        text = "Great job crushing your session!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Duration", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(timeFormatted, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Volume", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("${totalVol.toInt()} kg", style = MaterialTheme.typography.titleMedium, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Sets", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("$totalSets", style = MaterialTheme.typography.titleMedium, color = AmberGlow, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Burn", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("$estimatedKcal kcal", style = MaterialTheme.typography.titleMedium, color = VividFlame, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = workoutNotes,
                        onValueChange = { workoutNotes = it },
                        placeholder = { Text("Workout notes (e.g. felt strong, increased weight)...", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("workout_finish_notes_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout(workoutNotes)
                    },
                    modifier = Modifier.testTag("confirm_finish_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian)
                ) {
                    Text("Save & Log Workout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showFinishDialog = false }) {
                    Text("Keep Training", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Workout", tint = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("finish_workout_top_btn")
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Exercise Selection Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(session.exercises) { idx, ex ->
                    val isSelected = idx == session.currentExerciseIndex
                    val sets = session.completedSetsByExercise[idx] ?: emptyList()
                    val allSetsDone = sets.isNotEmpty() && sets.all { it.isCompleted }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.setCurrentExerciseIndex(idx) }
                            .testTag("exercise_tab_$idx"),
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isSelected -> NeonEmerald.copy(alpha = 0.2f)
                            allSetsDone -> SurfaceCardHighlight
                            else -> SurfaceDark
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isSelected -> NeonEmerald
                                allSetsDone -> NeonEmerald.copy(alpha = 0.5f)
                                else -> CardBorder
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (allSetsDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "${idx + 1}. ${ex.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) NeonEmerald else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Current Exercise Details & Sets Table
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Exercise Header Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentEx.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "${currentEx.muscleTarget} • ${currentEx.equipment}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElectricCyan
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = NeonEmerald.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .clickable { viewModel.inspectExerciseForm(currentEx.name) }
                                        .testTag("active_form_cues_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = NeonEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Form Cues",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (currentEx.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 Tip: ${currentEx.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                // Table Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SET", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(36.dp))
                        Text("KG", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(90.dp))
                        Text("REPS", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(90.dp))
                        Text("DONE", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(48.dp))
                    }
                }

                // Set Rows
                itemsIndexed(setsForCurrent) { setIdx, setRecord ->
                    SetRowItem(
                        setIndex = setIdx,
                        setRecord = setRecord,
                        onWeightChange = { newW ->
                            viewModel.updateSetRecord(
                                exerciseIndex = session.currentExerciseIndex,
                                setIndex = setIdx,
                                weightKg = maxOf(0f, newW),
                                reps = setRecord.reps,
                                completed = setRecord.isCompleted
                            )
                        },
                        onRepsChange = { newR ->
                            viewModel.updateSetRecord(
                                exerciseIndex = session.currentExerciseIndex,
                                setIndex = setIdx,
                                weightKg = setRecord.weightKg,
                                reps = maxOf(1, newR),
                                completed = setRecord.isCompleted
                            )
                        },
                        onToggleCompleted = { completed ->
                            viewModel.updateSetRecord(
                                exerciseIndex = session.currentExerciseIndex,
                                setIndex = setIdx,
                                weightKg = setRecord.weightKg,
                                reps = setRecord.reps,
                                completed = completed
                            )
                        }
                    )
                }

                // Navigation between exercises
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (session.currentExerciseIndex > 0) {
                            OutlinedButton(
                                onClick = { viewModel.setCurrentExerciseIndex(session.currentExerciseIndex - 1) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = BorderStroke(1.dp, CardBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Previous")
                            }
                        }

                        if (session.currentExerciseIndex < session.exercises.size - 1) {
                            Button(
                                onClick = { viewModel.setCurrentExerciseIndex(session.currentExerciseIndex + 1) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("next_exercise_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Next Exercise", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { showFinishDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("finish_workout_bottom_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Finish Workout", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Animated Rest Timer Banner (Floats at bottom when resting)
        AnimatedVisibility(
            visible = session.isResting,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceCardHighlight,
                border = BorderStroke(2.dp, NeonEmerald),
                shadowElevation = 8.dp,
                modifier = Modifier.testTag("rest_timer_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Rest Period",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${session.restSecondsRemaining}s",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NeonEmerald,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.addRestTime(15) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = ElectricCyan),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text("+15s", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.skipRestTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("skip_rest_timer_btn")
                        ) {
                            Text("Skip", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetRowItem(
    setIndex: Int,
    setRecord: WorkoutSetRecord,
    onWeightChange: (Float) -> Unit,
    onRepsChange: (Int) -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("set_row_$setIndex"),
        shape = RoundedCornerShape(12.dp),
        color = if (setRecord.isCompleted) NeonEmerald.copy(alpha = 0.10f) else SurfaceDark,
        border = BorderStroke(1.dp, if (setRecord.isCompleted) NeonEmerald.copy(alpha = 0.4f) else CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set Number
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (setRecord.isCompleted) NeonEmerald else SurfaceCardHighlight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${setIndex + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (setRecord.isCompleted) Obsidian else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Weight Stepper
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onWeightChange(setRecord.weightKg - 2.5f) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Weight", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${setRecord.weightKg}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onWeightChange(setRecord.weightKg + 2.5f) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Weight", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Reps Stepper
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onRepsChange(setRecord.reps - 1) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Reps", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${setRecord.reps}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onRepsChange(setRecord.reps + 1) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Reps", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Checkbox for Completed
            Checkbox(
                checked = setRecord.isCompleted,
                onCheckedChange = onToggleCompleted,
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonEmerald,
                    checkmarkColor = Obsidian,
                    uncheckedColor = CardBorder
                ),
                modifier = Modifier.testTag("checkbox_set_$setIndex")
            )
        }
    }
}
