package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividFlame

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, duration: Int, volume: Double, distance: Double, calories: Int, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("Full Body Resistance & Cardio") }
    var category by remember { mutableStateOf("Strength") }
    var durationText by remember { mutableStateOf("45") }
    var volumeText by remember { mutableStateOf("1500") }
    var distanceText by remember { mutableStateOf("0.0") }
    var caloriesText by remember { mutableStateOf("360") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Strength", "Running", "Cardio", "HIIT", "Mobility")
    val quickTitles = listOf(
        "Full Body Dumbbell & Band",
        "5K Cardio Run",
        "Cardio Intervals & Circuit",
        "Hypertrophy Session",
        "Recovery Run & Stretch"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("log_workout_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Log Completed Workout",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Quick Title Suggestions
                Text("Workout Title", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. 5K Run, Dumbbell Push Day...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_workout_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTitles.forEach { suggestion ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    title = suggestion
                                    if (suggestion.contains("Run")) {
                                        category = "Running"
                                        distanceText = "5.0"
                                        volumeText = "0"
                                    } else {
                                        category = "Strength"
                                        distanceText = "0.0"
                                        volumeText = "1650"
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Category Selector
                Text("Category", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { category = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonEmerald.copy(alpha = 0.2f) else SurfaceCard,
                            border = BorderStroke(1.dp, if (isSelected) NeonEmerald else CardBorder)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) NeonEmerald else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Metrics Grid: Duration & Calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Duration (min)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = durationText,
                            onValueChange = {
                                durationText = it
                                val d = it.toIntOrNull() ?: 0
                                caloriesText = (d * 8).toString()
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonEmerald,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.testTag("log_workout_duration_input")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = VividFlame, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Calories (kcal)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = caloriesText,
                            onValueChange = { caloriesText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonEmerald,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.testTag("log_workout_calories_input")
                        )
                    }
                }

                // Metrics Grid: Weight Lifted & Distance Run
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Weight Lifted (kg)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = volumeText,
                            onValueChange = { volumeText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonEmerald,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.testTag("log_workout_volume_input")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Distance (km)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = distanceText,
                            onValueChange = { distanceText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonEmerald,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.testTag("log_workout_distance_input")
                        )
                    }
                }

                // Notes
                Text("Session Notes (Optional)", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("e.g. Felt great on intervals, increased dumbbell load...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 2
                )

                // Save Button
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        val duration = durationText.toIntOrNull() ?: 45
                        val volume = volumeText.toDoubleOrNull() ?: 0.0
                        val distance = distanceText.toDoubleOrNull() ?: 0.0
                        val calories = caloriesText.toIntOrNull() ?: (duration * 8)
                        onSave(
                            if (title.isBlank()) "Workout Session" else title,
                            category,
                            duration,
                            volume,
                            distance,
                            calories,
                            notes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_manual_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Progress Log", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
