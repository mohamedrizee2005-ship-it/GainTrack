# GainTrack 4.0 — Your Workout, Simplified

A focused **offline-first, no-equipment Android strength coach**, built with Kotlin and Jetpack Compose. GainTrack 4 removes the Nutrition tab, food photos, AI camera, Gemini API setup, calorie counter, and old meal reminders. This version is only about training, rest and consistent progress.

## What is new

- **Today:** One clear next step, recovery-day guidance, current weight and target milestone
- **Plan:** Four guided workouts with exercise names, sets, repetitions, rest and video search links
- **Session:** Tap to complete sets, animated progress bar, automatic rest countdown, and a skip-rest control
- **Progress:** Local completion history and 12-week consistency display
- **Profile:** Choose 0–4, 5–10, 11–20 or 21+ push-ups; update weight and goal
- **Privacy:** Runs without the app's INTERNET permission, AI APIs or keys
- **Resilience:** Incomplete set checkmarks are restored if you switch away and start the same workout again

## Weekly schedule

| Day | Focus |
| --- | --- |
| Monday | Chest, shoulders and triceps |
| Tuesday | Back, biceps and core |
| Wednesday | Rest |
| Thursday | Chest, shoulders and triceps |
| Friday | Rest |
| Saturday | Back, arms and light legs |
| Sunday | Rest / weight check-in |

The plan adapts the first push-up exercise to your ability, initially 5–10 consecutive good full push-ups. Use controlled technique, progress reps gradually, and avoid training through pain. Bodyweight-only back exercises are inherently harder to progressively load than rows with resistance.

**Starting profile:** 47 kg, 167 cm, age 21, optional initial milestone 52 kg. Weight can be updated in Profile; numbers are personal starting defaults, not prescriptions. A BMI below 18.5 merits clinical consideration, particularly after unintended weight loss. Weight gain generally requires adequate nutrition even though this version no longer tracks food.

## Installing on Android

Download the v4 APK from [GitHub Releases](../../releases/tag/v4.0.0), then install it over your existing GainTrack app. **Do not uninstall first**: workout completions and historical data are stored locally. The package ID and database schema are retained to support upgrades. Old nutrition tables may remain inside a pre-existing database solely for backward compatibility, but this release has no Nutrition screen or Gemini integration.

This APK is a development/debug distribution, not a production-signed Play Store application. Verify Android permissions and device behavior before wider sharing.

## Tech stack

- Kotlin, Jetpack Compose and Material 3
- Local SharedPreferences for workout checkmarks and profile milestones
- Room database retained for historical compatibility
- Android notifications for workouts and check-ins
- GitHub Actions for verified APK compilation

## Version history

- v4.0.0 — Workout-only redesign, no AI or food logging
- v3.0.2 — Legacy nutrition, now removed from the visible app

