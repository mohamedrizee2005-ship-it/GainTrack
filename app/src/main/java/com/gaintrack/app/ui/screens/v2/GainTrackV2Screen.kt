package com.gaintrack.app.ui.screens.v2

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF080B13)
private val Panel = Color(0xFF141B2A)
private val Panel2 = Color(0xFF1B2638)
private val Line = Color(0xFF2B394E)
private val Lime = Color(0xFFB7F36B)
private val Aqua = Color(0xFF55D6C2)
private val Muted = Color(0xFF9AA8BA)
private val White = Color(0xFFF4F7FB)

private data class Exercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val rest: Int,
    val target: String,
    val cue: String,
    val videoQuery: String
)

private data class TrainingDay(
    val id: Int,
    val day: String,
    val title: String,
    val focus: String,
    val duration: String,
    val level: String,
    val exercises: List<Exercise>
)

private val basePlan = listOf(
    TrainingDay(1, "MON", "Chest + Triceps", "CHEST • SHOULDERS • TRICEPS", "30–40 min", "PUSH A", listOf(
        Exercise("Full push-up", 2, "3–6", 120, "Chest, triceps", "You can do 5–10 full reps consecutively. Start with 3–6 controlled reps per set and leave 2–3 good reps in reserve. If your form breaks, use an incline.", "standard push up beginner proper form"),
        Exercise("Incline push-up", 2, "8–12", 90, "Chest, triceps", "Use a stable counter. Lower slowly with a straight body, and choose an incline that makes the last reps challenging.", "incline push up proper form"),
        Exercise("Close-grip incline push-up", 2, "8–12", 90, "Triceps", "Bring hands slightly closer and use a comfortable elbow angle.", "close grip incline push up"),
        Exercise("Pike push-up progression", 2, "5–8", 120, "Shoulders", "Move head forward and down; use partial range if needed and avoid neck pressure.", "pike push up beginner")
    )),
    TrainingDay(2, "TUE", "Back + Biceps", "BACK • BICEPS • CORE", "30–35 min", "PULL A", listOf(
        Exercise("Self-resisted one-arm row", 3, "8–12 each", 90, "Back, biceps", "Resist your pulling arm with the other hand at the wrist. Pull elbow back slowly, keeping your torso stable.", "self resisted row no equipment"),
        Exercise("Prone W raise", 2, "10–15", 60, "Rear shoulders, upper back", "Lift elbows gently while face down. Keep neck neutral.", "prone W raise exercise"),
        Exercise("Self-resisted biceps curl", 3, "8–12 each", 90, "Biceps", "One hand resists the other throughout the curling movement. Keep it challenging without pain.", "self resisted biceps curl"),
        Exercise("Dead bug", 2, "8–12 each", 60, "Core", "Keep your trunk stable as the opposite arm and leg extend.", "dead bug exercise")
    )),
    TrainingDay(3, "THU", "Chest + Shoulders", "CHEST • SHOULDERS • TRICEPS", "30–40 min", "PUSH B", listOf(
        Exercise("Full push-up", 2, "3–6", 120, "Chest, triceps", "Repeat the full push-up at a controlled pace. Start with 3–6 clean reps per set and use incline push-ups if needed.", "standard push up proper form"),
        Exercise("Incline push-up", 2, "8–12", 90, "Chest", "Choose a counter or desk height that lets you train with good form throughout.", "incline push up proper form"),
        Exercise("Pike push-up progression", 2, "5–8", 120, "Shoulders", "Use an easy range first, then build difficulty gradually.", "beginner pike push up"),
        Exercise("Scapular push-up", 2, "10–15", 60, "Shoulder control", "Keep elbows straight; let shoulder blades move smoothly.", "scapular push up")
    )),
    TrainingDay(4, "SAT", "Back + Arms + Legs", "BACK • BICEPS • LIGHT LEGS", "30–40 min", "PULL B", listOf(
        Exercise("Self-resisted one-arm row", 3, "8–12 each", 90, "Back, biceps", "Use steady opposite-hand resistance; do not twist your torso.", "self resisted row no equipment"),
        Exercise("Prone reverse snow angel", 2, "10–15", 60, "Upper back", "Move arms slowly and keep neck relaxed.", "prone reverse snow angel exercise"),
        Exercise("Self-resisted biceps curl", 2, "8–12 each", 90, "Biceps", "Control both phases against firm hand resistance.", "self resisted biceps curl"),
        Exercise("Bodyweight squat", 2, "10–15", 90, "Legs, glutes", "Use comfortable depth with slow controlled repetitions.", "bodyweight squat proper form"),
        Exercise("Glute bridge", 2, "10–15", 75, "Glutes", "Lift hips without over-arching your back.", "glute bridge exercise")
    ))
)

