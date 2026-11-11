# GainTrack v3.0 — Focused No-Equipment Muscle Coach

**Current workout plan (v3):** Monday (chest/triceps), Tuesday (back/biceps/core), Thursday (chest/shoulders), Saturday (back/arms/light legs). **Rest days:** Wednesday, Friday, Sunday. The Android app retains its nutrition diary, photo-based meal estimates, and progress screens.

**How to build:** The [GitHub Actions workflow](../../actions/workflows/build-apk.yml) produces a debug APK artifact. Install on an Android device only after reviewing the build and app permissions. This is a development/debug build, not a production-signed Play Store release.

**Fitness note:** For a 47 kg adult aiming to gain weight, the food log's 2100 kcal and 85 g protein entries are starting estimates, not measured energy needs or a guaranteed prescription. Track weekly average body weight and adjust with care. The no-equipment self-resisted and floor-based back exercises are inherently harder to load progressively than safe equipment-based rows. Seek medical advice about persistent underweight or unintended weight loss.

**Workout changes in v3:** Four spaced sessions, easier-to-follow push-up progressions, controlled self-resisted arm/back training, more recovery, and real 12-week workout completion history. A session is marked complete after all planned sets are checked.

---

## Legacy v1/v2 documentation below
The original four-day plan below used a backpack and the later v2 code used five days. The v3 workout schedule above is authoritative for this branch.

# GainTrack – Muscle & Weight Gain Tracker

A native Android application architected for healthy, progressive weight and muscle gain at home using bodyweight and household backpack resistance. Preloaded with affordable Sri Lankan nutrition and safe, gradual progress tracking (+0.1 to 0.25 kg/week).

---

## 📱 Default User Profile & Initial Calibration
- **Current Weight**: 47.0 kg
- **Target Weight**: 55.0 kg
- **Height**: 168 cm
- **BMI**: ~16.7 *(includes medical assessment recommendation for safe, guided weight gain)*
- **Fitness Level**: Beginner
- **Push-up Ability**: 5–10 consecutive reps
- **Training Frequency**: 4 days per week (Mon, Tue, Thu, Sat)
- **Workout Duration**: 30–40 minutes (Night, default 7:30 PM)
- **Equipment**: No gym required; household backpack and sturdy furniture

---

## 🏋️ 4-Day Progressive Home Workout System

### Monday – Chest, Shoulders & Triceps
1. **Incline Push-ups**: 3 sets × 8–12 reps
2. **Standard Push-ups**: 2 sets × 4–8 reps
3. **Pike Push-ups**: 2 sets × 5–8 reps
4. **Close-grip Incline Push-ups**: 2 sets × 8–10 reps

### Tuesday – Legs & Core
1. **Bodyweight Squats**: 3 sets × 12 reps
2. **Reverse Lunges**: 2 sets × 8 reps per leg
3. **Glute Bridges**: 3 sets × 12–15 reps
4. **Calf Raises**: 2 sets × 15 reps
5. **Plank**: 2 sets × 20–40 seconds

### Thursday – Back & Biceps
1. **Backpack Bent-over Rows**: 3 sets × 10–12 reps (start with 3–5 kg in backpack)
2. **Single-arm Backpack Rows**: 2 sets × 10 reps per arm
3. **Backpack Biceps Curls**: 2 sets × 10–12 reps
4. **Superman Holds**: 2 sets × 15–20 seconds
5. **Reverse Snow Angels**: 2 sets × 10–12 reps

### Saturday – Full Body
1. **Incline or Standard Push-ups**: 2 sets × 8–10 reps
2. **Backpack Rows**: 2 sets × 10–12 reps
3. **Squats**: 2 sets × 12 reps
4. **Glute Bridges**: 2 sets × 15 reps
5. **Lying Leg Raises**: 2 sets × 10 reps

---

## 🍛 Sri Lankan Nutrition & Food Tracker
- **Calorie Surplus Target**: ~2100 kcal (safe moderate surplus for gradual gain)
- **Protein Target**: ~85g (~1.8g per kg body weight)
- **Sri Lankan Food Database**:
  - Cooked White / Red Rice
  - Boiled & Fried Eggs
  - Sri Lankan Chicken Curry
  - Fish Curry / Ambul Thiyal
  - Parippu / Dhal Curry
  - Fresh Cow Milk / UHT Milk
  - Bananas (Ambul / Seeni / Kolikuttu)
  - Roasted Peanuts (Rata Kaju)
  - Peanut Butter
  - Rolled Oats
  - String Hoppers (Idiyappam)
  - Pol Roti
  - Boiled Potatoes (Ala)
  - Canned Tuna / Jack Mackerel
  - Soya Meat Curry
- **Homemade Weight-Gain Shake Quick Add**:
  - 250ml milk + 1 banana + 40g oats + 2 tbsp peanut butter = **580 kcal & 22.1g protein** (1-tap log).

---

## 📈 Sunday Check-in & Progressive Overload
Every Sunday at 7:00 PM, the app prompts for the weekly check-in:
1. Current weight in kg
2. Number of workouts completed out of 4
3. Energy level
4. Appetite
5. Average sleep hours
6. Muscle soreness or joint pain
7. Personal notes

**Progressive Overload Guidance**:
When you consistently hit the top of the repetition range across all sets with clean form, GainTrack suggests adding +1 kg to your backpack or advancing your push-up angle.

---

## 🚀 How to Install on Your Samsung Galaxy A15

### Option A: Automated GitHub Actions (No Android Studio required on PC!)
1. Create a new repository on your GitHub account:
   - Go to [https://github.com/new](https://github.com/new)
   - Repository name: `GainTrack`
   - Select **Public**
2. In your terminal / command prompt, run:
   ```bash
   cd C:\Users\HP\.gemini\antigravity\scratch\GainTrack
   git init
   git add .
   git commit -m "Initial commit of GainTrack"
   git branch -M main
   git remote add origin https://github.com/mohamedrizee2005-ship-it/GainTrack.git
   git push -u origin main
   ```
3. Once pushed, open GitHub in your phone or PC browser:
   - Tap **Actions** tab on your repository.
   - The **Build GainTrack APK** workflow will run automatically (~2-3 minutes).
   - Once completed, tap **Releases** or scroll to **Artifacts** and download `GainTrack-v1.0.0-Debug-APK` or `app-debug.apk`.
4. Open the `.apk` on your Samsung Galaxy A15 and tap **Install**!

---

### Option B: Build via Android Studio
1. Launch **Android Studio**.
2. Select **Open** and navigate to:
   `C:\Users\HP\.gemini\antigravity\scratch\GainTrack`
3. Wait for Gradle to finish indexing dependencies.
4. In the top menu, click:
   **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**.
5. When complete, a notification will appear saying *"APK(s) generated successfully"*. Click **locate**.
   - The file will be at:
     `GainTrack/app/build/outputs/apk/debug/app-debug.apk`
6. Transfer this `app-debug.apk` to your Samsung Galaxy A15 (via USB, WhatsApp, or Google Drive), tap it, and tap **Install**.

---

## 🛠️ Tech Stack & Architecture
- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose with Material 3 (Dark Navy & Emerald Green)
- **Database**: Room Database 2.6.1 with KSP
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Navigation**: Navigation Compose 2.8.3
- **Background Tasks & Alarms**: WorkManager & Android AlarmManager with NotificationChannels
- **Charts**: Custom Jetpack Compose Canvas charts
- **Architecture**: Clean Architecture (Presentation, Domain, Data)
