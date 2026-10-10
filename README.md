# ByteLift

ByteLift is an Android app for workout planning, diet tracking and progress monitoring.

## Features

- Email and Google sign-in, with a fitness profile onboarding flow
- Workout routines (presets and custom), workout streaks and a leaderboard
- Diet page with recipes loaded from Firestore
- Profile and settings: theme, units, workout reminders, data export

## Tech

- Java, Android SDK (minSdk 25, targetSdk 36), Gradle Kotlin DSL
- Firebase Authentication, Cloud Firestore and Cloud Storage
- MVVM-style architecture with ViewModels and LiveData
- Material Components, WorkManager for reminders

## Setup

1. Clone the repository and open it in Android Studio.
2. Create a Firebase project, register an Android app with the package name
   `com.example.bytelift`, and enable Authentication (Email/Password, Google) and Firestore.
3. Download your own `google-services.json` and place it in `app/`.
   This file is not included in the repository.
4. Build and run on a device or emulator.

## About

Group project for WIA2007 Mobile Application Development at Universiti Malaya,
built around one of the UN Sustainable Development Goals.

Built by a team of students.
