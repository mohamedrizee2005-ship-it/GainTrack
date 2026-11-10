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

private val plan = listOf(
    TrainingDay(1, "MON", "Chest + Triceps", "CHEST • TRICEPS • FRONT SHOULDERS", "30–35 min", "PUSH A", listOf(
        Exercise("Incline push-up", 3, "8–12", 75, "Chest, triceps", "Hands on a stable wall or counter. Keep your body straight and lower your chest between your hands.", "incline push up proper form beginner"),
        Exercise("Knee push-up", 3, "6–10", 90, "Chest, triceps", "Keep a straight line from knees to head. Lower under control; do not let your hips sag.", "knee push up proper form"),
        Exercise("Close-grip knee push-up", 2, "6–10", 75, "Triceps, inner chest", "Hands just inside shoulder width, elbows angled back. Use an easier incline if needed.", "close grip knee push up triceps form"),
        Exercise("Pike hold", 2, "20–30 sec", 60, "Shoulders", "Push the floor away and keep your neck relaxed. Stop if you feel shoulder pain.", "pike push up hold shoulder exercise")
    )),
    TrainingDay(2, "TUE", "Back + Biceps + Forearms", "BACK • BICEPS • GRIP", "30–35 min", "PULL A", listOf(
        Exercise("Prone W raise", 3, "10–14", 60, "Upper back, rear shoulders", "Lie face down, elbows bent in a W. Lift hands and elbows gently; squeeze shoulder blades.", "prone W raise exercise form"),
        Exercise("Prone reverse snow angel", 3, "8–12", 60, "Upper back, rear shoulders", "Keep chest lightly lifted and sweep arms slowly without shrugging.", "prone reverse snow angel exercise"),
        Exercise("Self-resisted biceps curl", 3, "8–12 each arm", 60, "Biceps", "One hand resists the other arm as it curls up and lowers slowly. Make the working arm do the work.", "self resisted biceps curl exercise"),
        Exercise("Palm-down wrist curl (self-resisted)", 2, "10–15 each", 45, "Forearms", "Use your opposite hand to provide gentle resistance through wrist movement.", "self resisted wrist extension flexion exercise"),
        Exercise("Finger spread + fist", 2, "15–20", 30, "Hands, finger control", "Spread fingers wide, hold briefly, then close into a relaxed fist. Do not squeeze painfully.", "finger extension finger spread hand exercise")
    )),
    TrainingDay(3, "WED", "Legs + Core + Mobility", "LEGS • CORE • RECOVERY SUPPORT", "25–30 min", "FOUNDATION", listOf(
        Exercise("Bodyweight squat", 3, "10–15", 75, "Quads, glutes", "Sit hips back, keep feet planted and knees tracking over toes.", "bodyweight squat proper form"),
        Exercise("Reverse lunge", 2, "8 each leg", 75, "Legs, glutes", "Step back softly and lower only as far as comfortable. Hold a wall for balance if needed.", "reverse lunge beginner proper form"),
        Exercise("Glute bridge", 3, "12–15", 60, "Glutes, hips", "Drive through your feet, squeeze glutes at the top and avoid over-arching your back.", "glute bridge proper form"),
        Exercise("Dead bug", 3, "8 each side", 45, "Core", "Keep lower back gently against the floor while moving opposite arm and leg.", "dead bug exercise proper form"),
        Exercise("Bird dog", 2, "8 each side", 45, "Core, back stability", "Reach long without rotating your hips; move slowly.", "bird dog exercise proper form")
    )),
    TrainingDay(4, "THU", "Chest + Shoulders", "CHEST • SHOULDERS • TRICEPS", "30–35 min", "PUSH B", listOf(
        Exercise("Standard push-up progression", 3, "5–10", 90, "Chest, triceps", "Choose wall, incline, knee or full push-up so every rep stays controlled.", "push up progression beginner proper form"),
        Exercise("Wide incline push-up", 2, "8–12", 75, "Chest", "Use a comfortable wider hand position; keep elbows below shoulder height.", "wide incline push up proper form"),
        Exercise("Pike push-up (incline or partial)", 2, "5–8", 90, "Shoulders, triceps", "Move head forward and down between hands. Use a small range at first.", "beginner pike push up tutorial"),
        Exercise("Scapular push-up", 2, "10–12", 45, "Shoulder control, serratus", "Keep elbows straight; let chest sink slightly, then push the floor away.", "scapular push up tutorial")
    )),
    TrainingDay(5, "FRI", "Arms + Back + Full Body", "BICEPS • TRICEPS • FOREARMS • BACK", "30–35 min", "PULL B + ARMS", listOf(
        Exercise("Self-resisted biceps curl", 3, "10–12 each arm", 60, "Biceps", "Resist with the opposite hand on both the lifting and lowering phase.", "self resisted biceps curl exercise"),
        Exercise("Self-resisted triceps extension", 3, "10–12 each arm", 60, "Triceps", "Use your other hand to resist as you straighten the working elbow. Keep shoulder relaxed.", "self resisted triceps extension exercise"),
        Exercise("Prone Y-T raise", 2, "8 each shape", 60, "Upper back, rear shoulders", "Use small, slow lifts; keep forehead down and avoid shrugging.", "prone Y T raise exercise"),
        Exercise("Wrist flexion + extension", 2, "12 each direction", 45, "Forearms, wrists", "Use the opposite palm for light resistance. Move only through a pain-free range.", "self resisted wrist strengthening exercise"),
        Exercise("Wall push-up hold", 2, "20–30 sec", 45, "Chest, triceps, wrists", "Hold a strong straight-body position against the wall; keep wrists comfortable.", "wall push up isometric hold")
    ))
)

