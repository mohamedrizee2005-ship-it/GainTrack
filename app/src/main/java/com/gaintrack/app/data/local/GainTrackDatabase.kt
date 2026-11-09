package com.gaintrack.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gaintrack.app.data.local.dao.NutritionDao
import com.gaintrack.app.data.local.dao.UserDao
import com.gaintrack.app.data.local.dao.WeightDao
import com.gaintrack.app.data.local.dao.WorkoutDao
import com.gaintrack.app.data.local.entity.ExerciseSetRecordEntity
import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.data.local.entity.MealLogEntity
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.local.entity.WeeklyCheckInEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import com.gaintrack.app.data.local.entity.WorkoutExerciseEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutPlanEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSessionEntity::class,
        ExerciseSetRecordEntity::class,
        FoodItemEntity::class,
        MealLogEntity::class,
        WeightLogEntity::class,
        WeeklyCheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GainTrackDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun weightDao(): WeightDao

    companion object {
        @Volatile
        private var INSTANCE: GainTrackDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GainTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GainTrackDatabase::class.java,
                    "gaintrack_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(database: GainTrackDatabase) {
                // Prepopulate User Profile
                database.userDao().insertOrUpdateProfile(PrepopulatedData.defaultUserProfile)

                // Prepopulate Workout Plans
                database.workoutDao().insertPlans(PrepopulatedData.defaultWorkoutPlans)

                // Prepopulate Exercises for each plan
                for (plan in PrepopulatedData.defaultWorkoutPlans) {
                    val exercises = PrepopulatedData.getExercisesForPlan(plan.id)
                    database.workoutDao().insertExercises(exercises)
                }

                // Prepopulate Food Items
                database.nutritionDao().insertFoodItems(PrepopulatedData.defaultFoodItems)

                // Prepopulate Initial Weight Logs
                for (log in PrepopulatedData.getInitialWeightLogs()) {
                    database.weightDao().insertWeightLog(log)
                }
            }
        }
    }
}
