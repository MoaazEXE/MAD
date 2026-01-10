package com.example.bytelift.utils;

import android.content. Context;
import android.content. SharedPreferences;
import android.util.Log;

import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.models.StreakData;
import com.example.bytelift.ui.workout.utils.DataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com. google.gson.Gson;

import java. text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
public class StreakManager {

    private static final String TAG = "StreakManager";
    private static final String PREF_NAME = "streak_prefs_v2";
    private static final String KEY_STREAK_DATA = "streak_data";

    private static SharedPreferences prefs;
    private static StreakData streakData;
    private static FirebaseFirestore db;
    private static FirebaseAuth auth;

    // ✅ Initialize
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth. getInstance();

            loadStreakData();
            checkWeekRollover();
        }
    }

    // ✅ Set active routine
    public static void setActiveRoutine(Context context, String routineId) {
        init(context);

        String oldRoutineId = streakData. activeRoutineId;
        streakData.activeRoutineId = routineId;

        // Update required workouts based on new active routine
        Routine activeRoutine = DataManager. getRoutineById(routineId);
        if (activeRoutine != null) {
            streakData.workoutsRequiredThisWeek = activeRoutine. daysPerWeek;

            // Update isActiveForStreak flag in all routines
            List<Routine> allRoutines = DataManager.getRoutines();
            for (Routine r : allRoutines) {
                r.isActiveForStreak = r.routineId.equals(routineId);
                DataManager.updateRoutine(r);
            }

            Log.d(TAG, "Active routine set to: " + activeRoutine.name
                    + " (requires " + activeRoutine.daysPerWeek + " workouts/week)");
        }

        saveStreakData();
        syncToFirebase();
    }

    // ✅ Get active routine
    public static Routine getActiveRoutine(Context context) {
        init(context);

        if (streakData.activeRoutineId.isEmpty()) {
            return null;
        }

        return DataManager.getRoutineById(streakData.activeRoutineId);
    }

    // ✅ Mark workout complete (with day name)
    public static void markWorkoutComplete(Context context, String routineId, String dayName) {
        init(context);

        // Check if this is the active routine
        if (streakData.activeRoutineId.isEmpty()) {
            setActiveRoutine(context, routineId);
        } else if (!streakData.activeRoutineId. equals(routineId)) {
            Log.d(TAG, "Workout not counted: Not the active routine");
            return;
        }

        String today = getTodayDateString();

        // Check if already logged today
        if (streakData. workoutDatesThisWeek.contains(today)) {
            Log.d(TAG, "Workout already logged today");
            return;
        }

        // Add today's workout
        streakData.workoutDatesThisWeek.add(today);
        streakData.workoutsCompletedThisWeek++;
        streakData.lastUpdated = System.currentTimeMillis();

        Log. d(TAG, "Workout completed (" + dayName + ")! Progress: " + streakData. workoutsCompletedThisWeek
                + "/" + streakData.workoutsRequiredThisWeek);

        saveStreakData();
        syncToFirebase();
    }

    // ✅ Mark workout complete (backward compatible - without day name)
    public static void markWorkoutComplete(Context context, String routineId) {
        markWorkoutComplete(context, routineId, "Workout");
    }

    // ✅ Check if workout already logged today
    public static boolean isWorkoutLoggedToday(Context context) {
        init(context);
        String today = getTodayDateString();
        return streakData.workoutDatesThisWeek.contains(today);
    }

    // ✅ Get list of scheduled days for active routine
    public static List<String> getScheduledDays(Context context) {
        init(context);

        if (streakData.activeRoutineId.isEmpty()) {
            return new ArrayList<>();
        }

        Routine activeRoutine = DataManager.getRoutineById(streakData.activeRoutineId);
        if (activeRoutine == null || activeRoutine.days == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(activeRoutine.days. keySet());
    }

    // ✅ Get current streak
    public static int getCurrentStreak(Context context) {
        init(context);
        return streakData.currentStreak;
    }

    // ✅ Get longest streak
    public static int getLongestStreak(Context context) {
        init(context);
        return streakData.longestStreak;
    }

    // ✅ Get week progress string
    public static String getWeekProgress(Context context) {
        init(context);
        return streakData.workoutsCompletedThisWeek + " of "
                + streakData.workoutsRequiredThisWeek + " workouts";
    }

    // ✅ Get workouts remaining
    public static int getWorkoutsRemaining(Context context) {
        init(context);
        int remaining = streakData.workoutsRequiredThisWeek - streakData.workoutsCompletedThisWeek;
        return Math.max(0, remaining);
    }

    // ✅ Get days left in week
    public static int getDaysLeftInWeek() {
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        return (8 - today) % 7;
    }

    // ✅ Check if week is complete
    public static boolean isWeekComplete(Context context) {
        init(context);
        return streakData.workoutsCompletedThisWeek >= streakData.workoutsRequiredThisWeek;
    }

    // ✅ Get full streak data
    public static StreakData getStreakData(Context context) {
        init(context);
        return streakData;
    }

    // ✅ Check if week has rolled over
    private static void checkWeekRollover() {
        long currentWeekStart = getThisWeekMondayTimestamp();

        if (streakData.weekStartDate == 0 || currentWeekStart > streakData.weekStartDate) {
            Log.d(TAG, "Week rollover detected!");

            if (streakData.weekStartDate > 0) {
                evaluateLastWeek();
            }

            startNewWeek();
        }
    }

    // ✅ Evaluate if last week earned a streak
    private static void evaluateLastWeek() {
        if (streakData.workoutsCompletedThisWeek >= streakData.workoutsRequiredThisWeek) {
            streakData.currentStreak++;

            if (streakData.currentStreak > streakData.longestStreak) {
                streakData.longestStreak = streakData.currentStreak;
            }

            Log.d(TAG, "🔥 STREAK EARNED! Now at:  " + streakData.currentStreak + " weeks");
        } else {
            if (streakData.currentStreak > 0) {
                Log.d(TAG, "❌ Streak broken.  Was:  " + streakData.currentStreak
                        + ", completed " + streakData.workoutsCompletedThisWeek
                        + "/" + streakData.workoutsRequiredThisWeek);
            }
            streakData.currentStreak = 0;
        }
    }

    // ✅ Start a new week
    private static void startNewWeek() {
        streakData.weekStartDate = getThisWeekMondayTimestamp();
        streakData. workoutsCompletedThisWeek = 0;
        streakData.workoutDatesThisWeek.clear();

        streakData.workoutsRequiredThisWeek = calculateRequiredWorkouts();

        Log.d(TAG, "New week started.  Required workouts: " + streakData. workoutsRequiredThisWeek);

        saveStreakData();
        syncToFirebase();
    }

    // ✅ Calculate required workouts from active routine
    private static int calculateRequiredWorkouts() {
        if (! streakData.activeRoutineId.isEmpty()) {
            Routine activeRoutine = DataManager.getRoutineById(streakData. activeRoutineId);
            if (activeRoutine != null) {
                return activeRoutine. daysPerWeek;
            }
        }

        List<Routine> routines = DataManager.getRoutines();

        if (routines == null || routines.isEmpty()) {
            return 3;
        }

        int maxDays = 0;
        for (Routine routine : routines) {
            if (routine. daysPerWeek > maxDays) {
                maxDays = routine.daysPerWeek;
            }
        }

        return maxDays > 0 ? maxDays :  3;
    }

    // ✅ Get Monday 00:00 timestamp of current week
    private static long getThisWeekMondayTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar. HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar. MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // ✅ Get today's date as string (YYYY-MM-DD)
    private static String getTodayDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    // ✅ Load streak data from SharedPreferences
    private static void loadStreakData() {
        String json = prefs.getString(KEY_STREAK_DATA, null);

        if (json != null) {
            Gson gson = new Gson();
            streakData = gson.fromJson(json, StreakData.class);
            Log.d(TAG, "Loaded streak data: " + streakData.currentStreak + " weeks");
        } else {
            streakData = new StreakData();
            streakData.workoutsRequiredThisWeek = calculateRequiredWorkouts();
            Log.d(TAG, "Created new streak data");
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            streakData.userId = user.getUid();
        }
    }

    // ✅ Save streak data to SharedPreferences
    private static void saveStreakData() {
        if (prefs == null || streakData == null) return;

        Gson gson = new Gson();
        String json = gson.toJson(streakData);
        prefs.edit().putString(KEY_STREAK_DATA, json).apply();

        Log.d(TAG, "Saved streak data locally");
    }

    // ✅ Sync to Firebase
    private static void syncToFirebase() {
        if (db == null || streakData == null || streakData.userId.isEmpty()) {
            Log.w(TAG, "Cannot sync to Firebase: not initialized or no user");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("currentStreak", streakData. currentStreak);
        data.put("longestStreak", streakData.longestStreak);
        data.put("weekStartDate", streakData.weekStartDate);
        data.put("workoutsRequiredThisWeek", streakData.workoutsRequiredThisWeek);
        data.put("workoutsCompletedThisWeek", streakData.workoutsCompletedThisWeek);
        data.put("workoutDatesThisWeek", streakData.workoutDatesThisWeek);
        data.put("activeRoutineId", streakData. activeRoutineId);
        data.put("lastUpdated", streakData.lastUpdated);

        db.collection("users")
                .document(streakData.userId)
                .collection("streakData")
                .document("current")
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Streak synced to Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to sync streak", e));
    }

    // ✅ Force recalculate required workouts
    public static void recalculateRequiredWorkouts(Context context) {
        init(context);
        streakData.workoutsRequiredThisWeek = calculateRequiredWorkouts();
        saveStreakData();
        syncToFirebase();
        Log.d(TAG, "Recalculated required workouts:  " + streakData.workoutsRequiredThisWeek);
    }

    // ✅ Reset streak
    public static void resetStreak(Context context) {
        init(context);
        streakData. currentStreak = 0;
        streakData.workoutsCompletedThisWeek = 0;
        streakData.workoutDatesThisWeek.clear();
        saveStreakData();
        syncToFirebase();
        Log.d(TAG, "Streak reset");
    }
}