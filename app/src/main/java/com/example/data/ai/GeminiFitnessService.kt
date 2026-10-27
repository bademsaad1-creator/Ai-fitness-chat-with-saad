package com.example.data.ai

import android.util.Log
import com.example.data.model.FitnessJsonHelper
import com.example.data.model.FormAnalysisTip
import com.example.data.model.GeneratedWorkoutRoutine
import com.example.data.model.NutritionPlan
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "FitPulseGemini"

class GeminiFitnessService {

    // 1. Routine Generation Model with Structured JSON Output
    private val routineSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "routineTitle" to Schema.string(description = "High impact workout title"),
                "overview" to Schema.string(description = "Brief 1-2 sentence strategy explanation"),
                "estimatedDurationMinutes" to Schema.integer(description = "Total estimated workout time in minutes"),
                "targetMuscles" to Schema.string(description = "Comma separated target muscle groups"),
                "difficultyLevel" to Schema.string(description = "Beginner, Intermediate, or Advanced"),
                "warmupNotes" to Schema.string(description = "2-3 dynamic warm-up drills"),
                "exercises" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "name" to Schema.string(description = "Precise exercise name"),
                            "muscleTarget" to Schema.string(description = "Primary muscle targeted"),
                            "sets" to Schema.integer(description = "Number of sets to perform"),
                            "reps" to Schema.string(description = "Rep range, e.g. 8-10 or 12-15"),
                            "restSeconds" to Schema.integer(description = "Rest duration in seconds between sets"),
                            "notes" to Schema.string(description = "Form cue or tempo note"),
                            "equipment" to Schema.string(description = "Required equipment or Bodyweight")
                        )
                    ),
                    description = "List of structured exercises"
                ),
                "cooldownNotes" to Schema.string(description = "Recommended cooldown static stretches")
            )
        )
    }

    private val routineModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = routineSchema
                temperature = 0.7f
            }
        )
    }

    // 2. Nutrition Model with Structured Schema
    private val nutritionSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "dailyCalorieTarget" to Schema.integer(description = "Target daily calories"),
                "proteinGrams" to Schema.integer(description = "Target daily protein in grams"),
                "carbsGrams" to Schema.integer(description = "Target daily carbohydrates in grams"),
                "fatsGrams" to Schema.integer(description = "Target daily healthy fats in grams"),
                "advice" to Schema.string(description = "Concise nutritional coaching rationale"),
                "meals" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "mealType" to Schema.string(description = "Breakfast, Lunch, Dinner, or Snack"),
                            "title" to Schema.string(description = "Meal dish title"),
                            "description" to Schema.string(description = "Key ingredients and quick prep method"),
                            "calories" to Schema.integer(description = "Meal calories"),
                            "proteinGrams" to Schema.integer(description = "Meal protein grams"),
                            "carbsGrams" to Schema.integer(description = "Meal carbohydrates grams"),
                            "fatsGrams" to Schema.integer(description = "Meal fat grams")
                        )
                    ),
                    description = "Daily meal breakdowns"
                )
            )
        )
    }

    private val nutritionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = nutritionSchema
                temperature = 0.6f
            }
        )
    }

    // 3. Form Analysis Model
    private val formSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "exerciseName" to Schema.string(description = "Exercise name"),
                "primaryMuscles" to Schema.string(description = "Primary and secondary muscles"),
                "setupCues" to Schema.array(items = Schema.string(), description = "Step by step setup cues"),
                "movementCues" to Schema.array(items = Schema.string(), description = "Execution and eccentric/concentric cues"),
                "commonMistakes" to Schema.array(items = Schema.string(), description = "Key safety pitfalls to avoid"),
                "breathingTip" to Schema.string(description = "Inhale and exhale timing cue")
            )
        )
    }

    private val formModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = formSchema
                temperature = 0.5f
            }
        )
    }

    // 4. Conversational AI Coach Model (Free-form text responses)
    private val coachModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
                maxOutputTokens = 1000
            }
        )
    }

    suspend fun generateWorkoutRoutine(
        goal: String,
        targetMuscles: List<String>,
        equipment: List<String>,
        durationMinutes: Int,
        fitnessLevel: String,
        extraNotes: String = ""
    ): Result<GeneratedWorkoutRoutine> {
        return try {
            val prompt = """
                You are an elite certified strength and conditioning specialist (CSCS).
                Design a custom, science-backed workout routine for:
                - Goal: $goal
                - Target Muscle Group(s): ${targetMuscles.joinToString(", ")}
                - Available Equipment: ${if (equipment.isEmpty()) "Bodyweight Only" else equipment.joinToString(", ")}
                - Target Duration: $durationMinutes minutes
                - Fitness Level: $fitnessLevel
                ${if (extraNotes.isNotBlank()) "- User constraints / preferences: $extraNotes" else ""}
                
                Ensure the exercises follow optimal exercise ordering (compound multi-joint movements first, followed by accessory isolation work).
                Provide realistic rep ranges, set counts, and rest intervals appropriate for $goal.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                routineModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("Empty response from AI")
            val routine = FitnessJsonHelper.generatedRoutineAdapter.fromJson(jsonText)
                ?: throw IllegalStateException("Could not parse generated workout routine JSON")

            Result.success(routine)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Workout routine generation timed out", e)
            Result.failure(Exception("AI workout generation timed out. Please check your network and try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Workout routine generation failed", e)
            Result.failure(e)
        }
    }

    suspend fun generateNutritionPlan(
        goal: String,
        weightKg: Float,
        heightCm: Float,
        activityLevel: String,
        dietaryPreference: String
    ): Result<NutritionPlan> {
        return try {
            val prompt = """
                You are an expert sports nutritionist and dietitian.
                Create a precision macro and daily meal guide for an athlete with:
                - Primary Goal: $goal
                - Current Weight: $weightKg kg
                - Height: $heightCm cm
                - Activity Level: $activityLevel
                - Dietary Preference / Allergies: $dietaryPreference
                
                Calculate accurate total daily energy expenditure (TDEE) and optimal protein/carbs/fat targets.
                Provide 4 high-protein, wholesome meals (Breakfast, Lunch, Dinner, Snack) that match these macro totals.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                nutritionModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("Empty response from AI")
            val plan = FitnessJsonHelper.nutritionPlanAdapter.fromJson(jsonText)
                ?: throw IllegalStateException("Could not parse nutrition plan JSON")

            Result.success(plan)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Nutrition plan generation timed out", e)
            Result.failure(Exception("Nutrition plan generation timed out. Please try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Nutrition plan generation failed", e)
            Result.failure(e)
        }
    }

    suspend fun analyzeExerciseForm(exerciseName: String): Result<FormAnalysisTip> {
        return try {
            val prompt = """
                Provide biomechanical form cues and injury prevention guidelines for the exercise: $exerciseName.
                Include setup cues, movement execution cues, common mistakes to watch for, and breathing tips.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                formModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("Empty response from AI")
            val tip = FitnessJsonHelper.formAnalysisAdapter.fromJson(jsonText)
                ?: throw IllegalStateException("Could not parse form analysis JSON")

            Result.success(tip)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Form analysis timed out", e)
            Result.failure(Exception("Form analysis timed out."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Form analysis failed", e)
            Result.failure(e)
        }
    }

    suspend fun askFitnessCoachStream(
        userMessage: String,
        categoryFocus: String = "General",
        conversationHistory: List<Pair<String, String>> = emptyList(),
        athleteContext: String = "",
        onChunkReceived: (String) -> Unit
    ): Result<String> {
        return try {
            val focusGuideline = when (categoryFocus) {
                "Exercise Form" -> """
                    Focus deeply on biomechanics, joint alignment, setup cues, eccentric/concentric cadence, and injury prevention.
                    Break instructions into clear setup and execution bullet points.
                """.trimIndent()
                "Nutrition Tips" -> """
                    Focus on evidence-based sports nutrition: macro distributions, nutrient timing around workouts, hydration, and wholesome fueling.
                    Provide actionable numbers and food choices.
                """.trimIndent()
                "Motivation" -> """
                    Channel high-energy, empathetic, unstoppable athletic motivation.
                    Help the user overcome fatigue, build iron discipline, and stay consistent with powerful mental frameworks.
                """.trimIndent()
                else -> "Provide well-rounded, evidence-based strength, nutrition, and mindset advice."
            }

            val systemContext = """
                You are Coach Titan, the elite AI fitness and athletic performance coach in FitPulse.
                Tone: High-energy, knowledgeable, encouraging, and evidence-based (CSCS-certified level).
                Category Focus: $categoryFocus.
                $focusGuideline
                Athlete Context: $athleteContext
                Use concise paragraphs and bullet points for readability. Avoid generic fluff.
            """.trimIndent()

            val formattedHistory = conversationHistory.takeLast(8).joinToString("\n") { (role, msg) ->
                if (role == "user") "Athlete: $msg" else "Coach Titan: $msg"
            }

            val fullPrompt = """
                $systemContext

                $formattedHistory
                Athlete: $userMessage
                Coach Titan:
            """.trimIndent()

            val fullResponse = StringBuilder()
            withTimeout(60_000L) {
                coachModel.generateContentStream(fullPrompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    fullResponse.append(text)
                    onChunkReceived(fullResponse.toString())
                }
            }
            val completeText = fullResponse.toString()
            if (completeText.isBlank()) {
                throw IllegalStateException("Empty response from Coach Titan")
            }
            Result.success(completeText)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Coach streaming timed out", e)
            Result.failure(Exception("Coach Titan's response timed out. Please try asking again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Coach request failed", e)
            Result.failure(e)
        }
    }

    suspend fun askFitnessCoach(
        userMessage: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        athleteContext: String = ""
    ): Result<String> {
        return askFitnessCoachStream(
            userMessage = userMessage,
            categoryFocus = "General",
            conversationHistory = conversationHistory,
            athleteContext = athleteContext,
            onChunkReceived = {}
        )
    }
}
