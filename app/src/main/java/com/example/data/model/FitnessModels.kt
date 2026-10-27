package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExerciseItem(
    val name: String,
    val muscleTarget: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String = "",
    val equipment: String = "Bodyweight"
)

@JsonClass(generateAdapter = true)
data class GeneratedWorkoutRoutine(
    val routineTitle: String,
    val overview: String,
    val estimatedDurationMinutes: Int,
    val targetMuscles: String,
    val difficultyLevel: String,
    val warmupNotes: String,
    val exercises: List<ExerciseItem>,
    val cooldownNotes: String
)

@JsonClass(generateAdapter = true)
data class WorkoutSetRecord(
    val setNumber: Int,
    val weightKg: Float,
    val reps: Int,
    val isCompleted: Boolean = false
)

@JsonClass(generateAdapter = true)
data class LoggedExercise(
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<WorkoutSetRecord>
)

@JsonClass(generateAdapter = true)
data class MealPlanItem(
    val mealType: String, // Breakfast, Lunch, Snack, Dinner
    val title: String,
    val description: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatsGrams: Int
)

@JsonClass(generateAdapter = true)
data class NutritionPlan(
    val dailyCalorieTarget: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatsGrams: Int,
    val advice: String,
    val meals: List<MealPlanItem>
)

@JsonClass(generateAdapter = true)
data class FormAnalysisTip(
    val exerciseName: String,
    val primaryMuscles: String,
    val setupCues: List<String>,
    val movementCues: List<String>,
    val commonMistakes: List<String>,
    val breathingTip: String
)
