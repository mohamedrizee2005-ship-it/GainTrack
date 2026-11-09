package com.gaintrack.app.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.data.local.entity.MealLogEntity
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.repository.NutritionRepository
import com.gaintrack.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NutritionUiState(
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val todayMeals: List<MealLogEntity> = emptyList(),
    val todayCalories: Int = 0,
    val todayProtein: Double = 0.0,
    val remainingCalories: Int = 2100,
    val remainingProtein: Double = 85.0,
    val searchedFoods: List<FoodItemEntity> = emptyList(),
    val searchQuery: String = ""
)

class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val todayDate = nutritionRepository.getTodayDateString()

    val uiState: StateFlow<NutritionUiState> = combine(
        userRepository.userProfile,
        nutritionRepository.getMealsForDate(todayDate),
        _searchQuery.flatMapLatest { nutritionRepository.searchFood(it) }
    ) { profile, meals, foods ->
        val userProf = profile ?: UserProfileEntity()
        val totalCals = meals.sumOf { it.calories }
        val totalProt = meals.sumOf { it.proteinGrams }

        val remCals = (userProf.calorieTarget - totalCals).coerceAtLeast(0)
        val remProt = (userProf.proteinTarget - totalProt).coerceAtLeast(0.0)

        NutritionUiState(
            userProfile = userProf,
            todayMeals = meals,
            todayCalories = totalCals,
            todayProtein = totalProt,
            remainingCalories = remCals,
            remainingProtein = remProt,
            searchedFoods = foods,
            searchQuery = _searchQuery.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutritionUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun logMeal(
        mealType: String,
        foodName: String,
        servings: Double,
        calories: Int,
        proteinGrams: Double
    ) {
        viewModelScope.launch {
            nutritionRepository.logMeal(
                dateString = todayDate,
                mealType = mealType,
                foodName = foodName,
                servings = servings,
                calories = calories,
                proteinGrams = proteinGrams
            )
        }
    }

    fun logQuickWeightGainShake(mealType: String = "Snack / Shake") {
        viewModelScope.launch {
            nutritionRepository.logQuickWeightGainShake(mealType)
        }
    }

    fun deleteMeal(meal: MealLogEntity) {
        viewModelScope.launch {
            nutritionRepository.deleteMeal(meal)
        }
    }

    fun addNewCustomFood(
        name: String,
        servingUnit: String,
        calories: Int,
        protein: Double,
        category: String
    ) {
        viewModelScope.launch {
            val item = FoodItemEntity(
                name = name,
                servingUnit = servingUnit,
                caloriesPerUnit = calories,
                proteinPerUnit = protein,
                category = category,
                isCustom = true
            )
            nutritionRepository.addCustomFood(item)
        }
    }
}
