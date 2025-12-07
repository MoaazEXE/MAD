package com.example.bytelift;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple provider for presets. You will replace/add presets here.
 * Each preset is a WorkoutRoutineActivity.WorkoutRoutine with dayToExercises map.
 */
public class PresetsProvider {
    private static WorkoutRoutineActivity.WorkoutRoutine selectedPreset;

    public static void setSelectedPreset(WorkoutRoutineActivity.WorkoutRoutine p) { selectedPreset = p; }
    public static WorkoutRoutineActivity.WorkoutRoutine getSelectedPreset() { return selectedPreset; }

    public static List<WorkoutRoutineActivity.WorkoutRoutine> getPresetsForCategory(String category) {
        // TODO: replace these sample presets with your actual presets and images
        List<WorkoutRoutineActivity.WorkoutRoutine> list = new ArrayList<>();

        // Example: small 2-day preset for demonstration
        Map<String, List<WorkoutRoutineActivity.SelectedExercise>> days = new LinkedHashMap<>();
        List<WorkoutRoutineActivity.SelectedExercise> mList = new ArrayList<>();
        mList.add(new WorkoutRoutineActivity.SelectedExercise("pushup", "Push Ups", "3x10", R.drawable.pushup));
        mList.add(new WorkoutRoutineActivity.SelectedExercise("squat", "Bodyweight Squats", "3x15", R.drawable.squat));
        days.put("Monday", mList);

        List<WorkoutRoutineActivity.SelectedExercise> tList = new ArrayList<>();
        tList.add(new WorkoutRoutineActivity.SelectedExercise("plank", "Plank", "60s", R.drawable.plank));
        days.put("Tuesday", tList);

        WorkoutRoutineActivity.WorkoutRoutine sample = new WorkoutRoutineActivity.WorkoutRoutine("Sample " + category, days);
        sample.id = "preset_" + category + "_1";
        list.add(sample);

        // return same sample for all categories by default
        return list;
    }
}