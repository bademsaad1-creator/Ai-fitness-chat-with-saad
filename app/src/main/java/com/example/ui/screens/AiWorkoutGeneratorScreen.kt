package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.GeneratedWorkoutRoutine
import com.example.ui.components.FormTipDialog
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ChipBackground
import com.example.ui.theme.Crimson
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
import com.example.ui.viewmodel.GeneratorState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiWorkoutGeneratorScreen(
    viewModel: FitnessViewModel,
    onBack: () -> Unit,
    onStartWorkout: (GeneratedWorkoutRoutine) -> Unit,
    modifier: Modifier = Modifier
) {
    val generatorState by viewModel.generatorState.collectAsState()
    val formTipState by viewModel.formTipState.collectAsState()

    // Form inputs
    var selectedGoal by remember { mutableStateOf("Hypertrophy / Muscle Building") }
    val selectedMuscles = remember { mutableStateListOf("Chest", "Triceps") }
    val selectedEquipment = remember { mutableStateListOf("Dumbbells", "Barbell") }
    var durationMinutes by remember { mutableIntStateOf(45) }
    var selectedLevel by remember { mutableStateOf("Intermediate") }
    var extraNotes by remember { mutableStateOf("") }
    var routineSavedNotification by remember { mutableStateOf(false) }

    val goals = listOf(
        "Hypertrophy / Muscle Building",
        "Fat Loss & HIIT Conditioning",
        "Raw Strength & Power",
        "Calisthenics & Bodyweight",
        "Mobility & Joint Health"
    )

    val muscles = listOf("Full Body", "Chest", "Back", "Legs & Glutes", "Shoulders", "Biceps", "Triceps", "Core & Abs")
    val equipmentList = listOf("Dumbbells", "Barbell", "Cables", "Kettlebell", "Resistance Bands", "Bodyweight Only")
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val durations = listOf(20, 30, 45, 60)

    FormTipDialog(
        state = formTipState,
        onDismiss = { viewModel.dismissFormTipDialog() }
    )

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
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("generator_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Workout Generator",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Powered by Gemini 3.8 Flash",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan
                        )
                    }
                }
            }

            // Results View or Generator Form
            when (val state = generatorState) {
                is GeneratorState.Success -> {
                    item {
                        GeneratedRoutineView(
                            routine = state.routine,
                            routineSaved = routineSavedNotification,
                            onSave = {
                                viewModel.saveGeneratedRoutine(state.routine)
                                routineSavedNotification = true
                            },
                            onStart = {
                                onStartWorkout(state.routine)
                            },
                            onGenerateAnother = {
                                routineSavedNotification = false
                                viewModel.resetGeneratorState()
                            },
                            onInspectForm = { exName ->
                                viewModel.inspectExerciseForm(exName)
                            }
                        )
                    }
                }

                is GeneratorState.Generating -> {
                    item {
                        GeneratingLoaderCard()
                    }
                }

                is GeneratorState.Error -> {
                    item {
                        ErrorCard(
                            message = state.message,
                            onRetry = {
                                viewModel.generateRoutine(
                                    goal = selectedGoal,
                                    targetMuscles = selectedMuscles,
                                    equipment = selectedEquipment,
                                    durationMinutes = durationMinutes,
                                    fitnessLevel = selectedLevel,
                                    extraNotes = extraNotes
                                )
                            }
                        )
                    }
                }

                is GeneratorState.Idle -> {
                    // Step 1: Goal
                    item {
                        SectionHeader(title = "1. Training Goal")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            goals.forEach { goal ->
                                SelectableChip(
                                    label = goal,
                                    selected = selectedGoal == goal,
                                    onClick = { selectedGoal = goal }
                                )
                            }
                        }
                    }

                    // Step 2: Muscle Focus
                    item {
                        SectionHeader(title = "2. Target Muscles")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            muscles.forEach { muscle ->
                                val isSelected = selectedMuscles.contains(muscle)
                                SelectableChip(
                                    label = muscle,
                                    selected = isSelected,
                                    onClick = {
                                        if (muscle == "Full Body") {
                                            selectedMuscles.clear()
                                            selectedMuscles.add("Full Body")
                                        } else {
                                            selectedMuscles.remove("Full Body")
                                            if (isSelected) selectedMuscles.remove(muscle)
                                            else selectedMuscles.add(muscle)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Step 3: Equipment Available
                    item {
                        SectionHeader(title = "3. Available Equipment")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            equipmentList.forEach { equip ->
                                val isSelected = selectedEquipment.contains(equip)
                                SelectableChip(
                                    label = equip,
                                    selected = isSelected,
                                    onClick = {
                                        if (equip == "Bodyweight Only") {
                                            selectedEquipment.clear()
                                            selectedEquipment.add("Bodyweight Only")
                                        } else {
                                            selectedEquipment.remove("Bodyweight Only")
                                            if (isSelected) selectedEquipment.remove(equip)
                                            else selectedEquipment.add(equip)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Step 4: Duration & Level
                    item {
                        SectionHeader(title = "4. Duration & Experience")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            durations.forEach { d ->
                                SelectableChip(
                                    label = "$d min",
                                    selected = durationMinutes == d,
                                    onClick = { durationMinutes = d },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            levels.forEach { level ->
                                SelectableChip(
                                    label = level,
                                    selected = selectedLevel == level,
                                    onClick = { selectedLevel = level },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Step 5: Custom Preferences / Constraints
                    item {
                        SectionHeader(title = "5. Additional Notes (Optional)")
                        OutlinedTextField(
                            value = extraNotes,
                            onValueChange = { extraNotes = it },
                            placeholder = {
                                Text(
                                    "e.g. Include drop sets, lower back friendly, tempo 3-1-1...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generator_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonEmerald,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )
                    }

                    // Generate Action Button
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.generateRoutine(
                                    goal = selectedGoal,
                                    targetMuscles = if (selectedMuscles.isEmpty()) listOf("Full Body") else selectedMuscles,
                                    equipment = if (selectedEquipment.isEmpty()) listOf("Bodyweight") else selectedEquipment,
                                    durationMinutes = durationMinutes,
                                    fitnessLevel = selectedLevel,
                                    extraNotes = extraNotes
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_workout_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Custom Workout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("chip_$label"),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) NeonEmerald.copy(alpha = 0.2f) else SurfaceDark,
        border = BorderStroke(1.dp, if (selected) NeonEmerald else CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) NeonEmerald else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun GeneratingLoaderCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = NeonEmerald,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Crafting Your Perfect Workout",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gemini is analyzing muscle symmetry, fatigue curves, optimal exercise order, and tailored rest intervals...",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, Crimson.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Crimson, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Generation Error",
                style = MaterialTheme.typography.titleMedium,
                color = Crimson,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian)
            ) {
                Text("Retry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GeneratedRoutineView(
    routine: GeneratedWorkoutRoutine,
    routineSaved: Boolean,
    onSave: () -> Unit,
    onStart: () -> Unit,
    onGenerateAnother: () -> Unit,
    onInspectForm: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NeonEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Obsidian,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CUSTOM AI ROUTINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = routine.routineTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = routine.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricCyan.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${routine.estimatedDurationMinutes} mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGlow.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = routine.difficultyLevel,
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGlow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Warmup Note
        if (routine.warmupNotes.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Dynamic Warm-Up", style = MaterialTheme.typography.labelLarge, color = VividFlame)
                        Text(routine.warmupNotes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        // Exercises List
        Text(
            text = "Exercise Sequence (${routine.exercises.size})",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        routine.exercises.forEachIndexed { idx, ex ->
            ExerciseDetailItem(index = idx + 1, exercise = ex, onInspect = { onInspectForm(ex.name) })
        }

        // Cooldown
        if (routine.cooldownNotes.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🧘", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Cooldown & Stretching", style = MaterialTheme.typography.labelLarge, color = ElectricCyan)
                        Text(routine.cooldownNotes, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        // Action Buttons: Start Workout & Save
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_generated_workout_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Workout Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onSave,
                enabled = !routineSaved,
                modifier = Modifier
                    .weight(1f)
                    .testTag("save_routine_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (routineSaved) NeonEmeraldDark else SurfaceCardHighlight,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(if (routineSaved) Icons.Default.Check else Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (routineSaved) "Saved to Library" else "Save Routine", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onGenerateAnother,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TextSecondary),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Text("Customize More", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ExerciseDetailItem(
    index: Int,
    exercise: ExerciseItem,
    onInspect: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardHighlight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.clickable(onClick = onInspect)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(14.dp)
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

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${exercise.sets} Sets × ${exercise.reps} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•",
                    color = TextMuted
                )
                Text(
                    text = "${exercise.restSeconds}s Rest",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "•",
                    color = TextMuted
                )
                Text(
                    text = exercise.equipment,
                    style = MaterialTheme.typography.bodySmall,
                    color = AmberGlow
                )
            }

            if (exercise.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = exercise.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
