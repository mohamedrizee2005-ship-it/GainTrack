package com.gaintrack.app.ui.screens.nutrition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaintrack.app.GainTrackApp
import com.gaintrack.app.data.local.SecureGeminiSettings
import com.gaintrack.app.data.local.entity.MealLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val NB = Color(0xFF080B13)
private val NP = Color(0xFF141B2A)
private val NT = Color(0xFFF4F7FB)
private val NM = Color(0xFF9AA8BA)
private val NG = Color(0xFFB7F36B)
private val NA = Color(0xFF55D6C2)

private data class Estimate(val name:String,val portion:String,val kcal:Int,val protein:Double,val carbs:Double,val fat:Double,val note:String,val question:String = "")
private fun foodToday() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

@Composable
fun NutritionDashboard(
    app: GainTrackApp,
    apiKey: String,
    model: String,
    onSaveKey: (String) -> Boolean,
    onForgetKey: () -> Unit,
    onModelChange: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val meals by app.nutritionRepository.getMealsForDate(foodToday()).collectAsState(initial = emptyList())
    val profile by app.userRepository.userProfile.collectAsState(initial = null)
    val calorieTarget = profile?.calorieTarget ?: 2100
    val proteinTarget = profile?.proteinTarget ?: 85
    val total = meals.sumOf { it.calories }
    val protein = meals.sumOf { it.proteinGrams }
    val carbs = meals.sumOf { it.carbsGrams }
    val fat = meals.sumOf { it.fatGrams }
    var mealType by remember { mutableStateOf("Lunch") }
    var portion by remember { mutableStateOf("Medium serving") }
    var ingredients by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(false) }
    var draftKey by remember { mutableStateOf("") }
    var settingsError by remember { mutableStateOf("") }
    var manual by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var kcal by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    var f by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var estimate by remember { mutableStateOf<Estimate?>(null) }
    var lastBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var clarification by remember { mutableStateOf("") }

    fun save(item: Estimate) {
        scope.launch {
            app.nutritionRepository.logMeal(mealType = mealType, foodName = item.name + " (" + item.portion + ")",
                servings = 1.0, calories = item.kcal, proteinGrams = item.protein, carbsGrams = item.carbs, fatGrams = item.fat)
            estimate = null
            clarification = ""
            ingredients = ""
            status = "Saved to today's food diary."
        }
    }
    fun analyse(bitmap: Bitmap, overrideKey: String? = null) {
        lastBitmap = bitmap
        val selectedKey = overrideKey ?: apiKey
        if (selectedKey.isBlank()) {
            status = "Add your Gemini API key in AI Settings to analyse photos. Manual logging is free."
            draftKey = ""
            settings = true
            return
        }
        busy = true
        status = "Analysing photo. This can take up to 40 seconds…"
        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val stream = ByteArrayOutputStream()
                    val scale = minOf(1f, 960f / maxOf(bitmap.width, bitmap.height))
                    val uploadBitmap = if (scale < 1f) Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true) else bitmap
                    uploadBitmap.compress(Bitmap.CompressFormat.JPEG, 76, stream)
                    val encoded = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                    val prompt = """You estimate nutrition for Sri Lankan food photos. Identify visible foods and estimate edible portions. Optional ingredients: ${ingredients.ifBlank { "not provided" }}. Portion hint: $portion. User clarification: $clarification. Return ONLY JSON keys food_name, portion, calories, protein_g, carbs_g, fat_g, uncertainty_note, clarifying_question. If a key ingredient or portion is important and cannot be inferred, ask one short clarifying question; otherwise return an empty clarifying_question. Use reasonable food-composition estimates. Do not claim exactness. If milk powder, sugar, oil or hidden ingredients cannot be seen, state uncertainty and give a broad estimate."""
                    val parts = JSONArray().put(JSONObject().put("text", prompt))
                        .put(JSONObject().put("inline_data", JSONObject().put("mime_type", "image/jpeg").put("data", encoded)))
                    val body = JSONObject().put("contents", JSONArray().put(JSONObject().put("parts", parts)))
                        .put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
                    val conn = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"; conn.connectTimeout = 12000; conn.readTimeout = 30000; conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("x-goog-api-key", selectedKey.trim())
                    conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                    val code = conn.responseCode
                    val response = (if (code in 200..299) conn.inputStream else conn.errorStream)
                        ?.bufferedReader()?.use { it.readText() }.orEmpty()
                    conn.disconnect()
                    if (code !in 200..299) {
                        val explanation = when (code) {
                            400 -> "Invalid photo request or API configuration."
                            401, 403 -> "API key not authorized. Create a new key in Google AI Studio and check its project restrictions."
                            404 -> "The selected Gemini model is unavailable for this API key/project."
                            429 -> "Gemini API quota or rate limit reached. Check usage limits in Google AI Studio."
                            500, 502, 503, 504 -> "Google's service is temporarily unavailable. Try again shortly."
                            else -> "Request failed (HTTP $code). Check your Gemini API project and try again."
                        }
                        error("AI error $code: $explanation")
                    }
                    val responseJson = JSONObject(response)
                    val candidates = responseJson.optJSONArray("candidates")
                    if (candidates == null || candidates.length() == 0) {
                        val blockReason = responseJson.optJSONObject("promptFeedback")?.optString("blockReason").orEmpty()
                        error("Gemini returned no food estimate. " + blockReason.ifBlank { "Try another clear photo." })
                    }
                    val responseParts = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
                        ?: error("Gemini returned an empty response. Try a different photo.")
                    val answer = buildString {
                        for (index in 0 until responseParts.length()) {
                            val textPart = responseParts.optJSONObject(index)?.optString("text").orEmpty()
                            if (textPart.isNotBlank()) append(textPart)
                        }
                    }
                    if (answer.isBlank()) error("Gemini did not return a food estimate. Try a different photo.")
                    val json = JSONObject(answer.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())
                    Estimate(json.optString("food_name","Food"),json.optString("portion","Estimated serving"),json.optInt("calories",0),
                        json.optDouble("protein_g",0.0),json.optDouble("carbs_g",0.0),json.optDouble("fat_g",0.0),json.optString("uncertainty_note","Photo-based estimate."),json.optString("clarifying_question",""))
                }
                estimate = result
                status = "Review the AI estimate before saving."
            } catch (e: Exception) {
                status = friendlyGeminiError(e)
            }
            finally { busy = false }
        }
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { val bmp = context.contentResolver.openInputStream(it)?.use { stream -> BitmapFactory.decodeStream(stream) }; if (bmp != null) analyse(bmp) else status = "Could not read image." }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp -> if (bmp != null) analyse(bmp) }

    Column(Modifier.fillMaxSize().background(NB)) {
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("NUTRITION COACH", color=NG, fontSize=11.sp, fontWeight=FontWeight.Bold); Text("Fuel your progress.", color=NT, fontSize=26.sp, fontWeight=FontWeight.ExtraBold); Text("Sri Lankan food • daily guidance", color=NM, fontSize=12.sp) }
                    TextButton(onClick={
                        draftKey = ""
                        settingsError = ""
                        settings = true
                    }) { Text("AI SETTINGS", color=NG) }
                }
            }
            item {
                Card(colors=CardDefaults.cardColors(containerColor=NP), shape=RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(17.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        Text("TODAY'S INTAKE", color=NM, fontSize=11.sp, fontWeight=FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                            Column { Text("$total", color=NT, fontSize=32.sp, fontWeight=FontWeight.Bold); Text("of $calorieTarget kcal", color=NM) }
                            Column(horizontalAlignment=Alignment.End) { Text("${(calorieTarget-total).coerceAtLeast(0)}", color=NG, fontSize=27.sp, fontWeight=FontWeight.Bold); Text("remaining", color=NM) }
                        }
                        LinearProgressIndicator(progress={ (total.toFloat()/calorieTarget.coerceAtLeast(1)).coerceIn(0f,1f) }, modifier=Modifier.fillMaxWidth().height(8.dp), color=NG, trackColor=Color(0xFF30394A))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
                            Macro("Protein","%.1fg".format(protein),"/ $proteinTarget g",NA)
                            Macro("Carbs","%.1fg".format(carbs),"today",NT)
                            Macro("Fat","%.1fg".format(fat),"today",NT)
                        }
                    }
                }
            }
            item {
                Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF172B2A)), shape=RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(15.dp), verticalArrangement=Arrangement.spacedBy(9.dp)) {
                        Text("AI FOOD CAMERA", color=NG, fontWeight=FontWeight.Bold)
                        Text("Take a photo or upload one. Add ingredients and portion details if you know them; otherwise leave them blank. AI will ask for clarification when needed.", color=NT, fontSize=13.sp)
                        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                            listOf("Breakfast","Lunch","Dinner","Snack").forEach { label -> FilterChip(selected=mealType==label,onClick={mealType=label},label={Text(label,fontSize=11.sp)}) }
                        }
                        OutlinedTextField(value=portion,onValueChange={portion=it},label={Text("Portion (optional)")},placeholder={Text("One medium bowl")},modifier=Modifier.fillMaxWidth(),singleLine=true)
                        OutlinedTextField(value=ingredients,onValueChange={ingredients=it},label={Text("Ingredients / brand (optional)")},placeholder={Text("3 tbsp milk powder + 2 tsp sugar")},modifier=Modifier.fillMaxWidth(),minLines=2)
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            Button(onClick={camera.launch(null)},enabled=!busy,modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=NG,contentColor=NB)) { Icon(Icons.Default.CameraAlt,null); Text(" Camera") }
                            OutlinedButton(onClick={gallery.launch("image/*")},enabled=!busy,modifier=Modifier.weight(1f)) { Icon(Icons.Default.Image,null); Text(" Gallery") }
                        }
                        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth(),color=NG)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (model == SecureGeminiSettings.FREE_MODEL) "Flash-Lite · Free" else "Flash · Quality",
                                color = NM,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(enabled = !busy, onClick = {
                                busy = true
                                status = "Checking Gemini connection…"
                                scope.launch {
                                    status = withContext(Dispatchers.IO) {
                                        try { checkGeminiConnection(apiKey, model) }
                                        catch (e: Exception) { friendlyGeminiError(e) }
                                    }
                                    busy = false
                                }
                            }) { Text("Test AI connection", color = NG, fontSize = 11.sp) }
                        }
                        if (status.isNotBlank()) {
                            val failed = status.startsWith("AI error") || status.startsWith("Gemini returned") || status.startsWith("Gemini did not")
                            Text(status,color=if (failed) Color(0xFFFFB4AB) else NM,fontSize=13.sp)
                            if (failed && lastBitmap != null && !busy) {
                                TextButton(onClick={lastBitmap?.let { analyse(it) }}) { Text("Retry this photo", color=NG) }
                            }
                        }
                    }
                }
            }
            if (estimate != null) item {
                val e=estimate!!
                Card(colors=CardDefaults.cardColors(containerColor=NP),shape=RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text("REVIEW ESTIMATE",color=NG,fontWeight=FontWeight.Bold)
                        Text(e.name,color=NT,fontSize=19.sp,fontWeight=FontWeight.Bold); Text(e.portion,color=NM)
                        Text("${e.kcal} kcal  •  Protein ${"%.1f".format(e.protein)} g  •  Carbs ${"%.1f".format(e.carbs)} g  •  Fat ${"%.1f".format(e.fat)} g",color=NT)
                        Text(e.note,color=NM,fontSize=12.sp)
                        if (e.question.isNotBlank()) {
                            Text("Quick question: " + e.question,color=NA,fontWeight=FontWeight.SemiBold,fontSize=13.sp)
                            OutlinedTextField(value=clarification,onValueChange={clarification=it},label={Text("Your answer (optional)")},modifier=Modifier.fillMaxWidth())
                            TextButton(onClick={lastBitmap?.let { ingredients = (ingredients + " " + clarification).trim(); clarification = ""; analyse(it) }}) { Text("Update estimate with my answer",color=NG) }
                        }
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            Button(onClick={save(e)},modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=NG,contentColor=NB)){Text("Add to diary")}
                            OutlinedButton(onClick={estimate=null}){Text("Discard")}
                        }
                    }
                }
            }
            item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){ Text("TODAY'S FOOD DIARY",color=NT,fontWeight=FontWeight.Bold); TextButton(onClick={manual=true}){Text("+ Add manually",color=NG)} } }
            if (meals.isEmpty()) item { Text("No meals logged yet. Capture your first meal or add it manually.",color=NM) }
            items(meals,key={it.id}) { meal -> MealItem(meal){ scope.launch { app.nutritionRepository.deleteMeal(meal) } } }
            item { CoachAdvice(meals,total,calorieTarget,protein,proteinTarget) }
            item { Text("Photo estimates are approximate. Oil, ingredients and portion sizes can change calories. Review before saving.",color=NM,fontSize=11.sp) }
        }
    }
    if (settings) AlertDialog(
        onDismissRequest = { settings = false },
        containerColor = NP,
        title = { Text("Gemini AI settings", color = NT) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    if (apiKey.isBlank()) "No key saved. Enter your new Gemini key once and save it securely on this phone."
                    else "Your key is saved securely on this phone. Leave the field empty to keep it, or type a new key to replace it.",
                    color = NM, fontSize = 12.sp
                )
                OutlinedTextField(
                    value = draftKey,
                    onValueChange = { draftKey = it },
                    label = { Text("New API key (optional when saved)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Text("Gemini model", color = NT, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    FilterChip(
                        selected = model == SecureGeminiSettings.FREE_MODEL,
                        onClick = { onModelChange(SecureGeminiSettings.FREE_MODEL) },
                        label = { Text("Flash-Lite · Free", fontSize = 10.sp) }
                    )
                    FilterChip(
                        selected = model == SecureGeminiSettings.QUALITY_MODEL,
                        onClick = { onModelChange(SecureGeminiSettings.QUALITY_MODEL) },
                        label = { Text("Flash · Quality", fontSize = 10.sp) }
                    )
                }
                if (apiKey.isNotBlank()) {
                    TextButton(onClick = {
                        onForgetKey()
                        draftKey = ""
                        settingsError = "The saved API key was cleared."
                    }) { Text("Forget saved key", color = Color(0xFFFFB4AB)) }
                }
                if (settingsError.isNotBlank()) {
                    Text(settingsError, color = Color(0xFFFFB4AB), fontSize = 12.sp)
                }
                Text(
                    "The key is encrypted with Android Keystore and stays on this phone. Uninstalling the app removes it. Free requests still have Google project limits.",
                    color = NM, fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val candidate = draftKey.trim().ifBlank { apiKey }
                if (candidate.isBlank()) {
                    settingsError = "Enter a Gemini API key first."
                } else if (draftKey.isNotBlank() && !onSaveKey(candidate)) {
                    settingsError = "Could not securely save this key. Please retry."
                } else {
                    settings = false
                    settingsError = ""
                    draftKey = ""
                    lastBitmap?.let { if (!busy) analyse(it, candidate) }
                }
            }) { Text(if (lastBitmap == null) "Save settings" else "Save & analyse", color = NG) }
        },
        dismissButton = { TextButton(onClick = { settings = false }) { Text("Cancel") } }
    )
    if(manual) AlertDialog(onDismissRequest={manual=false},containerColor=NP,title={Text("Add food manually",color=NT)},
        text={Column(verticalArrangement=Arrangement.spacedBy(6.dp)){
            OutlinedTextField(value=name,onValueChange={name=it},label={Text("Food name")},modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=kcal,onValueChange={kcal=it.filter(Char::isDigit)},label={Text("Calories kcal")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=p,onValueChange={p=it},label={Text("Protein g")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=c,onValueChange={c=it},label={Text("Carbs g")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=f,onValueChange={f=it},label={Text("Fat g")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.fillMaxWidth())
        }},
        confirmButton={TextButton(onClick={save(Estimate(name.ifBlank{"Food"},"User entered",kcal.toIntOrNull()?:0,p.toDoubleOrNull()?:0.0,c.toDoubleOrNull()?:0.0,f.toDoubleOrNull()?:0.0,"Values entered by you."));manual=false;name="";kcal="";p="";c="";f=""}){Text("Save",color=NG)}},
        dismissButton={TextButton(onClick={manual=false}){Text("Cancel")}})
}

@Composable private fun Macro(label:String,value:String,target:String,color:Color){Column{Text(label,color=NM,fontSize=10.sp);Text(value,color=color,fontSize=16.sp,fontWeight=FontWeight.Bold);Text(target,color=NM,fontSize=10.sp)}}
@Composable private fun MealItem(meal:MealLogEntity,onDelete:()->Unit){
    Card(colors=CardDefaults.cardColors(containerColor=NP),shape=RoundedCornerShape(13.dp)){Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){
        Icon(Icons.Default.Restaurant,null,tint=NG);Spacer(Modifier.width(9.dp));Column(Modifier.weight(1f)){Text(meal.foodName,color=NT,fontWeight=FontWeight.SemiBold);Text("${meal.mealType} • ${meal.calories} kcal • ${"%.1f".format(meal.proteinGrams)} g protein",color=NM,fontSize=11.sp)}
        IconButton(onClick=onDelete){Icon(Icons.Default.Delete,null,tint=NM)}
    }}
}
@Composable private fun CoachAdvice(meals:List<MealLogEntity>,total:Int,target:Int,protein:Double,proteinTarget:Int){
    val remain=(target-total).coerceAtLeast(0);val pleft=(proteinTarget-protein).coerceAtLeast(0.0)
    val names=meals.joinToString(" ") { it.foodName }.lowercase()
    val treat=listOf("soft drink","soda","sweet drink","cake","biscuits","chocolate","short eats","roll","pastry","fried","chips","fries").any { it in names }
    val base=when{total>target+500->"You are above today's estimated target. No need to skip meals; return to your usual balanced eating pattern.";pleft>25&&remain>250->"Consider eggs, milk, dhal, chickpeas, fish or curd for protein, with rice, oats or banana for energy.";remain>450->"You still have energy to add today. Try rice with dhal and egg, milk with banana, or chickpeas.";pleft>10->"Protein is a little short. Consider milk, eggs, fish, chicken, dhal or green gram.";else->"Nice work logging your food. Keep meals varied and aim for steady progress, not perfection."}
    val advice=if(treat) "A treat or fried/sugary food is okay sometimes. For your next meal, balance it with protein, vegetables and fruit; choose eggs, dhal, fish, chickpeas or plain milk more often. " + base else base
    Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF172B2A)),shape=RoundedCornerShape(17.dp)){Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text("YOUR DAILY COACH",color=NG,fontWeight=FontWeight.Bold,fontSize=11.sp);Text(advice,color=NT,fontSize=14.sp);Text("General guidance based on your logged estimates and profile targets, not medical advice.",color=NM,fontSize=10.sp)}}
}
