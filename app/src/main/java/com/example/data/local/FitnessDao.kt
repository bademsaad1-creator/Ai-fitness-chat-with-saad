package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {

    // Workout Logs
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getRecentWorkoutLogs(sinceTimestamp: Long): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkoutLog(id: Long)

    @Query("SELECT COUNT(*) FROM workout_logs")
    fun getTotalWorkoutsCount(): Flow<Int>

    @Query("SELECT SUM(totalCalories) FROM workout_logs")
    fun getTotalCaloriesBurned(): Flow<Int?>

    @Query("SELECT SUM(distanceKm) FROM workout_logs")
    fun getTotalDistanceRun(): Flow<Double?>

    @Query("SELECT SUM(totalVolumeKg) FROM workout_logs")
    fun getTotalVolumeLifted(): Flow<Double?>

    @Query("SELECT SUM(durationMinutes) FROM workout_logs")
    fun getTotalDurationMinutes(): Flow<Int?>

    @Query("SELECT * FROM workout_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getLogsAscendingSince(sinceTimestamp: Long): Flow<List<WorkoutLogEntity>>

    // Saved Routines
    @Query("SELECT * FROM saved_routines ORDER BY createdAt DESC")
    fun getAllSavedRoutines(): Flow<List<SavedRoutineEntity>>

    @Query("SELECT * FROM saved_routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: Long): SavedRoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRoutine(routine: SavedRoutineEntity): Long

    @Query("DELETE FROM saved_routines WHERE id = :id")
    suspend fun deleteSavedRoutine(id: Long)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)
}
