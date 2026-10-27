package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [WorkoutLogEntity::class, SavedRoutineEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FitPulseDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var INSTANCE: FitPulseDatabase? = null

        fun getDatabase(context: Context): FitPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitPulseDatabase::class.java,
                    "fitpulse_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: FitPulseDatabase) {
            val dao = db.fitnessDao()

            // Seed initial User Profile
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    name = "Athlete",
                    fitnessLevel = "Beginner",
                    primaryGoal = "Cardio Health & Muscle Building",
                    currentWeightKg = 74.0f,
                    targetWeightKg = 77.0f,
                    heightCm = 175.0f,
                    dailyCalorieTarget = 2450,
                    dailyProteinTargetGrams = 155,
                    streakDays = 5,
                    lastWorkoutTimestamp = System.currentTimeMillis() - 86400000L
                )
            )

            // 1. Beginner Cardio & Muscle Program: Day 1
            val day1ExercisesJson = """[
                {"name":"Dumbbell Goblet Squat","muscleTarget":"Quadriceps & Glutes","sets":3,"reps":"10-12","restSeconds":60,"notes":"Keep chest tall; strengthens legs while elevating heart rate","equipment":"Dumbbells"},
                {"name":"Resistance Band Chest Press","muscleTarget":"Chest & Triceps","sets":3,"reps":"10-12","restSeconds":60,"notes":"Anchor band securely behind back; press forward with control","equipment":"Resistance Bands"},
                {"name":"Dumbbell Romanian Deadlift","muscleTarget":"Hamstrings & Glutes","sets":3,"reps":"10-12","restSeconds":60,"notes":"Hinge at hips, keep spine neutral; feel the hamstring stretch","equipment":"Dumbbells"},
                {"name":"Band Standing Neutral Rows","muscleTarget":"Upper Back & Lats","sets":3,"reps":"12-15","restSeconds":45,"notes":"Squeeze shoulder blades together at peak contraction","equipment":"Resistance Bands"},
                {"name":"Standing Dumbbell Overhead Press","muscleTarget":"Shoulders & Core","sets":3,"reps":"10","restSeconds":60,"notes":"Brace core tightly to protect lower back","equipment":"Dumbbells"},
                {"name":"Low-Impact Jumping Jacks / High Knees","muscleTarget":"Cardiovascular Conditioning","sets":3,"reps":"45 sec","restSeconds":30,"notes":"Aerobic burn: maintain steady brisk tempo and rhythm","equipment":"Bodyweight"}
            ]""".trimIndent()

            dao.insertSavedRoutine(
                SavedRoutineEntity(
                    title = "Beginner Hybrid: Day 1 - Full Body & Cardio",
                    description = "Day 1 of 3: Dumbbell & resistance band compound strength paired with an aerobic stamina booster (45 min).",
                    targetMuscles = "Full Body, Quads, Chest, Back, Heart",
                    equipment = "Dumbbells, Resistance Bands",
                    difficulty = "Beginner",
                    estimatedMinutes = 45,
                    exercisesJson = day1ExercisesJson,
                    isCustomAiGenerated = true
                )
            )

            // 2. Beginner Cardio & Muscle Program: Day 2
            val day2ExercisesJson = """[
                {"name":"Dumbbell Thruster (Squat to Press)","muscleTarget":"Total Body & Heart Rate","sets":3,"reps":"10","restSeconds":60,"notes":"Fluid power movement from deep squat straight into overhead press","equipment":"Dumbbells"},
                {"name":"Resistance Band Lateral Walk","muscleTarget":"Glute Medius & Hip Stability","sets":3,"reps":"12 steps each side","restSeconds":45,"notes":"Place band around thighs; stay in quarter squat stance","equipment":"Resistance Bands"},
                {"name":"Dumbbell Farmer's March","muscleTarget":"Core Stability & Forearms","sets":3,"reps":"45 sec","restSeconds":45,"notes":"Hold weights at sides, march in place with high knees and tight core","equipment":"Dumbbells"},
                {"name":"Band Standing Woodchops","muscleTarget":"Rotational Obliques & Core","sets":3,"reps":"12 each side","restSeconds":45,"notes":"Rotate through hips and pivot rear foot with power","equipment":"Resistance Bands"},
                {"name":"Dumbbell Renegade Rows","muscleTarget":"Back & Core Anti-Rotation","sets":3,"reps":"8 each side","restSeconds":60,"notes":"From plank or knees, row weight without twisting hips","equipment":"Dumbbells"},
                {"name":"Band Shadow Boxing / Speed Punching","muscleTarget":"Aerobic Conditioning & Shoulders","sets":3,"reps":"60 sec","restSeconds":30,"notes":"Brisk punches against band tension for cardiovascular VO2 boost","equipment":"Resistance Bands"}
            ]""".trimIndent()

            dao.insertSavedRoutine(
                SavedRoutineEntity(
                    title = "Beginner Hybrid: Day 2 - Cardio Intervals & Band Circuit",
                    description = "Day 2 of 3: High-efficiency interval circuit alternating resistance exercises with heart-rate elevation (45 min).",
                    targetMuscles = "Full Body, Shoulders, Core, Cardiovascular",
                    equipment = "Dumbbells, Resistance Bands",
                    difficulty = "Beginner",
                    estimatedMinutes = 45,
                    exercisesJson = day2ExercisesJson,
                    isCustomAiGenerated = true
                )
            )

            // 3. Beginner Cardio & Muscle Program: Day 3
            val day3ExercisesJson = """[
                {"name":"Dumbbell Floor Press","muscleTarget":"Chest, Triceps & Front Deltoids","sets":3,"reps":"10-12","restSeconds":60,"notes":"Upper arms touch floor gently at 45 degrees before pressing up","equipment":"Dumbbells"},
                {"name":"Banded Glute Bridge with Dumbbell","muscleTarget":"Glutes & Hamstrings","sets":3,"reps":"12-15","restSeconds":45,"notes":"Band around knees, rest dumbbell on hips, squeeze glutes at top","equipment":"Dumbbells, Resistance Bands"},
                {"name":"One-Arm Dumbbell Row","muscleTarget":"Lats & Mid-Back","sets":3,"reps":"10 each arm","restSeconds":60,"notes":"Pull elbow back toward hip; squeeze lats at the top","equipment":"Dumbbells"},
                {"name":"Band Bicep Curls to Overhead Triceps Extension","muscleTarget":"Biceps & Triceps","sets":3,"reps":"12","restSeconds":45,"notes":"Continuous tension on arm musculature with smooth tempo","equipment":"Resistance Bands"},
                {"name":"Dumbbell Suitcase Step-Back Lunges","muscleTarget":"Legs & Single-Leg Balance","sets":3,"reps":"10 each leg","restSeconds":60,"notes":"Step back softly, keep front knee tracking over middle toe","equipment":"Dumbbells"},
                {"name":"Cardio Band Mountain Climbers","muscleTarget":"Cardio Stamina & Abdominals","sets":3,"reps":"40 sec","restSeconds":30,"notes":"Band around feet for added resistance as knees drive forward","equipment":"Resistance Bands"}
            ]""".trimIndent()

            dao.insertSavedRoutine(
                SavedRoutineEntity(
                    title = "Beginner Hybrid: Day 3 - Muscle Hypertrophy & Heart Health",
                    description = "Day 3 of 3: Hypertrophy-focused resistance tempo training finishing with heart-health aerobic conditioning (45 min).",
                    targetMuscles = "Chest, Back, Arms, Legs, Core",
                    equipment = "Dumbbells, Resistance Bands",
                    difficulty = "Beginner",
                    estimatedMinutes = 45,
                    exercisesJson = day3ExercisesJson,
                    isCustomAiGenerated = true
                )
            )

            // Seed past month logs across last 30 days to populate the monthly progress chart
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val pastLogs = listOf(
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 1),
                    title = "Beginner Hybrid: Day 1 - Full Body & Cardio",
                    category = "Strength",
                    durationMinutes = 45,
                    totalCalories = 360,
                    totalVolumeKg = 1850.0,
                    distanceKm = 0.0,
                    totalSetsCompleted = 18,
                    exercisesSummary = "6 exercises: Goblet Squats, Band Press, DB RDL, Band Rows, DB Press, Jacks",
                    notes = "Great pacing on Day 1! Felt energized throughout."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 3),
                    title = "Outdoor Cardio Jog & Recovery Run",
                    category = "Running",
                    durationMinutes = 35,
                    totalCalories = 390,
                    totalVolumeKg = 0.0,
                    distanceKm = 5.2,
                    totalSetsCompleted = 1,
                    exercisesSummary = "Continuous aerobic zone 2 running",
                    notes = "Kept heart rate steady at ~138 bpm. Smooth cadence."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 5),
                    title = "Beginner Hybrid: Day 2 - Cardio Intervals & Band Circuit",
                    category = "HIIT",
                    durationMinutes = 45,
                    totalCalories = 410,
                    totalVolumeKg = 1420.0,
                    distanceKm = 1.2,
                    totalSetsCompleted = 18,
                    exercisesSummary = "6 exercises: Thrusters, Band Walks, Farmer March, Woodchops, Renegade Rows, Boxing",
                    notes = "Thrusters were challenging but heart rate spiked nicely!"
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 8),
                    title = "Beginner Hybrid: Day 3 - Muscle Hypertrophy & Heart Health",
                    category = "Strength",
                    durationMinutes = 44,
                    totalCalories = 345,
                    totalVolumeKg = 1960.0,
                    distanceKm = 0.0,
                    totalSetsCompleted = 18,
                    exercisesSummary = "6 exercises: Floor Press, Banded Glute Bridge, DB Row, Band Arms, Lunges, Climbers",
                    notes = "Pump in chest and arms was noticeable. Good recovery."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 11),
                    title = "Tempo Cardio Run",
                    category = "Running",
                    durationMinutes = 40,
                    totalCalories = 440,
                    totalVolumeKg = 0.0,
                    distanceKm = 6.0,
                    totalSetsCompleted = 1,
                    exercisesSummary = "Tempo run outdoors with 5 min warm-up & cooldown",
                    notes = "Set a personal best distance for 40 minutes!"
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 14),
                    title = "Beginner Hybrid: Day 1 - Full Body & Cardio",
                    category = "Strength",
                    durationMinutes = 45,
                    totalCalories = 350,
                    totalVolumeKg = 1750.0,
                    distanceKm = 0.0,
                    totalSetsCompleted = 18,
                    exercisesSummary = "6 exercises: Goblet Squats, Band Press, DB RDL, Band Rows, DB Press, Jacks",
                    notes = "Increased dumbbell weight on Goblet squats from 10kg to 12.5kg."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 17),
                    title = "Interval Sprint & Walk",
                    category = "Running",
                    durationMinutes = 30,
                    totalCalories = 340,
                    totalVolumeKg = 0.0,
                    distanceKm = 4.5,
                    totalSetsCompleted = 6,
                    exercisesSummary = "6x 1-minute sprints alternating with 3-minute walking",
                    notes = "High VO2 max stimulus."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 21),
                    title = "Beginner Hybrid: Day 2 - Cardio Intervals & Band Circuit",
                    category = "HIIT",
                    durationMinutes = 45,
                    totalCalories = 395,
                    totalVolumeKg = 1380.0,
                    distanceKm = 0.8,
                    totalSetsCompleted = 18,
                    exercisesSummary = "6 exercises: Thrusters, Band Walks, March, Woodchops, Rows, Boxing",
                    notes = "Felt much smoother with band tension."
                ),
                WorkoutLogEntity(
                    timestamp = now - (dayMs * 25),
                    title = "Weekend Trail Run",
                    category = "Running",
                    durationMinutes = 48,
                    totalCalories = 520,
                    totalVolumeKg = 0.0,
                    distanceKm = 7.1,
                    totalSetsCompleted = 1,
                    exercisesSummary = "Scenic aerobic endurance trail run",
                    notes = "Solid stamina, heart rate felt comfortable."
                )
            )

            pastLogs.forEach { log ->
                dao.insertWorkoutLog(log)
            }
        }
    }
}