private fun todayIndex(): Int {
    val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return when (day) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        else -> 0
    }
}

private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

@Composable
fun GainTrackV2Screen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gaintrack_v2", 0) }
    var tab by rememberSaveable { mutableStateOf(0) }
    var selectedDay by rememberSaveable { mutableIntStateOf(todayIndex()) }
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
                    Text("Your plan prioritises gradual strength progress, clean technique and recovery. A BMI below 18.5 is considered underweight; consider a healthcare check-in, especially if your weight has been falling without trying.", color = Muted, fontSize = 12.sp)
                }
            },
            confirmButton = { TextButton(onClick = { showProfile = false }) { Text("Close", color = Lime) } }
        )
    }
}

@Composable
private fun HomeTab(
    selectedDay: Int,
    doneSet: Set<String>,
    weekCount: Int,
    onStart: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    onPlan: () -> Unit,
    onProgress: () -> Unit,
    onProfile: () -> Unit
) {
    val today = plan[selectedDay]
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
                Text("5-day bodyweight strength plan", color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Chest • Arms • Shoulders • Full body", color = Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("TODAY'S SESSION", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("WEEK $weekCount / 5", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Lime.copy(alpha = .13f)) {
                        Text(today.day, color = Lime, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(today.title, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(today.focus, color = Muted, fontSize = 10.sp, letterSpacing = .4.sp)
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Muted, modifier = Modifier.size(15.dp))
                    Text("  ${today.duration}  •  ${today.exercises.size} movements", color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onStart(selectedDay) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)
                ) {
                    Icon(if (todayDone) Icons.Default.RestartAlt else Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (todayDone) "REPEAT SESSION" else "START WORKOUT", fontWeight = FontWeight.ExtraBold, letterSpacing = .6.sp)
                }
                if (todayDone) Text("Session completed today — repeat only if you feel recovered.", color = Aqua, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("YOUR 5-DAY SPLIT", color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
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
                    Text("Rest on Saturday and Sunday. Muscles adapt between sessions.", color = Muted, fontSize = 12.sp)
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
private fun PlanTab(selectedDay: Int, doneSet: Set<String>, onSelect: (Int) -> Unit, onStart: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("THE PROGRAM", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text("Your 5-day split", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("A balanced beginner plan with extra attention to chest, arms and shoulders.", color = Muted, fontSize = 13.sp)
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
        Text("Start with the easiest variation that lets you complete every rep with good form. When you reach the top of the rep range on every set comfortably, move to a slightly harder variation or slow the lowering phase. Finish most sets with about 2–3 good reps still possible; do not force painful reps.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
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
                Button(onClick = onFinish, enabled = completed > 0, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)) {
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
                Text("12-WEEK STRENGTH JOURNEY", color = Aqua, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                repeat(12) { week ->
                    val count = doneSet.count { key ->
                        val date = key.substringBefore("|")
                        val parsed = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date) }.getOrNull()
                        if (parsed == null) false else {
                            val cal = Calendar.getInstance().apply { time = parsed }
                            val now = Calendar.getInstance()
                            cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && week == 0
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("W${week + 1}", color = Muted, fontSize = 10.sp, modifier = Modifier.width(32.dp))
                        Box(Modifier.weight(1f).height(7.dp).clip(CircleShape).background(Panel2)) {
                            if (week == 0 && count > 0) Box(Modifier.fillMaxWidth((count / 5f).coerceIn(0f, 1f)).fillMaxHeight().background(Lime))
                        }
                        Text(if (week == 0) "$count/5" else "—", color = Muted, fontSize = 10.sp, modifier = Modifier.width(34.dp))
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
