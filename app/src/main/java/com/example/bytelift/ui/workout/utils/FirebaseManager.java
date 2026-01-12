package com.example.bytelift.ui.workout.utils;

import android.util.Log;

import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_ROUTINES = "routines";

    private static FirebaseFirestore db;
    private static FirebaseAuth auth;

    // ✅ Initialize Firebase
    public static void init() {
        if (db == null) db = FirebaseFirestore.getInstance();
        if (auth == null) auth = FirebaseAuth.getInstance();
    }

    private static String getUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // ✅ Save routine
    public static void saveRoutine(Routine routine, OnCompleteListener listener) {
        init();
        String userId = getUserId();

        if (userId == null) {
            Log.e(TAG, "User not logged in");
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        routine.userId = userId;
        routine.updatedAt = System.currentTimeMillis();

        Map<String, Object> routineData = routineToMap(routine);

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_ROUTINES)
                .document(routine.routineId)
                .set(routineData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Routine saved: " + routine.name);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving routine", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // ✅ Load routines
    public static void loadRoutines(OnRoutinesLoadedListener listener) {
        init();
        String userId = getUserId();

        if (userId == null) {
            Log.e(TAG, "User not logged in");
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_ROUTINES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Routine> routines = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Routine routine = mapToRoutine(document.getData());
                            if (routine != null) routines.add(routine);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing routine", e);
                        }
                    }

                    Log.d(TAG, "Loaded " + routines.size() + " routines from Firestore");
                    if (listener != null) listener.onSuccess(routines);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading routines", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // ✅ Delete routine
    public static void deleteRoutine(String routineId, OnCompleteListener listener) {
        init();
        String userId = getUserId();

        if (userId == null) {
            Log.e(TAG, "User not logged in");
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_ROUTINES)
                .document(routineId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Routine deleted: " + routineId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting routine", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // 🔄 Mapping helpers
    private static Map<String, Object> routineToMap(Routine routine) {
        Map<String, Object> map = new HashMap<>();
        map.put("routineId", routine.routineId);
        map.put("userId", routine.userId);
        map.put("name", routine.name);
        map.put("type", routine.type);
        map.put("daysPerWeek", routine.daysPerWeek);

        // Firestore can store nested Maps/Lists fine
        map.put("days", routine.days);

        map.put("createdAt", routine.createdAt);
        map.put("updatedAt", routine.updatedAt);
        map.put("notes", routine.notes);
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Routine mapToRoutine(Map<String, Object> map) {
        Routine routine = new Routine();
        routine.routineId = (String) map.get("routineId");
        routine.userId = (String) map.get("userId");
        routine.name = (String) map.get("name");
        routine.type = (String) map.get("type");

        Object daysPerWeek = map.get("daysPerWeek");
        routine.daysPerWeek = (daysPerWeek instanceof Long) ? ((Long) daysPerWeek).intValue() : 0;

        // ✅ FIX: Firestore returns Map<String, Object> -> we must rebuild Map<String, List<Exercise>>
        Map<String, List<Exercise>> parsedDays = new HashMap<>();

        Object rawDaysObj = map.get("days");
        if (rawDaysObj instanceof Map) {
            Map<String, Object> rawDays = (Map<String, Object>) rawDaysObj;

            for (Map.Entry<String, Object> entry : rawDays.entrySet()) {
                String dayKey = entry.getKey();
                Object rawListObj = entry.getValue();

                List<Exercise> dayExercises = new ArrayList<>();

                if (rawListObj instanceof List) {
                    List<Object> rawList = (List<Object>) rawListObj;

                    for (Object item : rawList) {
                        if (item instanceof Map) {
                            Map<String, Object> exMap = (Map<String, Object>) item;

                            Exercise ex = new Exercise();
                            ex.id = (String) exMap.get("id");
                            ex.name = (String) exMap.get("name");
                            ex.muscleGroup = (String) exMap.get("muscleGroup");

                            Object setsObj = exMap.get("sets");
                            ex.sets = (setsObj instanceof Long) ? ((Long) setsObj).intValue()
                                    : (setsObj instanceof Integer ? (Integer) setsObj : 0);

                            Object repsObj = exMap.get("reps");
                            ex.reps = (repsObj instanceof Long) ? ((Long) repsObj).intValue()
                                    : (repsObj instanceof Integer ? (Integer) repsObj : 0);

                            Object weightObj = exMap.get("weight");
                            if (weightObj instanceof Double) {
                                ex.weight = (Double) weightObj;
                            } else if (weightObj instanceof Long) {
                                ex.weight = ((Long) weightObj).doubleValue();
                            } else if (weightObj instanceof Integer) {
                                ex.weight = ((Integer) weightObj).doubleValue();
                            } else {
                                ex.weight = null;
                            }

                            dayExercises.add(ex);
                        }
                    }
                }

                parsedDays.put(dayKey, dayExercises);
            }
        }

        routine.days = parsedDays;

        Object createdAt = map.get("createdAt");
        routine.createdAt = (createdAt instanceof Long) ? (Long) createdAt : 0L;

        Object updatedAt = map.get("updatedAt");
        routine.updatedAt = (updatedAt instanceof Long) ? (Long) updatedAt : 0L;

        routine.notes = (String) map.get("notes");

        return routine;
    }

    // 🔔 Callbacks
    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnRoutinesLoadedListener {
        void onSuccess(List<Routine> routines);
        void onFailure(String error);
    }
}
