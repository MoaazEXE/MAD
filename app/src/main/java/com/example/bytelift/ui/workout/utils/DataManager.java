package com.example.bytelift.ui.workout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bytelift.ui.workout.models.Routine;
import com.google.gson.Gson;
import com. google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String TAG = "DataManager";
    private static List<Routine> routines = new ArrayList<>();
    private static SharedPreferences prefs;
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_ROUTINES = "routines";
    private static boolean firebaseSynced = false;

    // ✅ Initialize with Firebase sync
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            // Load from local cache first
            loadRoutinesLocal();

            // Then sync with Firebase
            syncWithFirebase();
        }
    }

    // ✅ Sync local data with Firebase
    private static void syncWithFirebase() {
        if (firebaseSynced) return;

        FirebaseManager.init();
        FirebaseManager.loadRoutines(new FirebaseManager.OnRoutinesLoadedListener() {
            @Override
            public void onSuccess(List<Routine> firebaseRoutines) {
                Log.d(TAG, "Firebase sync successful: " + firebaseRoutines.size() + " routines");

                // Merge Firebase routines with local routines
                mergeRoutines(firebaseRoutines);
                firebaseSynced = true;
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Firebase sync failed: " + error);
                // Continue with local data
            }
        });
    }

    // ✅ Merge Firebase and local routines (Firebase takes priority)
    private static void mergeRoutines(List<Routine> firebaseRoutines) {
        for (Routine fbRoutine : firebaseRoutines) {
            boolean found = false;

            // Check if routine exists locally
            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i).routineId.equals(fbRoutine.routineId)) {
                    // Firebase version is newer, replace local
                    if (fbRoutine.updatedAt > routines.get(i).updatedAt) {
                        routines.set(i, fbRoutine);
                    }
                    found = true;
                    break;
                }
            }

            // New routine from Firebase, add it
            if (!found) {
                routines.add(fbRoutine);
            }
        }

        // Save merged data locally
        saveRoutinesLocal();
    }

    public static List<Routine> getRoutines() {
        return routines;
    }

    // ✅ Add routine (saves to both local and Firebase)
    public static void addRoutine(Routine routine) {
        routines.add(routine);
        saveRoutinesLocal();

        // Save to Firebase
        FirebaseManager. saveRoutine(routine, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Routine synced to Firebase: " + routine. name);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to sync routine to Firebase:  " + error);
            }
        });
    }

    // ✅ Remove routine (removes from both local and Firebase)
    public static void removeRoutine(Routine routine) {
        routines.remove(routine);
        saveRoutinesLocal();

        // Delete from Firebase
        FirebaseManager. deleteRoutine(routine.routineId, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Routine deleted from Firebase:  " + routine.routineId);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to delete routine from Firebase: " + error);
            }
        });
    }

    // ✅ Update routine (updates both local and Firebase)
    public static void updateRoutine(Routine routine) {
        if (routine == null || routine.routineId == null) return;

        // Update locally
        for (int i = 0; i < routines.size(); i++) {
            if (routines. get(i).routineId.equals(routine.routineId)) {
                routine.updatedAt = System.currentTimeMillis();
                routines.set(i, routine);
                saveRoutinesLocal();

                // Update Firebase
                FirebaseManager.saveRoutine(routine, new FirebaseManager.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Routine updated in Firebase: " + routine.name);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "Failed to update routine in Firebase: " + error);
                    }
                });
                return;
            }
        }
    }

    // ✅ Save to SharedPreferences (local cache)
    private static void saveRoutinesLocal() {
        if (prefs == null) return;

        Gson gson = new Gson();
        String json = gson.toJson(routines);
        prefs.edit().putString(KEY_ROUTINES, json).apply();
        Log.d(TAG, "Saved " + routines.size() + " routines locally");
    }

    // ✅ Load from SharedPreferences (local cache)
    private static void loadRoutinesLocal() {
        if (prefs == null) return;

        String json = prefs.getString(KEY_ROUTINES, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Routine>>() {}.getType();
            List<Routine> loaded = gson.fromJson(json, type);
            if (loaded != null) {
                routines = loaded;
                Log.d(TAG, "Loaded " + routines.size() + " routines from local cache");
            }
        }
    }

    // ✅ Get a specific routine by ID
    public static Routine getRoutineById(String routineId) {
        for (Routine routine :  routines) {
            if (routine.routineId. equals(routineId)) {
                return routine;
            }
        }
        return null;
    }

    // ✅ Clear all routines (for testing/logout)
    public static void clearAllRoutines() {
        routines.clear();
        saveRoutinesLocal();
    }

    // ✅ Force re-sync with Firebase
    public static void forceSync() {
        firebaseSynced = false;
        syncWithFirebase();
    }
}