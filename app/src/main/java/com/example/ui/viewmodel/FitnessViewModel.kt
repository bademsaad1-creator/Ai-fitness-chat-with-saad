package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiFitnessService
import com.example.data.local.FitPulseDatabase
import com.example.data.local.SavedRoutineEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.ExerciseItem
import com.example.data.model.FitnessJsonHelper
import com.example.data.model.FormAnalysisTip
import com.example.data.model.GeneratedWorkoutRoutine
import com.example.data.model.LoggedExercise
import com.example.data.model.NutritionPlan
import com.example.data.model.WorkoutSetRecord
import com.example.data.repository.FitnessRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// UI States
sealed interface GeneratorState {
    data object Idle : GeneratorState
    data object Generating : GeneratorState
    data class Success(val routine: GeneratedWorkoutRoutine) : GeneratorState
    data class Error(val message: String) : GeneratorState
}

sealed interface NutritionState {
    data object Idle : NutritionState
    data object Generating : NutritionState
    data class Success(val plan: NutritionPlan) : NutritionState
    data class Error(val message: String) : NutritionState
}

sealed interface FormTipState {
    data object Idle : FormTipState
    data object Loading : FormTipState
    data class Success(val tip: FormAnalysisTip) : FormTipState
    data class Error(val message: String) : FormTipState
}

data class CoachMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val category: String = "General", // "Form", "Nutrition", "Motivation", "General"
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val suggestedFollowUps: List<String> = emptyList()
)

