package com.gaintrack.app.data.local

import com.gaintrack.app.data.local.entity.FoodItemEntity
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import com.gaintrack.app.data.local.entity.WorkoutExerciseEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PrepopulatedData {

    val defaultUserProfile = UserProfileEntity(
        id = 1,
        currentWeightKg = 47.0,
        targetWeightKg = 55.0,
        heightCm = 168.0,
        calorieTarget = 2100, // Safe moderate surplus for 47kg beginner
        proteinTarget = 85,   // ~1.8g/kg body weight
        fitnessLevel = "Beginner",
        pushupAbility = "5–10 consecutive reps",
        mainGoal = "Healthy weight gain & muscle development",
        trainingLocation = "Home (Backpack & Furniture)",
        workoutFrequency = 4,
        preferredWorkoutTime = "19:30",
        foodPreference = "Affordable Sri Lankan food",
        darkTheme = true,
        workoutReminderEnabled = true,
        nutritionReminderEnabled = true,
        checkInReminderEnabled = true,
        waterReminderEnabled = true,
        sleepReminderEnabled = true,
        workoutReminderTime = "19:30",
        checkInReminderTime = "19:00"
    )

    val defaultWorkoutPlans = listOf(
        WorkoutPlanEntity(
            id = 1,
            dayOfWeek = "MONDAY",
            title = "Chest, Shoulders & Triceps",
            description = "Build upper body pushing strength, chest thickness and shoulder stability using bodyweight and elevation.",
            estimatedDurationMinutes = 35,
            targetMuscleGroups = "Pectorals, Anterior Deltoids, Triceps"
        ),
        WorkoutPlanEntity(
            id = 2,
            dayOfWeek = "TUESDAY",
            title = "Legs & Core",
            description = "Lower body foundation for functional strength, glute activation and abdominal core endurance.",
            estimatedDurationMinutes = 35,
            targetMuscleGroups = "Quadriceps, Glutes, Hamstrings, Calves, Abs"
        ),
        WorkoutPlanEntity(
            id = 3,
            dayOfWeek = "THURSDAY",
            title = "Back & Biceps",
            description = "Upper body pulling strength with backpack resistance to build posture, back width and arm thickness.",
            estimatedDurationMinutes = 35,
            targetMuscleGroups = "Latissimus Dorsi, Rhomboids, Biceps, Rear Deltoids"
        ),
        WorkoutPlanEntity(
            id = 4,
            dayOfWeek = "SATURDAY",
            title = "Full Body",
            description = "High-frequency systemic stimulation connecting upper, lower and core muscle groups for total muscle growth.",
            estimatedDurationMinutes = 35,
            targetMuscleGroups = "Chest, Back, Legs, Glutes, Core"
        )
    )

    fun getExercisesForPlan(planId: Long): List<WorkoutExerciseEntity> {
        return when (planId) {
            1L -> listOf(
                WorkoutExerciseEntity(
                    workoutPlanId = 1,
                    orderIndex = 1,
                    name = "Incline push-ups",
                    defaultSets = 3,
                    minReps = 8,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Place hands shoulder-width on a sturdy desk, table or bed frame. Step back so body forms a straight line from heels to head. Lower chest to the edge with control, pause for 1s, then press back up.",
                    safetyTips = "Keep elbows at roughly 45 degrees to torso. Squeeze glutes and abs to avoid sagging hips. Stop if wrists hurt.",
                    targetMuscles = "Chest, Triceps, Shoulders"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 1,
                    orderIndex = 2,
                    name = "Standard push-ups",
                    defaultSets = 2,
                    minReps = 4,
                    maxReps = 8,
                    defaultRestSeconds = 90,
                    defaultWeightKg = 0.0,
                    formInstructions = "Hands slightly wider than shoulders on floor. Lower until chest touches floor gently, then push aggressively. Maintain strict plank throughout.",
                    safetyTips = "Do not flare elbows 90 degrees out. If fatigued, do not sacrifice form; end set safely.",
                    targetMuscles = "Chest, Anterior Deltoids, Triceps"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 1,
                    orderIndex = 3,
                    name = "Pike push-ups",
                    defaultSets = 2,
                    minReps = 5,
                    maxReps = 8,
                    defaultRestSeconds = 75,
                    defaultWeightKg = 0.0,
                    formInstructions = "Push hips high into an inverted V shape. Bend elbows backward to lower the crown of your head diagonally forward towards the floor, then push back up.",
                    safetyTips = "Keep neck relaxed and head aligned. Do not drop rapidly. Great alternative to overhead pressing.",
                    targetMuscles = "Shoulders, Upper Chest, Triceps"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 1,
                    orderIndex = 4,
                    name = "Close-grip incline push-ups",
                    defaultSets = 2,
                    minReps = 8,
                    maxReps = 10,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Hands placed 6–8 inches apart on sturdy elevated surface. Keep elbows tucked tightly against ribs as you lower and press.",
                    safetyTips = "Emphasizes triceps lockouts. Maintain strict core bracing.",
                    targetMuscles = "Triceps, Inner Chest"
                )
            )
            2L -> listOf(
                WorkoutExerciseEntity(
                    workoutPlanId = 2,
                    orderIndex = 1,
                    name = "Bodyweight squats",
                    defaultSets = 3,
                    minReps = 12,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Stand with feet shoulder-width, toes slightly out. Sit hips back and down until thighs are parallel to ground. Drive through midfoot and heels to stand.",
                    safetyTips = "Keep heels planted and chest proud. Knees should track in line with your 2nd toe.",
                    targetMuscles = "Quadriceps, Glutes"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 2,
                    orderIndex = 2,
                    name = "Reverse lunges",
                    defaultSets = 2,
                    minReps = 8,
                    maxReps = 8,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Step one leg backward, dropping the back knee until it hovers just an inch above the floor. Front knee at 90 degrees. Push off front heel to return.",
                    safetyTips = "Reverse lunges are gentler on knees than forward lunges. Move with balance.",
                    targetMuscles = "Glutes, Quadriceps, Hamstrings"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 2,
                    orderIndex = 3,
                    name = "Glute bridges",
                    defaultSets = 3,
                    minReps = 12,
                    maxReps = 15,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Lie on back with knees bent and feet flat on floor. Drive heels into floor and squeeze glutes hard to raise hips until body forms a ramp. Hold top 1-2s.",
                    safetyTips = "Avoid arching lower back at top; focus purely on glute contraction.",
                    targetMuscles = "Glutes, Hamstrings"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 2,
                    orderIndex = 4,
                    name = "Calf raises",
                    defaultSets = 2,
                    minReps = 15,
                    maxReps = 15,
                    defaultRestSeconds = 45,
                    defaultWeightKg = 0.0,
                    formInstructions = "Stand near a wall for balance. Raise heels as high as possible on tiptoes, hold for 1 second, then lower with a 2-second negative stretch.",
                    safetyTips = "Controlled tempo is key for calf hypertrophy.",
                    targetMuscles = "Gastrocnemius, Soleus"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 2,
                    orderIndex = 5,
                    name = "Plank",
                    defaultSets = 2,
                    minReps = 20, // seconds
                    maxReps = 40,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Rest on forearms and toes. Tuck pelvis, pull belly button towards spine, and squeeze glutes and quads tight. Breathe steadily.",
                    safetyTips = "Do not allow lower back to sway or sag. If form breaks, end set.",
                    targetMuscles = "Core, Rectus Abdominis, Transverse Abdominis"
                )
            )
            3L -> listOf(
                WorkoutExerciseEntity(
                    workoutPlanId = 3,
                    orderIndex = 1,
                    name = "Backpack bent-over rows",
                    defaultSets = 3,
                    minReps = 10,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 4.0, // starting weight in backpack
                    formInstructions = "Load backpack with books (3-5 kg). Hinge forward at hips keeping back flat and spine neutral. Pull backpack straps towards lower belly, squeezing back muscles.",
                    safetyTips = "Never round lower back. Brace abs firmly throughout movement.",
                    targetMuscles = "Lats, Rhomboids, Traps, Rear Deltoids"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 3,
                    orderIndex = 2,
                    name = "Single-arm backpack rows",
                    defaultSets = 2,
                    minReps = 10,
                    maxReps = 10,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 4.0,
                    formInstructions = "Support one hand on a chair or table. Hold backpack handle with other arm. Pull elbow up and back towards hip. Complete reps on both arms.",
                    safetyTips = "Keep shoulders square to floor, avoid twisting upper body.",
                    targetMuscles = "Latissimus Dorsi, Biceps"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 3,
                    orderIndex = 3,
                    name = "Backpack biceps curls",
                    defaultSets = 2,
                    minReps = 10,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 3.0,
                    formInstructions = "Hold backpack straps with palms facing forward. Pin elbows firmly to your sides and curl the bag upwards, squeezing biceps at top.",
                    safetyTips = "Do not swing torso to gain momentum. Maintain strict upright posture.",
                    targetMuscles = "Biceps Brachii, Brachialis"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 3,
                    orderIndex = 4,
                    name = "Superman holds",
                    defaultSets = 2,
                    minReps = 15, // seconds
                    maxReps = 20,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Lie face down with arms extended forward. Lift chest, arms, and thighs slightly off floor by contracting lower back and glutes. Hold position.",
                    safetyTips = "Gaze at floor to keep neck neutral. Don't hyperextend aggressively.",
                    targetMuscles = "Erector Spinae, Glutes"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 3,
                    orderIndex = 5,
                    name = "Reverse snow angels",
                    defaultSets = 2,
                    minReps = 10,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Lie face down, arms by your sides. Lift hands slightly off floor, sweep arms in a wide controlled arc overhead and back down, squeezing upper back.",
                    safetyTips = "Maintain steady breathing. Excellent for postural scapular retractors.",
                    targetMuscles = "Rhomboids, Mid/Lower Trapezius, Rear Delts"
                )
            )
            4L -> listOf(
                WorkoutExerciseEntity(
                    workoutPlanId = 4,
                    orderIndex = 1,
                    name = "Incline or standard push-ups",
                    defaultSets = 2,
                    minReps = 8,
                    maxReps = 10,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Choose elevation based on energy. Push chest away with full control, keeping body rigid and elbows tucked at 45 degrees.",
                    safetyTips = "Stop 1 rep shy of failure to ensure recovery.",
                    targetMuscles = "Chest, Shoulders, Triceps"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 4,
                    orderIndex = 2,
                    name = "Backpack rows",
                    defaultSets = 2,
                    minReps = 10,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 4.0,
                    formInstructions = "Hinged at hips with flat back, pull backpack into lower ribcage and squeeze lats.",
                    safetyTips = "Keep core braced throughout.",
                    targetMuscles = "Back, Biceps"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 4,
                    orderIndex = 3,
                    name = "Squats",
                    defaultSets = 2,
                    minReps = 12,
                    maxReps = 12,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Controlled 2-second descent, hips parallel or below, drive upward through heels.",
                    safetyTips = "Keep knees aligned with toes.",
                    targetMuscles = "Quads, Glutes"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 4,
                    orderIndex = 4,
                    name = "Glute bridges",
                    defaultSets = 2,
                    minReps = 15,
                    maxReps = 15,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Feet flat, drive hips up and contract glutes firmly for a 2-second hold at the apex.",
                    safetyTips = "Avoid lumbar overextension.",
                    targetMuscles = "Glutes, Hamstrings"
                ),
                WorkoutExerciseEntity(
                    workoutPlanId = 4,
                    orderIndex = 5,
                    name = "Lying leg raises",
                    defaultSets = 2,
                    minReps = 10,
                    maxReps = 10,
                    defaultRestSeconds = 60,
                    defaultWeightKg = 0.0,
                    formInstructions = "Lie on back with hands slightly under hips for lumbar support. Raise straight legs up to vertical, lower slowly without touching floor.",
                    safetyTips = "Only lower legs as far as your lower back stays flat on the floor.",
                    targetMuscles = "Lower Abs, Hip Flexors"
                )
            )
            else -> emptyList()
        }
    }

    val defaultFoodItems = listOf(
        FoodItemEntity(
            name = "Cooked Rice (White / Red)",
            servingUnit = "1 cup (~200g)",
            caloriesPerUnit = 260,
            proteinPerUnit = 5.0,
            category = "Carbs / Staples"
        ),
        FoodItemEntity(
            name = "Boiled Egg",
            servingUnit = "1 whole large",
            caloriesPerUnit = 78,
            proteinPerUnit = 6.3,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Egg Omelette / Fried Egg",
            servingUnit = "1 whole egg",
            caloriesPerUnit = 95,
            proteinPerUnit = 6.3,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Sri Lankan Chicken Curry",
            servingUnit = "1 piece (~100g)",
            caloriesPerUnit = 190,
            proteinPerUnit = 22.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Fish Curry / Ambul Thiyal",
            servingUnit = "1 piece (~80g)",
            caloriesPerUnit = 140,
            proteinPerUnit = 18.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Parippu / Dhal Curry",
            servingUnit = "1 cup (~150g)",
            caloriesPerUnit = 170,
            proteinPerUnit = 9.0,
            category = "Sri Lankan Food"
        ),
        FoodItemEntity(
            name = "Fresh Cow Milk / UHT Milk",
            servingUnit = "1 glass (250ml)",
            caloriesPerUnit = 150,
            proteinPerUnit = 8.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Banana (Ambul / Seeni / Kolikuttu)",
            servingUnit = "1 medium banana",
            caloriesPerUnit = 90,
            proteinPerUnit = 1.1,
            category = "Carbs / Staples"
        ),
        FoodItemEntity(
            name = "Roasted Peanuts (Rata Kaju)",
            servingUnit = "1 handful (~30g)",
            caloriesPerUnit = 170,
            proteinPerUnit = 7.5,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Peanut Butter",
            servingUnit = "2 tablespoons (32g)",
            caloriesPerUnit = 190,
            proteinPerUnit = 8.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Rolled / Instant Oats",
            servingUnit = "1/2 cup raw (40g)",
            caloriesPerUnit = 150,
            proteinPerUnit = 5.0,
            category = "Carbs / Staples"
        ),
        FoodItemEntity(
            name = "String Hoppers (Idiyappam)",
            servingUnit = "5 pieces",
            caloriesPerUnit = 180,
            proteinPerUnit = 3.5,
            category = "Sri Lankan Food"
        ),
        FoodItemEntity(
            name = "Pol Roti (Coconut Flatbread)",
            servingUnit = "1 medium roti",
            caloriesPerUnit = 220,
            proteinPerUnit = 4.5,
            category = "Sri Lankan Food"
        ),
        FoodItemEntity(
            name = "Boiled Potatoes (Ala)",
            servingUnit = "1 medium potato (~150g)",
            caloriesPerUnit = 110,
            proteinPerUnit = 2.5,
            category = "Carbs / Staples"
        ),
        FoodItemEntity(
            name = "Canned Tuna / Jack Mackerel",
            servingUnit = "100g portion",
            caloriesPerUnit = 130,
            proteinPerUnit = 24.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Soya Meat Curry",
            servingUnit = "1 cup cooked",
            caloriesPerUnit = 160,
            proteinPerUnit = 15.0,
            category = "High Protein"
        ),
        FoodItemEntity(
            name = "Weight-Gain Shake (Milk + Banana + Oats + PB)",
            servingUnit = "1 large shake (500ml)",
            caloriesPerUnit = 580,
            proteinPerUnit = 22.1,
            category = "Shake / Smoothie"
        )
    )

    fun getInitialWeightLogs(): List<WeightLogEntity> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        
        // 7 days ago baseline
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val baselineDate = dateFormat.format(cal.time)
        val baselineTime = cal.timeInMillis

        // Today
        cal.add(Calendar.DAY_OF_YEAR, 7)
        val todayDate = dateFormat.format(cal.time)
        val todayTime = cal.timeInMillis

        return listOf(
            WeightLogEntity(
                dateString = baselineDate,
                timestampEpochMs = baselineTime,
                weightKg = 46.8,
                notes = "Starting baseline check-in"
            ),
            WeightLogEntity(
                dateString = todayDate,
                timestampEpochMs = todayTime,
                weightKg = 47.0,
                notes = "Current starting weight - feeling energized"
            )
        )
    }
}
