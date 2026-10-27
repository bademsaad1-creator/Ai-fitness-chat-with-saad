package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val category: String, // Strength, Hypertrophy, HIIT, Cardio, Mobility
    val durationMinutes: Int,
    val totalCalories: Int,
    val totalVolumeKg: Double,
    val distanceKm: Double = 0.0,
    val totalSetsCompleted: Int,
    val exercisesSummary: String, // High level summary or exercise list
    val detailsJson: String = "", // Detailed sets/reps json
    val notes: String = ""
)

@Entity(tableName = "saved_routines")
data class SavedRoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val targetMuscles: String,
    val equipment: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val exercisesJson: String,
    val isCustomAiGenerated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Athlete",
    val fitnessLevel: String = "Intermediate", // Beginner, Intermediate, Advanced
    val primaryGoal: String = "Muscle Building", // Muscle Building, Fat Loss, Strength & Power, Endurance
    val currentWeightKg: Float = 75.0f,
    val targetWeightKg: Float = 78.0f,
    val heightCm: Float = 178.0f,
    val dailyCalorieTarget: Int = 2400,
    val dailyProteinTargetGrams: Int = 160,
    val streakDays: Int = 3,
    val lastWorkoutTimestamp: Long = 0L
)
