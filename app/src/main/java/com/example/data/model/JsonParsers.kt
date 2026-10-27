package com.example.data.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object FitnessJsonHelper {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val exerciseListType = Types.newParameterizedType(List::class.java, ExerciseItem::class.java)
    val exerciseListAdapter = moshi.adapter<List<ExerciseItem>>(exerciseListType)

    val generatedRoutineAdapter = moshi.adapter(GeneratedWorkoutRoutine::class.java)
    val nutritionPlanAdapter = moshi.adapter(NutritionPlan::class.java)
    val formAnalysisAdapter = moshi.adapter(FormAnalysisTip::class.java)

    fun parseExercises(json: String): List<ExerciseItem> {
        return try {
            exerciseListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeExercises(list: List<ExerciseItem>): String {
        return try {
            exerciseListAdapter.toJson(list)
        } catch (e: Exception) {
            "[]"
        }
    }
}
