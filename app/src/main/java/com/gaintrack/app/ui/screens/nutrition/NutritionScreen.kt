package com.gaintrack.app.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.data.local.entity.MealLogEntity
import com.gaintrack.app.ui.components.NutritionProgressBar
import com.gaintrack.app.ui.components.StatCard
import com.gaintrack.app.ui.theme.CyanAccent
import com.gaintrack.app.ui.theme.EmeraldContainer
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBackground
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.RedError
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddFoodDialog(
            foodItems = uiState.searchedFoods,
            searchQuery = uiState.searchQuery,
            onSearchChanged = { viewModel.onSearchQueryChanged(it) },
            onFoodLogged = { mealType, foodName, servings, calories, protein ->
                viewModel.logMeal(mealType, foodName, servings, calories, protein)
            },
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = NavyDarkest,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Food")
            }
        },
        containerColor = NavyBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(NavyBackground)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Nutrition",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target surplus & protein for gradual muscle gain",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Macro Progress Dashboard Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
                color = NavySurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "CALORIES",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.todayCalories} / ${uiState.userProfile.calorieTarget}",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.remainingCalories} kcal remaining",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "PROTEIN",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%.1f / %d g".format(uiState.todayProtein, uiState.userProfile.proteinTarget),
                                style = MaterialTheme.typography.titleLarge,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.1f g remaining".format(uiState.remainingProtein),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NutritionProgressBar(
                        label = "Daily Calories Progress",
                        current = uiState.todayCalories,
                        target = uiState.userProfile.calorieTarget,
                        unit = "kcal",
                        barColor = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NutritionProgressBar(
                        label = "Daily Protein Progress",
                        current = uiState.todayProtein.toInt(),
                        target = uiState.userProfile.proteinTarget,
                        unit = "g",
                        barColor = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Add Weight-Gain Shake Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                color = NavySurface
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldPrimary.copy(alpha = 0.12f),
                                    NavySurface
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Homemade Weight-Gain Shake",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "+580 kcal",
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Recipe: 250ml fresh milk + 1 medium banana + 40g oats + 2 tbsp peanut butter (approx 22.1g protein). Easy liquid calories for fast recovery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.logQuickWeightGainShake() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = NavyDarkest
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Log Shake")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1-Tap Log Homemade Shake (+580 kcal)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Meals logged today
            Text(
                text = "Today's Meal Log",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.todayMeals.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                    color = NavySurface
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No meals logged today yet.\nTap '+' below to add food or log your shake!",
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                val grouped = uiState.todayMeals.groupBy { it.mealType }
                grouped.forEach { (type, meals) ->
                    Text(
                        text = type.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    meals.forEach { meal ->
                        MealEntryRow(
                            meal = meal,
                            onDelete = { viewModel.deleteMeal(meal) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preparation & Nutrition Disclaimer Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                color = NavySurfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Note: Nutritional values vary depending on home cooking style, coconut milk thickness, oil and portion sizes. Use these figures as consistent approximate references.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun MealEntryRow(
    meal: MealLogEntity,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
        color = NavySurface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.foodName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${meal.servings} serving • ${meal.calories} kcal • %.1fg protein".format(meal.proteinGrams),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = RedError.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
