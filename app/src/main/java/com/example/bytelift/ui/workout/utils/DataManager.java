package com.example.bytelift.ui.workout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bytelift.ui.workout.models.Routine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String TAG = "DataManager";
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_ROUTINES = "routines";

    private static List<Routine> routines = new ArrayList<>();
    private static SharedPreferences prefs;
    private static boolean firebaseSynced = false;

    // ✅ Initialize with local cache + Firebase sync
    // ⚠️ IMPORTANT: always pass Application Context
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            loadRoutinesLocal();
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
                mergeRoutines(firebaseRoutines);
                firebaseSynced = true;
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Firebase sync failed: " + error);
            }
        });
    }

    // ✅ Merge Firebase and local routines (Firebase priority)
    private static void mergeRoutines(List<Routine> firebaseRoutines) {
        for (Routine fbRoutine : firebaseRoutines) {
            boolean found = false;

            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i).routineId.equals(fbRoutine.routineId)) {
                    if (fbRoutine.updatedAt > routines.get(i).updatedAt) {
                        routines.set(i, fbRoutine);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                routines.add(fbRoutine);
            }
        }

        saveRoutinesLocal();
    }

    public static List<Routine> getRoutines() {
        return routines;
    }

    // ✅ Add routine
    public static void addRoutine(Routine routine) {
        routines.add(routine);
        saveRoutinesLocal();

        FirebaseManager.saveRoutine(routine, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Routine synced to Firebase: " + routine.name);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to sync routine to Firebase: " + error);
            }
        });
    }

    // ✅ Remove routine
    public static void removeRoutine(Routine routine) {
        routines.remove(routine);
        saveRoutinesLocal();

        FirebaseManager.deleteRoutine(routine.routineId, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Routine deleted from Firebase: " + routine.routineId);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to delete routine from Firebase: " + error);
            }
        });
    }

    // ✅ Update routine
    public static void updateRoutine(Routine routine) {
        if (routine == null || routine.routineId == null) return;

        for (int i = 0; i < routines.size(); i++) {
            if (routines.get(i).routineId.equals(routine.routineId)) {
                routine.updatedAt = System.currentTimeMillis();
                routines.set(i, routine);
                saveRoutinesLocal();

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

    // ✅ Save locally
    private static void saveRoutinesLocal() {
        if (prefs == null) return;

        Gson gson = new Gson();
        String json = gson.toJson(routines);
        prefs.edit().putString(KEY_ROUTINES, json).apply();
        Log.d(TAG, "Saved " + routines.size() + " routines locally");
    }

    // ✅ Load from local cache
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

    public static Routine getRoutineById(String routineId) {
        for (Routine routine : routines) {
            if (routine.routineId.equals(routineId)) {
                return routine;
            }
        }
        return null;
    }

    public static void clearAllRoutines() {
        routines.clear();
        saveRoutinesLocal();
    }

    public static void forceSync() {
        firebaseSynced = false;
        syncWithFirebase();
    }
}
