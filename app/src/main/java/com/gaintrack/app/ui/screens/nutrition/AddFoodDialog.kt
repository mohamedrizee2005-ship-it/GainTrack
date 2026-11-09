package com.gaintrack.app.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.ui.theme.CyanAccent
import com.gaintrack.app.ui.theme.EmeraldLight
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBorder
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.NavySurfaceVariant
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun AddFoodDialog(
    foodItems: List<FoodItemEntity>,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onFoodLogged: (mealType: String, foodName: String, servings: Double, calories: Int, protein: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack / Shake")
    var selectedMealType by remember { mutableStateOf("Lunch") }
    var selectedFood by remember { mutableStateOf<FoodItemEntity?>(foodItems.firstOrNull()) }
    var servingsInput by remember { mutableStateOf("1.0") }
    var isManualEntry by remember { mutableStateOf(false) }

    // Manual fields
    var manualName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var manualProtein by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(20.dp)),
            color = NavySurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isManualEntry) "Custom Food Entry" else "Add Meal / Food",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Meal Type selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mealTypes.forEach { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = NavyDarkest,
                                containerColor = NavySurfaceVariant,
                                labelColor = TextWhite
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isManualEntry) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChanged,
                        placeholder = { Text("Search Sri Lankan foods (Rice, Egg, Dhal...)", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Food Item List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(foodItems) { item ->
                            val isSelected = selectedFood?.id == item.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) EmeraldPrimary else NavyBorder.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedFood = item },
                                color = if (isSelected) NavySurfaceVariant else NavyDarkest.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = item.servingUnit,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${item.caloriesPerUnit} kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = EmeraldLight,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "%.1fg protein".format(item.proteinPerUnit),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CyanAccent
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Servings input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Servings multiplier:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite
                        )
                        OutlinedTextField(
                            value = servingsInput,
                            onValueChange = { servingsInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = NavyBorder
                            )
                        )
                    }

                    // Calculation Preview
                    val servings = servingsInput.toDoubleOrNull() ?: 1.0
                    val currentItem = selectedFood
                    if (currentItem != null) {
                        val computedCals = (currentItem.caloriesPerUnit * servings).toInt()
                        val computedProt = (currentItem.proteinPerUnit * servings)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total: $computedCals kcal • %.1f g protein".format(computedProt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { isManualEntry = true }) {
                        Text("+ Can't find food? Enter custom", color = EmeraldLight, fontSize = 12.sp)
                    }
                } else {
                    // Manual entry fields
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text("Food Name (e.g. Pol Roti with Dhal)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = NavyBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = manualCalories,
                            onValueChange = { manualCalories = it },
                            label = { Text("Calories (kcal)", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = NavyBorder
                            )
                        )
                        OutlinedTextField(
                            value = manualProtein,
                            onValueChange = { manualProtein = it },
                            label = { Text("Protein (g)", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = NavyBorder
                            )
                        )
                    }
                    TextButton(onClick = { isManualEntry = false }) {
                        Text("← Back to food database", color = EmeraldLight, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit button
                Button(
                    onClick = {
                        if (isManualEntry) {
                            val cals = manualCalories.toIntOrNull() ?: 200
                            val prot = manualProtein.toDoubleOrNull() ?: 5.0
                            if (manualName.isNotBlank()) {
                                onFoodLogged(selectedMealType, manualName, 1.0, cals, prot)
                                onDismiss()
                            }
                        } else {
                            val item = selectedFood
                            val servings = servingsInput.toDoubleOrNull() ?: 1.0
                            if (item != null) {
                                val cals = (item.caloriesPerUnit * servings).toInt()
                                val prot = item.proteinPerUnit * servings
                                onFoodLogged(selectedMealType, item.name, servings, cals, prot)
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = NavyDarkest
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add to Meal Log", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
