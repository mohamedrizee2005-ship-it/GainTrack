package com.gaintrack.app.data.repository

import com.gaintrack.app.data.local.dao.NutritionDao
import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.data.local.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionRepository(private val nutritionDao: NutritionDao) {

    val allFoodItems: Flow<List<FoodItemEntity>> = nutritionDao.getAllFoodItems()

    fun searchFood(query: String): Flow<List<FoodItemEntity>> {
        return if (query.isBlank()) {
            nutritionDao.getAllFoodItems()
        } else {
            nutritionDao.searchFoodItems(query.trim())
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getMealsForDate(dateString: String): Flow<List<MealLogEntity>> {
        return nutritionDao.getMealsForDate(dateString)
    }

    fun getMealHistory(limit: Int = 30): Flow<List<MealLogEntity>> {
        return nutritionDao.getMealHistory(limit)
    }

    suspend fun logMeal(
        dateString: String = getTodayDateString(),
        mealType: String,
        foodName: String,
        servings: Double,
        calories: Int,
        proteinGrams: Double,
        carbsGrams: Double = 0.0,
        fatGrams: Double = 0.0
    ): Long {
        val meal = MealLogEntity(
            dateString = dateString,
            mealType = mealType,
            foodName = foodName,
            servings = servings,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            timestampEpochMs = System.currentTimeMillis()
        )
        return nutritionDao.insertMeal(meal)
    }

    suspend fun logQuickWeightGainShake(mealType: String = "Snack / Shake"): Long {
        // High calorie homemade shake: 250ml milk, 1 banana, 40g oats, 2 tbsp peanut butter
        return logMeal(
            mealType = mealType,
            foodName = "Homemade Weight-Gain Shake (Milk + Banana + Oats + Peanut Butter)",
            servings = 1.0,
            calories = 580,
            proteinGrams = 22.1
        )
    }

    suspend fun deleteMeal(meal: MealLogEntity) {
        nutritionDao.deleteMeal(meal)
    }

    suspend fun addCustomFood(foodItem: FoodItemEntity): Long {
        return nutritionDao.insertFoodItem(foodItem.copy(isCustom = true))
    }
}
