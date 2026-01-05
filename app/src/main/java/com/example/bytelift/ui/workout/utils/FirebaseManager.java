package com.example.bytelift.ui.workout.utils;

import android.util.Log;

import com.example.bytelift.ui.workout.models.Routine;
import com. google.firebase.auth.FirebaseAuth;
import com.google. firebase.auth.FirebaseUser;
import com.google.firebase. firestore.FirebaseFirestore;
import com. google.firebase.firestore.QueryDocumentSnapshot;

import java. util.ArrayList;
import java. util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_ROUTINES = "routines";

    private static FirebaseFirestore db;
    private static FirebaseAuth auth;

    // ✅ Initialize Firebase instances
    public static void init() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
    }

    // ✅ Get current user ID
    private static String getUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // ✅ Save a routine to Firestore
    public static void saveRoutine(Routine routine, OnCompleteListener listener) {
        init();
        String userId = getUserId();

        if (userId == null) {
            Log.e(TAG, "User not logged in");
            if (listener != null) listener.onFailure("User not logged in");
            return;
        }

        // Set userId in routine
        routine.userId = userId;
        routine.updatedAt = System.currentTimeMillis();

        // Convert routine to Map
        Map<String, Object> routineData = routineToMap(routine);

        // Save to Firestore:  users/{userId}/routines/{routineId}
        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_ROUTINES)
                .document(routine.routineId)
                .set(routineData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Routine saved successfully:  " + routine.name);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving routine", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // ✅ Load all routines from Firestore
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
                            if (routine != null) {
                                routines.add(routine);
                            }
                        } catch (Exception e) {
                            Log. e(TAG, "Error parsing routine", e);
                        }
                    }

                    Log.d(TAG, "Loaded " + routines. size() + " routines from Firestore");
                    if (listener != null) listener.onSuccess(routines);
                })
                .addOnFailureListener(e -> {
                    Log. e(TAG, "Error loading routines", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // ✅ Delete a routine from Firestore
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
                    Log.d(TAG, "Routine deleted successfully:  " + routineId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log. e(TAG, "Error deleting routine", e);
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // ✅ Convert Routine object to Map for Firestore
    private static Map<String, Object> routineToMap(Routine routine) {
        Map<String, Object> map = new HashMap<>();
        map.put("routineId", routine.routineId);
        map.put("userId", routine.userId);
        map.put("name", routine.name);
        map.put("type", routine.type);
        map.put("daysPerWeek", routine.daysPerWeek);
        map.put("days", routine.days);
        map.put("createdAt", routine.createdAt);
        map.put("updatedAt", routine.updatedAt);
        map.put("notes", routine.notes);
        return map;
    }

    // ✅ Convert Firestore Map to Routine object
    private static Routine mapToRoutine(Map<String, Object> map) {
        Routine routine = new Routine();
        routine.routineId = (String) map.get("routineId");
        routine.userId = (String) map.get("userId");
        routine.name = (String) map.get("name");
        routine.type = (String) map.get("type");

        Object daysPerWeek = map.get("daysPerWeek");
        routine.daysPerWeek = (daysPerWeek instanceof Long) ? ((Long) daysPerWeek).intValue() : 0;

        @SuppressWarnings("unchecked")
        Map<String, Object> daysMap = (Map<String, Object>) map.get("days");
        routine.days = (Map) daysMap;

        Object createdAt = map.get("createdAt");
        routine.createdAt = (createdAt instanceof Long) ? (Long) createdAt : 0L;

        Object updatedAt = map.get("updatedAt");
        routine.updatedAt = (updatedAt instanceof Long) ? (Long) updatedAt : 0L;

        routine.notes = (String) map.get("notes");

        return routine;
    }

    // ✅ Callback interfaces
    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnRoutinesLoadedListener {
        void onSuccess(List<Routine> routines);
        void onFailure(String error);
    }
}