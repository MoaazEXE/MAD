package com.example.bytelift.ui.workout;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoutineStorage {

    private static final String PREF_NAME = "workout_routines";
    private static final String KEY_ROUTINES = "routines";

    public static List<WorkoutRoutineActivity.WorkoutRoutine> loadRoutines(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ROUTINES, null);
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<WorkoutRoutineActivity.WorkoutRoutine>>() {}.getType();
            List<WorkoutRoutineActivity.WorkoutRoutine> list =
                    gson.fromJson(json, type);
            if (list == null) {
                return new ArrayList<>();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveRoutines(Context context,
                                    List<WorkoutRoutineActivity.WorkoutRoutine> routines) {
        if (routines == null) routines = new ArrayList<>();
        Gson gson = new Gson();
        String json = gson.toJson(routines);
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ROUTINES, json).apply();
    }
}