private val pushupLevels = listOf("0–4", "5–10", "11–20", "21+")

private fun calibratedPlan(pushupLevel: String): List<TrainingDay> = basePlan.map { day ->
    if (day.id != 1 && day.id != 3) day
    else {
        val starter = day.exercises.first()
        val calibrated = when (pushupLevel) {
            "0–4" -> starter.copy(
                name = "Incline push-up",
                reps = "6–10",
                cue = "Use a sturdy wall or counter and leave 2–3 clean reps in reserve. Progress toward full push-ups gradually.",
                videoQuery = "beginner incline push up progression"
            )
            "11–20" -> starter.copy(
                name = "Full push-up",
                sets = 3,
                reps = "6–12",
                cue = "Build toward three sets of 12 clean full push-ups, keeping 2–3 repetitions in reserve."
            )
            "21+" -> starter.copy(
                name = "Controlled-tempo full push-up",
                sets = 3,
                reps = "8–15",
                cue = "Use a controlled lowering phase and a full comfortable range; do not chase failure.",
                videoQuery = "tempo push up proper form"
            )
            else -> starter // 5–10 consecutive full push-ups: start with 2 x 3–6
        }
        day.copy(exercises = listOf(calibrated) + day.exercises.drop(1))
    }
}

private fun pushupProgression(level: String): String = when (level) {
    "0–4" -> "Begin with 2 × 6–10 incline push-ups. Lower the incline gradually. Switch to the 5–10 level when you can complete at least 5 clean full push-ups."
    "5–10" -> "Start with 2 × 3–6 full push-ups on Monday and Thursday. Add a clean rep when possible. Once 2 × 6 is comfortable across two workouts, work toward 2 × 8–10. Re-test and choose 11–20 only after you can do at least 11 full reps in one clean set."
    "11–20" -> "Build from 3 × 6 toward 3 × 12 controlled full push-ups. When comfortable, try tempo reps, then update your level after re-testing your maximum."
    else -> "Aim for 3 × 8–15 controlled-tempo full push-ups, keeping clean form and around 2–3 reps in reserve. Progress gradually."
}

private fun todayIndex(): Int = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.MONDAY -> 0
    Calendar.TUESDAY -> 1
    Calendar.THURSDAY -> 2
    Calendar.SATURDAY -> 3
    else -> -1
}

private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

