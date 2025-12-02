package com.example.bytelift;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java. lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RoutineStorage {
    private static final String PREFS_NAME = "mad_workout_prefs";
    private static final String KEY_ROUTINES = "saved_routines";
    private static final Gson gson = new Gson();

    public static List<WorkoutRoutineActivity. WorkoutRoutine> loadRoutines(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = sp.getString(KEY_ROUTINES, null);
        if (json == null) return new ArrayList<>();
        Type t = new TypeToken<List<WorkoutRoutineActivity.WorkoutRoutine>>() {}. getType();
        List<WorkoutRoutineActivity.WorkoutRoutine> out = gson.fromJson(json, t);
        return out != null ? out : new ArrayList<>();
    }

    public static void saveRoutines(Context ctx, List<WorkoutRoutineActivity. WorkoutRoutine> routines) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ROUTINES, gson.toJson(routines)). apply();
    }
}