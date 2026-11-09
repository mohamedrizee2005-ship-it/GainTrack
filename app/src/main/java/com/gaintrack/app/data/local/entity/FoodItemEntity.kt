package com.gaintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val servingUnit: String,
    val caloriesPerUnit: Int,
    val proteinPerUnit: Double,
    val category: String,
    val isCustom: Boolean = false
)

@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // e.g. "2026-11-09"
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack / Shake"
    val foodName: String,
    val servings: Double,
    val calories: Int,
    val proteinGrams: Double,
    val timestampEpochMs: Long = System.currentTimeMillis()
)