@Composable
fun GainTrackV2Screen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gaintrack_v2", 0) }
    var pushupLevel by rememberSaveable {
        mutableStateOf(prefs.getString("pushupLevel", "5–10")?.takeIf { it in pushupLevels } ?: "5–10")
    }
    val plan = remember(pushupLevel) { calibratedPlan(pushupLevel) }
    var tab by rememberSaveable { mutableStateOf(0) }
    var selectedDay by rememberSaveable { mutableIntStateOf(todayIndex().coerceAtLeast(0)) }
    var activeWorkout by rememberSaveable { mutableStateOf(false) }
    var doneKeys by remember { mutableStateOf(prefs.getString("completed", "") ?: "") }
    var checkedSets by rememberSaveable { mutableStateOf("") }
    var showProfile by rememberSaveable { mutableStateOf(false) }

    val doneSet = remember(doneKeys) { doneKeys.split(",").filter { it.isNotBlank() }.toSet() }
    val weekCount = remember(doneSet) {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        doneSet.count { it.substringBefore("|") >= monday }
    }
    val selected = plan[selectedDay]
    val exerciseDone = checkedSets.split(",").filter { it.isNotBlank() }.toSet()
    val setTotal = selected.exercises.sumOf { it.sets }
    val completedInSession = exerciseDone.size.coerceAtMost(setTotal)

    fun openVideo(query: String) {
        val url = "https://www.youtube.com/results?search_query=" + Uri.encode(query)
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun finishWorkout() {
        if (completedInSession != setTotal) return
        val key = todayKey() + "|" + selected.id
        if (key !in doneSet) {
            doneKeys = (doneSet + key).joinToString(",")
            prefs.edit().putString("completed", doneKeys).apply()
        }
        activeWorkout = false
        checkedSets = ""
        tab = 2
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (activeWorkout) {
                WorkoutSessionView(
                    day = selected,
                    checkedSets = exerciseDone,
                    onToggleSet = { key ->
                        checkedSets = if (key in exerciseDone) exerciseDone.minus(key).joinToString(",")
                        else (exerciseDone + key).joinToString(",")
                    },
                    onVideo = ::openVideo,
                    onBack = { activeWorkout = false },
                    onFinish = ::finishWorkout
                )
            } else {
                when (tab) {
                    0 -> HomeTab(
                        plan = plan,
                        pushupLevel = pushupLevel,
                        selectedDay = selectedDay,
                        doneSet = doneSet,
                        weekCount = weekCount,
                        onStart = { selectedDay = it; checkedSets = ""; activeWorkout = true },
                        onSelect = { selectedDay = it },
                        onPlan = { tab = 1 },
                        onProgress = { tab = 2 },
                        onProfile = { showProfile = true }
                    )
                    1 -> PlanTab(
                        plan = plan,
                        pushupLevel = pushupLevel,
                        selectedDay = selectedDay,
                        doneSet = doneSet,
                        onSelect = { selectedDay = it },
                        onStart = { selectedDay = it; checkedSets = ""; activeWorkout = true }
                    )
                    else -> ProgressTab(
                        doneSet = doneSet,
                        weekCount = weekCount,
                        onBackHome = { tab = 0 }
                    )
                }
                NavigationBar(containerColor = Panel, contentColor = Lime) {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Today") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.FitnessCenter, null) }, label = { Text("Plan") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Progress") })
                }
            }
        }
    }

    if (showProfile) {
        AlertDialog(
            onDismissRequest = { showProfile = false },
            containerColor = Panel,
            title = { Text("Your starting profile", color = White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("21 years • Male", color = White)
                    Text("Starting weight: 47 kg", color = White)
                    Text("Height: 167 cm", color = White)
                    Text("BMI: 16.9", color = Lime, fontWeight = FontWeight.Bold)
                    Text("Full push-up ability: $pushupLevel clean reps", color = Lime, fontWeight = FontWeight.Bold)
                    Text("Update your level when you can reliably do more clean full push-ups.", color = Muted, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pushupLevels.take(2).forEach { level ->
                            FilterChip(
                                selected = pushupLevel == level,
                                onClick = {
                                    pushupLevel = level
                                    prefs.edit().putString("pushupLevel", level).apply()
                                },
                                label = { Text("$level reps") }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pushupLevels.drop(2).forEach { level ->
                            FilterChip(
                                selected = pushupLevel == level,
                                onClick = {
                                    pushupLevel = level
                                    prefs.edit().putString("pushupLevel", level).apply()
                                },
                                label = { Text("$level reps") }
                            )
                        }
                    }
                    Text("Train with control and allow recovery. With a BMI under 18.5, consider a healthcare assessment if your weight is low or falling unexpectedly.", color = Muted, fontSize = 12.sp)
                }
            },
            confirmButton = { TextButton(onClick = { showProfile = false }) { Text("Close", color = Lime) } }
        )
    }
}

@Composable
private fun HomeTab(
    plan: List<TrainingDay>,
    pushupLevel: String,
    selectedDay: Int,
    doneSet: Set<String>,
    weekCount: Int,
    onStart: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    onPlan: () -> Unit,
    onProgress: () -> Unit,
    onProfile: () -> Unit
) {
    val isRestDay = todayIndex() < 0
    val today = plan[if (isRestDay) selectedDay else todayIndex()]
    val todayDone = doneSet.contains(todayKey() + "|" + today.id)
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("GAINTRACK", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("Build strength.\nBuild yourself.", color = White, fontSize = 29.sp, lineHeight = 33.sp, fontWeight = FontWeight.ExtraBold)
            }
            Surface(onClick = onProfile, shape = CircleShape, color = Panel2) {
                Icon(Icons.Default.Tune, "Profile", tint = Lime, modifier = Modifier.padding(13.dp).size(22.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF283C29), Color(0xFF172A2B), Color(0xFF172033))))
                .padding(20.dp)
        ) {
            Column {
                Text("YOUR PERSONAL START", color = Aqua, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier.fillMaxWidth()) {
                    ProfileMetric("47", "KG", Modifier.weight(1f))
                    ProfileMetric("167", "CM", Modifier.weight(1f))
                    ProfileMetric("21", "YEARS", Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Text("4-day no-equipment strength plan", color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Chest • Arms • Shoulders • Light legs", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("Full push-up level: $pushupLevel reps", color = Aqua, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(if (isRestDay) "RECOVERY DAY" else "TODAY'S SESSION", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("THIS WEEK $weekCount / 4", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Lime.copy(alpha = .13f)) {
                        Text(if (isRestDay) "REST" else today.day, color = Lime, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (isRestDay) "Recovery day" else today.title, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(if (isRestDay) "Eat enough, sleep and recover." else today.focus, color = Muted, fontSize = 10.sp, letterSpacing = .4.sp)
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Muted, modifier = Modifier.size(15.dp))
                    Text(if (isRestDay) "  No workout scheduled • Focus on recovery" else "  ${today.duration}  •  ${today.exercises.size} movements", color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { if (isRestDay) onPlan() else onStart(todayIndex()) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)
                ) {
                    Icon(if (todayDone) Icons.Default.RestartAlt else Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isRestDay) "VIEW WORKOUT PLAN" else if (todayDone) "REPEAT SESSION" else "START WORKOUT", fontWeight = FontWeight.ExtraBold, letterSpacing = .6.sp)
                }
                if (todayDone && !isRestDay) Text("Session completed today — repeat only if you feel recovered.", color = Aqua, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("YOUR 4-DAY SPLIT", color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onPlan) { Text("View plan", color = Lime) }
        }
        plan.forEachIndexed { index, day ->
            DayRow(day = day, isSelected = selectedDay == index, isDone = doneSet.contains(todayKey() + "|" + day.id), onClick = { onSelect(index) })
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(16.dp))
        Surface(shape = RoundedCornerShape(18.dp), color = Panel2) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SelfImprovement, null, tint = Aqua, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Recovery is part of the plan", color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Recover on Wednesday, Friday and Sunday. Muscles rebuild between sessions.", color = Muted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileMetric(value: String, label: String, modifier: Modifier) {
    Column(modifier) {
        Text(value, color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Aqua, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun DayRow(day: TrainingDay, isSelected: Boolean, isDone: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = if (isSelected) Color(0xFF26362B) else Panel, border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Lime.copy(alpha = .6f) else Line)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(day.day, color = if (isSelected) Lime else Muted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(42.dp))
            Column(Modifier.weight(1f)) {
                Text(day.title, color = White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(day.focus, color = Muted, fontSize = 9.sp, letterSpacing = .3.sp)
            }
            if (isDone) Icon(Icons.Default.CheckCircle, null, tint = Aqua, modifier = Modifier.size(19.dp))
            else Text(day.duration, color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun PlanTab(plan: List<TrainingDay>, pushupLevel: String, selectedDay: Int, doneSet: Set<String>, onSelect: (Int) -> Unit, onStart: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("THE PROGRAM", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text("Your 4-day split", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Calibrated for $pushupLevel consecutive clean full push-ups. Tap your profile on Today to update the level as you get stronger.", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        plan.forEachIndexed { index, day ->
            DayRow(day, selectedDay == index, doneSet.contains(todayKey() + "|" + day.id)) { onSelect(index) }
            if (selectedDay == index) {
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(20.dp), color = Panel2) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${day.level}  •  ${day.duration}", color = Aqua, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        day.exercises.forEachIndexed { i, ex ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${i + 1}", color = Lime, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(ex.name, color = White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("${ex.sets} sets × ${ex.reps} • ${ex.target}", color = Muted, fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onStart(index) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink), shape = RoundedCornerShape(14.dp)) {
                            Icon(Icons.Default.PlayArrow, null)
                            Text("Start this session", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text("Progression rule", color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(pushupProgression(pushupLevel) + " Keep about 2–3 clean reps in reserve and avoid sharp pain.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun WorkoutSessionView(
    day: TrainingDay,
    checkedSets: Set<String>,
    onToggleSet: (String) -> Unit,
    onVideo: (String) -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val total = day.exercises.sumOf { it.sets }
    val completed = checkedSets.size.coerceAtMost(total)
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = White) }
            Column(Modifier.weight(1f)) {
                Text("WORKOUT MODE", color = Lime, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(day.title, color = White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            }
            Text("$completed/$total", color = Aqua, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(6.dp).clip(CircleShape).background(Panel2)) {
            Box(Modifier.fillMaxWidth(completed.toFloat() / total.coerceAtLeast(1)).fillMaxHeight().clip(CircleShape).background(Lime))
        }
        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(day.exercises) { index, ex ->
                Surface(shape = RoundedCornerShape(20.dp), color = Panel) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Lime.copy(alpha = .14f)) {
                                Text("${index + 1}", color = Lime, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(ex.name, color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(ex.target, color = Aqua, fontSize = 11.sp)
                            }
                            Text("${ex.sets} × ${ex.reps}", color = Lime, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(ex.cue, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = Muted, modifier = Modifier.size(14.dp))
                            Text("  Rest ${ex.rest}s between sets", color = Muted, fontSize = 10.sp)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { onVideo(ex.videoQuery) }, contentPadding = PaddingValues(horizontal = 6.dp)) {
                                Icon(Icons.Default.OpenInNew, null, tint = Aqua, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Video guide", color = Aqua, fontSize = 11.sp)
                            }
                        }
                        ex.sets.let { count ->
                            repeat(count) { set ->
                                val key = "${day.id}-${index}-${set}"
                                val isChecked = key in checkedSets
                                Surface(
                                    onClick = { onToggleSet(key) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = if (isChecked) Color(0xFF263A2B) else Panel2,
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                                ) {
                                    Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(if (isChecked) Icons.Default.CheckCircle else Icons.Default.Check, null, tint = if (isChecked) Lime else Muted, modifier = Modifier.size(17.dp))
                                        Spacer(Modifier.width(9.dp))
                                        Text("Set ${set + 1}", color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.weight(1f))
                                        Text(if (isChecked) "Completed" else "${ex.reps} reps", color = if (isChecked) Aqua else Muted, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Surface(color = Panel) {
            Column(Modifier.padding(16.dp)) {
                Text("$completed of $total sets completed", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onFinish, enabled = completed == total, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)) {
                    Text("FINISH SESSION", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun ProgressTab(doneSet: Set<String>, weekCount: Int, onBackHome: () -> Unit) {
    val total = doneSet.size
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("YOUR CONSISTENCY", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Text("Progress", color = White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text("Small improvements, repeated every week.", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProgressCard("TOTAL SESSIONS", total.toString(), Modifier.weight(1f))
            ProgressCard("THIS WEEK", weekCount.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Surface(shape = RoundedCornerShape(22.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("YOUR LAST 12 WEEKS", color = Aqua, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                repeat(12) { week ->
                    val startOfWeek = Calendar.getInstance().apply {
                        firstDayOfWeek = Calendar.MONDAY
                        val daysSinceMonday = (get(Calendar.DAY_OF_WEEK) + 5) % 7
                        add(Calendar.DAY_OF_YEAR, -daysSinceMonday - 7 * week)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val endOfWeek = (startOfWeek.clone() as Calendar).apply {
                        add(Calendar.DAY_OF_YEAR, 7)
                    }
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val count = doneSet.count { key ->
                        val date = runCatching { format.parse(key.substringBefore("|")) }.getOrNull()
                        date != null && !date.before(startOfWeek.time) && date.before(endOfWeek.time)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(if (week == 0) "Now" else "-$week", color = Muted, fontSize = 10.sp, modifier = Modifier.width(32.dp))
                        Box(Modifier.weight(1f).height(7.dp).clip(CircleShape).background(Panel2)) {
                            if (count > 0) Box(Modifier.fillMaxWidth((count / 4f).coerceIn(0f, 1f)).fillMaxHeight().background(Lime))
                        }
                        Text("$count/4", color = Muted, fontSize = 10.sp, modifier = Modifier.width(34.dp))
                    }
                }
                Text("Your completed sessions are saved on this phone. The weekly view starts filling as you train.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 10.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        Surface(shape = RoundedCornerShape(20.dp), color = Panel2) {
            Column(Modifier.padding(16.dp)) {
                Text("How to progress", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("1. Keep every rep controlled.\n2. Add reps before choosing a harder variation.\n3. Rest between sessions for the same muscles.\n4. Stop for sharp pain, dizziness or unusual symptoms.", color = Muted, fontSize = 12.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onBackHome, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Lime)) {
            Text("Back to today")
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ProgressCard(label: String, value: String, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(18.dp), color = Panel) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = .7.sp)
            Text(value, color = Lime, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