data class ActiveWorkoutSession(
    val title: String,
    val category: String,
    val exercises: List<ExerciseItem>,
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,
    val completedSetsByExercise: Map<Int, List<WorkoutSetRecord>> = emptyMap(),
    val startTimestamp: Long = System.currentTimeMillis(),
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val targetRestDuration: Int = 60,
    val isFinished: Boolean = false
)

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitnessRepository
    private val vibrator: Vibrator?

    init {
        val database = FitPulseDatabase.getDatabase(application)
        val aiService = GeminiFitnessService()
        repository = FitnessRepository(database.fitnessDao(), aiService)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Room DB StateFlows
    val workoutLogs: StateFlow<List<WorkoutLogEntity>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedRoutines: StateFlow<List<SavedRoutineEntity>> = repository.allSavedRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalWorkoutsCount: StateFlow<Int> = repository.totalWorkoutsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCaloriesBurned: StateFlow<Int?> = repository.totalCaloriesBurned
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDistanceRun: StateFlow<Double?> = repository.totalDistanceRun
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalVolumeLifted: StateFlow<Double?> = repository.totalVolumeLifted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDurationMinutes: StateFlow<Int?> = repository.totalDurationMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val monthlyLogs: StateFlow<List<WorkoutLogEntity>> = repository.getLogsSince(System.currentTimeMillis() - (86400000L * 30))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Workout Generator State
    private val _generatorState = MutableStateFlow<GeneratorState>(GeneratorState.Idle)
    val generatorState: StateFlow<GeneratorState> = _generatorState.asStateFlow()

    // Active Workout State
    private val _activeSession = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeSession: StateFlow<ActiveWorkoutSession?> = _activeSession.asStateFlow()

    private var restTimerJob: Job? = null

    // AI Coach Chat State
    private val _coachMessages = MutableStateFlow<List<CoachMessage>>(
        listOf(
            CoachMessage(
                isUser = false,
                text = "Welcome to FitPulse! I'm Coach Titan, your personal AI athletic performance and fitness advisor. Ask me anything about exercise form biomechanics, nutrition & macro fueling, or mental motivation!",
                category = "General",
                suggestedFollowUps = listOf(
                    "Check my squat form & knee angle",
                    "Optimal pre-workout meal 1 hr before lifting",
                    "How to stay consistent on low-energy days"
                )
            )
        )
    )
    val coachMessages: StateFlow<List<CoachMessage>> = _coachMessages.asStateFlow()

    private val _isCoachThinking = MutableStateFlow(false)
    val isCoachThinking: StateFlow<Boolean> = _isCoachThinking.asStateFlow()

    // Nutrition State
    private val _nutritionState = MutableStateFlow<NutritionState>(NutritionState.Idle)
    val nutritionState: StateFlow<NutritionState> = _nutritionState.asStateFlow()

    // Form Tip State
    private val _formTipState = MutableStateFlow<FormTipState>(FormTipState.Idle)
    val formTipState: StateFlow<FormTipState> = _formTipState.asStateFlow()

    // AI Routine Generator Action
    fun generateRoutine(
        goal: String,
        targetMuscles: List<String>,
        equipment: List<String>,
        durationMinutes: Int,
        fitnessLevel: String,
        extraNotes: String = ""
    ) {
        viewModelScope.launch {
            _generatorState.value = GeneratorState.Generating
            val result = repository.generateWorkoutRoutine(
                goal = goal,
                targetMuscles = targetMuscles,
                equipment = equipment,
                durationMinutes = durationMinutes,
                fitnessLevel = fitnessLevel,
                extraNotes = extraNotes
            )
            result.onSuccess { routine ->
                _generatorState.value = GeneratorState.Success(routine)
            }.onFailure { error ->
                _generatorState.value = GeneratorState.Error(error.localizedMessage ?: "Failed to generate workout")
            }
        }
    }

    fun resetGeneratorState() {
        _generatorState.value = GeneratorState.Idle
    }

    fun saveGeneratedRoutine(routine: GeneratedWorkoutRoutine) {
        viewModelScope.launch {
            repository.saveRoutine(
                title = routine.routineTitle,
                description = routine.overview,
                targetMuscles = routine.targetMuscles,
                equipment = routine.exercises.joinToString(", ") { it.equipment }.split(", ").distinct().joinToString(", "),
                difficulty = routine.difficultyLevel,
                estimatedMinutes = routine.estimatedDurationMinutes,
                exercises = routine.exercises,
                isCustomAiGenerated = true
            )
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }

    fun deleteWorkoutLog(logId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutLog(logId)
        }
    }

    // Active Workout Controller
    fun startWorkoutFromRoutine(routine: SavedRoutineEntity) {
        val exercises = FitnessJsonHelper.parseExercises(routine.exercisesJson)
        startWorkout(routine.title, routine.difficulty, exercises)
    }

    fun startWorkout(title: String, category: String, exercises: List<ExerciseItem>) {
        if (exercises.isEmpty()) return

        val initialSetsMap = exercises.indices.associateWith { exIdx ->
            val ex = exercises[exIdx]
            val parsedReps = ex.reps.split("-").lastOrNull()?.filter { it.isDigit() }?.toIntOrNull() ?: 10
            (1..ex.sets).map { setNum ->
                WorkoutSetRecord(
                    setNumber = setNum,
                    weightKg = 20.0f, // reasonable starter barbell/dumbbell weight
                    reps = parsedReps,
                    isCompleted = false
                )
            }
        }

        _activeSession.value = ActiveWorkoutSession(
            title = title,
            category = category,
            exercises = exercises,
            currentExerciseIndex = 0,
            completedSetsByExercise = initialSetsMap,
            startTimestamp = System.currentTimeMillis()
        )
    }

    fun updateSetRecord(exerciseIndex: Int, setIndex: Int, weightKg: Float, reps: Int, completed: Boolean) {
        val session = _activeSession.value ?: return
        val currentList = session.completedSetsByExercise[exerciseIndex]?.toMutableList() ?: return
        if (setIndex in currentList.indices) {
            currentList[setIndex] = currentList[setIndex].copy(
                weightKg = weightKg,
                reps = reps,
                isCompleted = completed
            )
            val updatedMap = session.completedSetsByExercise.toMutableMap()
            updatedMap[exerciseIndex] = currentList
            _activeSession.value = session.copy(completedSetsByExercise = updatedMap)

            // If set was just completed, trigger haptic and start rest timer!
            if (completed) {
                triggerHapticFeedback()
                val currentEx = session.exercises.getOrNull(exerciseIndex)
                val restSec = currentEx?.restSeconds ?: 60
                startRestTimer(restSec)
            }
        }
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        val session = _activeSession.value ?: return
        _activeSession.value = session.copy(isResting = true, restSecondsRemaining = seconds, targetRestDuration = seconds)

        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000L)
                remaining--
                val curr = _activeSession.value ?: break
                _activeSession.value = curr.copy(restSecondsRemaining = remaining)
            }
            // Timer finished
            triggerRestCompleteVibration()
            val curr = _activeSession.value
            if (curr != null) {
                _activeSession.value = curr.copy(isResting = false, restSecondsRemaining = 0)
            }
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        val session = _activeSession.value ?: return
        _activeSession.value = session.copy(isResting = false, restSecondsRemaining = 0)
    }

    fun addRestTime(seconds: Int) {
        val session = _activeSession.value ?: return
        val newTime = session.restSecondsRemaining + seconds
        _activeSession.value = session.copy(restSecondsRemaining = newTime)
    }

    fun setCurrentExerciseIndex(index: Int) {
        val session = _activeSession.value ?: return
        if (index in session.exercises.indices) {
            _activeSession.value = session.copy(currentExerciseIndex = index)
        }
    }

    fun finishWorkout(notes: String = "") {
        val session = _activeSession.value ?: return
        restTimerJob?.cancel()

        val elapsedMinutes = maxOf(1, ((System.currentTimeMillis() - session.startTimestamp) / 60000L).toInt())

        var totalVolume = 0.0
        var totalSetsCompleted = 0
        session.completedSetsByExercise.values.flatten().forEach { set ->
            if (set.isCompleted) {
                totalSetsCompleted++
                totalVolume += (set.weightKg * set.reps)
            }
        }

        // Calorie burn estimation ~7.5 kcal/min for resistance/HIIT training
        val estimatedCalories = (elapsedMinutes * 8.0).toInt()

        val summary = "${session.exercises.size} exercises: " + session.exercises.take(3).joinToString(", ") { it.name } +
                if (session.exercises.size > 3) " +${session.exercises.size - 3} more" else ""

        viewModelScope.launch {
            repository.logWorkout(
                title = session.title,
                category = session.category,
                durationMinutes = elapsedMinutes,
                totalCalories = estimatedCalories,
                totalVolumeKg = totalVolume,
                totalSetsCompleted = totalSetsCompleted,
                exercisesSummary = summary,
                notes = notes
            )
            // Update profile streak
            val currentProf = userProfile.value
            if (currentProf != null) {
                repository.updateUserProfile(
                    currentProf.copy(
                        streakDays = currentProf.streakDays + 1,
                        lastWorkoutTimestamp = System.currentTimeMillis()
                    )
                )
            }
            _activeSession.value = null
        }
    }

    fun cancelActiveWorkout() {
        restTimerJob?.cancel()
        _activeSession.value = null
    }

    fun logManualWorkout(
        title: String,
        category: String,
        durationMinutes: Int,
        volumeKg: Double,
        distanceKm: Double,
        calories: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val summary = when {
                volumeKg > 0 && distanceKm > 0 -> "Volume: ${volumeKg.toInt()}kg • Distance: ${String.format("%.1f", distanceKm)}km"
                volumeKg > 0 -> "Total Volume: ${volumeKg.toInt()} kg"
                distanceKm > 0 -> "Distance: ${String.format("%.1f", distanceKm)} km"
                else -> "Duration: $durationMinutes min"
            }
            val cal = if (calories > 0) calories else (durationMinutes * 7.5).toInt()
            repository.logWorkout(
                title = title,
                category = category,
                durationMinutes = durationMinutes,
                totalCalories = cal,
                totalVolumeKg = volumeKg,
                distanceKm = distanceKm,
                totalSetsCompleted = if (volumeKg > 0) 12 else 1,
                exercisesSummary = summary,
                notes = notes
            )
            val currentProf = userProfile.value
            if (currentProf != null) {
                repository.updateUserProfile(
                    currentProf.copy(
                        streakDays = currentProf.streakDays + 1,
                        lastWorkoutTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // AI Coach Chat
    fun sendCoachMessage(userText: String, categoryFocus: String = "General") {
        if (userText.isBlank()) return

        val userMsg = CoachMessage(isUser = true, text = userText, category = categoryFocus)
        val initialAssistantMsg = CoachMessage(
            isUser = false,
            text = "",
            category = categoryFocus,
            isStreaming = true
        )
        _coachMessages.value = _coachMessages.value + userMsg + initialAssistantMsg
        _isCoachThinking.value = true

        viewModelScope.launch {
            val history = _coachMessages.value.dropLast(2).map {
                (if (it.isUser) "user" else "coach") to it.text
            }
            val profile = userProfile.value
            val athleteContext = "Goal: ${profile?.primaryGoal ?: "Fitness"}, Level: ${profile?.fitnessLevel ?: "Intermediate"}, Weight: ${profile?.currentWeightKg ?: 75}kg"

            val followUps = when (categoryFocus) {
                "Exercise Form" -> listOf(
                    "What are common mistakes for this lift?",
                    "How should I breathe during this exercise?",
                    "Alternative exercise for this muscle group"
                )
                "Nutrition Tips" -> listOf(
                    "Best post-workout recovery snack",
                    "How much protein do I need per day?",
                    "Should I take creatine monohydrate?"
                )
                "Motivation" -> listOf(
                    "How to overcome workout burnout?",
                    "Mental cues to push through the last 2 reps",
                    "How to build discipline over motivation"
                )
                else -> listOf(
                    "Give me a form cue for this",
                    "Nutrition tip to support this",
                    "Motivate me for my next workout"
                )
            }

            val result = repository.askFitnessCoachStream(
                message = userText,
                category = categoryFocus,
                history = history,
                athleteContext = athleteContext,
                onChunkReceived = { currentStreamedText ->
                    val currentList = _coachMessages.value.toMutableList()
                    if (currentList.isNotEmpty() && !currentList.last().isUser) {
                        currentList[currentList.lastIndex] = currentList.last().copy(
                            text = currentStreamedText,
                            isStreaming = true
                        )
                        _coachMessages.value = currentList
                    }
                }
            )

            _isCoachThinking.value = false
            val currentList = _coachMessages.value.toMutableList()
            if (currentList.isNotEmpty() && !currentList.last().isUser) {
                result.onSuccess { finalText ->
                    currentList[currentList.lastIndex] = currentList.last().copy(
                        text = finalText,
                        isStreaming = false,
                        suggestedFollowUps = followUps
                    )
                    _coachMessages.value = currentList
                }.onFailure { err ->
                    currentList[currentList.lastIndex] = currentList.last().copy(
                        text = "Coach Titan encountered an issue: ${err.localizedMessage ?: "Connection error"}. Please check your connection and ask again!",
                        isStreaming = false
                    )
                    _coachMessages.value = currentList
                }
            }
        }
    }

    fun clearCoachChat() {
        _coachMessages.value = listOf(
            CoachMessage(
                isUser = false,
                text = "Chat cleared! How can I assist your training, nutrition, or mindset today?",
                category = "General",
                suggestedFollowUps = listOf(
                    "Form tips for dumbbell bench press",
                    "Healthy high-protein meal idea",
                    "Pre-workout mental pep talk"
                )
            )
        )
    }

    // AI Nutrition Planner
    fun generateNutritionPlan(dietaryPreference: String = "High Protein, Whole Foods") {
        viewModelScope.launch {
            _nutritionState.value = NutritionState.Generating
            val profile = userProfile.value
            val goal = profile?.primaryGoal ?: "Muscle Building & Strength"
            val weight = profile?.currentWeightKg ?: 75.0f
            val height = profile?.heightCm ?: 178.0f
            val activity = "4-5 workout sessions per week"

            val result = repository.generateNutritionPlan(goal, weight, height, activity, dietaryPreference)
            result.onSuccess { plan ->
                _nutritionState.value = NutritionState.Success(plan)
                // Optionally update profile calorie and protein targets
                if (profile != null) {
                    repository.updateUserProfile(
                        profile.copy(
                            dailyCalorieTarget = plan.dailyCalorieTarget,
                            dailyProteinTargetGrams = plan.proteinGrams
                        )
                    )
                }
            }.onFailure { error ->
                _nutritionState.value = NutritionState.Error(error.localizedMessage ?: "Failed to generate nutrition plan")
            }
        }
    }

    // AI Form Analysis
    fun inspectExerciseForm(exerciseName: String) {
        viewModelScope.launch {
            _formTipState.value = FormTipState.Loading
            val result = repository.analyzeExerciseForm(exerciseName)
            result.onSuccess { tip ->
                _formTipState.value = FormTipState.Success(tip)
            }.onFailure { err ->
                _formTipState.value = FormTipState.Error(err.localizedMessage ?: "Could not retrieve form cues")
            }
        }
    }

    fun dismissFormTipDialog() {
        _formTipState.value = FormTipState.Idle
    }

    fun updateProfile(weight: Float, targetWeight: Float, goal: String, level: String) {
        viewModelScope.launch {
            val curr = userProfile.value ?: UserProfileEntity()
            repository.updateUserProfile(
                curr.copy(
                    currentWeightKg = weight,
                    targetWeightKg = targetWeight,
                    primaryGoal = goal,
                    fitnessLevel = level
                )
            )
        }
    }

    private fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40L)
            }
        } catch (_: Exception) {}
    }

    private fun triggerRestCompleteVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 200, 100, 200, 100, 300)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 200, 100, 200, 100, 300), -1)
            }
        } catch (_: Exception) {}
    }
}
