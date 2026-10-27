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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealPlanItem
import com.example.data.model.NutritionPlan
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
import com.example.ui.viewmodel.FitnessViewModel
import com.example.ui.viewmodel.NutritionState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NutritionScreen(
    viewModel: FitnessViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutritionState by viewModel.nutritionState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedPreference by remember { mutableStateOf("High Protein & Whole Foods") }
    val preferences = listOf(
        "High Protein & Whole Foods",
        "Lean Bulking Focus",
        "Fat Shred & Carb Cycling",
        "Mediterranean Diet",
        "Plant-Based / Vegetarian"
    )

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
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("nutrition_back_btn")
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
                                text = "AI Nutrition & Macros",
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
                            text = "Precision fueling for your fitness goals",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan
                        )
                    }
                }
            }

            // Athlete Biometric Overview Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${userProfile?.currentWeightKg ?: 75f} kg • ${userProfile?.heightCm?.toInt() ?: 178} cm",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target: ${userProfile?.targetWeightKg ?: 80f} kg (${userProfile?.primaryGoal ?: "Hypertrophy"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonEmerald
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCardHighlight
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${userProfile?.dailyCalorieTarget ?: 2400}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = VividFlame,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "kcal / day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Dietary Preference Selection
            item {
                Text(
                    text = "Dietary Style & Focus",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    preferences.forEach { pref ->
                        val isSelected = selectedPreference == pref
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPreference = pref }
                                .testTag("diet_pref_$pref"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NeonEmerald.copy(alpha = 0.2f) else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSelected) NeonEmerald else CardBorder)
                        ) {
                            Text(
                                text = pref,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) NeonEmerald else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.generateNutritionPlan(selectedPreference) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_nutrition_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Obsidian),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculate Macros & Plan with AI", fontWeight = FontWeight.Bold)
                }
            }

            // State Handling
            when (val state = nutritionState) {
                is NutritionState.Generating -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = NeonEmerald)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Calculating Metabolic Energy & Meals...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Gemini is balancing protein synthesis thresholds, leucine timing, and daily energy expenditure.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                is NutritionState.Error -> {
                    item {
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
                                Text(state.message, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                is NutritionState.Success -> {
                    val plan = state.plan

                    // Macro Breakdown Cards Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroCard(label = "Calories", value = "${plan.dailyCalorieTarget}", unit = "kcal", tint = VividFlame, modifier = Modifier.weight(1f))
                            MacroCard(label = "Protein", value = "${plan.proteinGrams}", unit = "g", tint = NeonEmerald, modifier = Modifier.weight(1f))
                            MacroCard(label = "Carbs", value = "${plan.carbsGrams}", unit = "g", tint = ElectricCyan, modifier = Modifier.weight(1f))
                            MacroCard(label = "Fats", value = "${plan.fatsGrams}", unit = "g", tint = AmberGlow, modifier = Modifier.weight(1f))
                        }
                    }

                    // Coach Rationale
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 Nutritional Rationale",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = AmberGlow
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = plan.advice,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Meals
                    item {
                        Text(
                            text = "Daily Meal Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    plan.meals.forEach { meal ->
                        item {
                            MealItemCard(meal = meal)
                        }
                    }
                }

                is NutritionState.Idle -> {
                    // Show helpful default guidance
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Ready to dial in your nutrition?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Select your preferred dietary style and tap the button above to generate a full macronutrient schedule.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun MacroCard(
    label: String,
    value: String,
    unit: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = tint, fontWeight = FontWeight.Black)
            Text(unit, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun MealItemCard(meal: MealPlanItem) {
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = meal.mealType.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${meal.calories} kcal",
                    style = MaterialTheme.typography.labelLarge,
                    color = VividFlame,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = meal.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meal.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🥩 ${meal.proteinGrams}g Protein",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonEmerald
                )
                Text(
                    text = "🍚 ${meal.carbsGrams}g Carbs",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan
                )
                Text(
                    text = "🥑 ${meal.fatsGrams}g Fat",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberGlow
                )
            }
        }
    }
}
