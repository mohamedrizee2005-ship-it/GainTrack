package com.gaintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.data.local.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItemEntity>)

    @Delete
    suspend fun deleteFoodItem(item: FoodItemEntity)

    @Query("SELECT * FROM meal_logs WHERE dateString = :dateString ORDER BY timestampEpochMs ASC")
    fun getMealsForDate(dateString: String): Flow<List<MealLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLogEntity): Long

    @Delete
    suspend fun deleteMeal(meal: MealLogEntity)

    @Query("SELECT * FROM meal_logs ORDER BY timestampEpochMs DESC LIMIT :limit")
    fun getMealHistory(limit: Int = 30): Flow<List<MealLogEntity>>
}
