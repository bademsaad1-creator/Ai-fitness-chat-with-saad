package com.example.data.repository

import com.example.data.ai.GeminiFitnessService
import com.example.data.local.FitnessDao
import com.example.data.local.SavedRoutineEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.ExerciseItem
import com.example.data.model.FitnessJsonHelper
import com.example.data.model.FormAnalysisTip
import com.example.data.model.GeneratedWorkoutRoutine
import com.example.data.model.NutritionPlan
import kotlinx.coroutines.flow.Flow

class FitnessRepository(
    private val dao: FitnessDao,
    private val aiService: GeminiFitnessService
) {
    // Flows from Room
    val allWorkoutLogs: Flow<List<WorkoutLogEntity>> = dao.getAllWorkoutLogs()
    val allSavedRoutines: Flow<List<SavedRoutineEntity>> = dao.getAllSavedRoutines()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val totalWorkoutsCount: Flow<Int> = dao.getTotalWorkoutsCount()
    val totalCaloriesBurned: Flow<Int?> = dao.getTotalCaloriesBurned()
    val totalDistanceRun: Flow<Double?> = dao.getTotalDistanceRun()
    val totalVolumeLifted: Flow<Double?> = dao.getTotalVolumeLifted()
    val totalDurationMinutes: Flow<Int?> = dao.getTotalDurationMinutes()

    fun getLogsSince(sinceTimestamp: Long): Flow<List<WorkoutLogEntity>> = dao.getLogsAscendingSince(sinceTimestamp)

    // Local DB operations
    suspend fun logWorkout(
        title: String,
        category: String,
        durationMinutes: Int,
        totalCalories: Int,
        totalVolumeKg: Double,
        distanceKm: Double = 0.0,
        totalSetsCompleted: Int,
        exercisesSummary: String,
        detailsJson: String = "",
        notes: String = ""
    ): Long {
        val id = dao.insertWorkoutLog(
            WorkoutLogEntity(
                title = title,
                category = category,
                durationMinutes = durationMinutes,
                totalCalories = totalCalories,
                totalVolumeKg = totalVolumeKg,
                distanceKm = distanceKm,
                totalSetsCompleted = totalSetsCompleted,
                exercisesSummary = exercisesSummary,
                detailsJson = detailsJson,
                notes = notes
            )
        )
        return id
    }

    suspend fun saveRoutine(
        title: String,
        description: String,
        targetMuscles: String,
        equipment: String,
        difficulty: String,
        estimatedMinutes: Int,
        exercises: List<ExerciseItem>,
        isCustomAiGenerated: Boolean
    ): Long {
        return dao.insertSavedRoutine(
            SavedRoutineEntity(
                title = title,
                description = description,
                targetMuscles = targetMuscles,
                equipment = equipment,
                difficulty = difficulty,
                estimatedMinutes = estimatedMinutes,
                exercisesJson = FitnessJsonHelper.serializeExercises(exercises),
                isCustomAiGenerated = isCustomAiGenerated
            )
        )
    }

    suspend fun deleteRoutine(id: Long) = dao.deleteSavedRoutine(id)
    suspend fun deleteWorkoutLog(id: Long) = dao.deleteWorkoutLog(id)

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    // AI operations
    suspend fun generateWorkoutRoutine(
        goal: String,
        targetMuscles: List<String>,
        equipment: List<String>,
        durationMinutes: Int,
        fitnessLevel: String,
        extraNotes: String
    ): Result<GeneratedWorkoutRoutine> {
        return aiService.generateWorkoutRoutine(
            goal = goal,
            targetMuscles = targetMuscles,
            equipment = equipment,
            durationMinutes = durationMinutes,
            fitnessLevel = fitnessLevel,
            extraNotes = extraNotes
        )
    }

    suspend fun generateNutritionPlan(
        goal: String,
        weightKg: Float,
        heightCm: Float,
        activityLevel: String,
        dietaryPreference: String
    ): Result<NutritionPlan> {
        return aiService.generateNutritionPlan(
            goal = goal,
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = activityLevel,
            dietaryPreference = dietaryPreference
        )
    }

    suspend fun analyzeExerciseForm(exerciseName: String): Result<FormAnalysisTip> {
        return aiService.analyzeExerciseForm(exerciseName)
    }

    suspend fun askFitnessCoach(
        message: String,
        history: List<Pair<String, String>>,
        athleteContext: String
    ): Result<String> {
        return aiService.askFitnessCoach(message, history, athleteContext)
    }

    suspend fun askFitnessCoachStream(
        message: String,
        category: String,
        history: List<Pair<String, String>>,
        athleteContext: String,
        onChunkReceived: (String) -> Unit
    ): Result<String> {
        return aiService.askFitnessCoachStream(message, category, history, athleteContext, onChunkReceived)
    }
}
