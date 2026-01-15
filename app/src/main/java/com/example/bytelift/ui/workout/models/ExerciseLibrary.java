package com.example.bytelift.ui.workout.models;

import java.util.ArrayList;
import java.util.List;

public class ExerciseLibrary {

    public static List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();

        // CHEST EXERCISES
        exercises.add(createExercise("Barbell Bench Press", "chest"));
        exercises.add(createExercise("Incline Dumbbell Press", "chest"));
        exercises.add(createExercise("Decline Bench Press", "chest"));
        exercises.add(createExercise("Dumbbell Bench Press", "chest"));
        exercises.add(createExercise("Chest Fly", "chest"));
        exercises. add(createExercise("Cable Fly", "chest"));
        exercises. add(createExercise("Push-Ups", "chest"));
        exercises.add(createExercise("Weighted Dips", "chest"));
        exercises.add(createExercise("Incline Bench", "chest"));

        // BACK EXERCISES
        exercises.add(createExercise("Deadlift", "back"));
        exercises.add(createExercise("Pull-Ups", "back"));
        exercises.add(createExercise("Weighted Pull-Ups", "back"));
        exercises.add(createExercise("Barbell Row", "back"));
        exercises. add(createExercise("Bent Over Row", "back"));
        exercises. add(createExercise("Dumbbell Row", "back"));
        exercises.add(createExercise("Seated Cable Row", "back"));
        exercises.add(createExercise("Seated Row", "back"));
        exercises. add(createExercise("Lat Pulldown", "back"));
        exercises.add(createExercise("Face Pull", "back"));
        exercises. add(createExercise("Shrugs", "back"));
        exercises.add(createExercise("Romanian Deadlift", "back"));

        // ARMS EXERCISES
        exercises.add(createExercise("Barbell Curl", "arms"));
        exercises. add(createExercise("Dumbbell Curl", "arms"));
        exercises.add(createExercise("Hammer Curl", "arms"));
        exercises. add(createExercise("Triceps Pushdown", "arms"));
        exercises.add(createExercise("Skull Crushers", "arms"));
        exercises.add(createExercise("Overhead Triceps Extension", "arms"));
        exercises. add(createExercise("Overhead Press", "arms"));
        exercises. add(createExercise("Shoulder Press", "arms"));
        exercises.add(createExercise("Dumbbell Shoulder Press", "arms"));
        exercises.add(createExercise("Lateral Raise", "arms"));
        exercises. add(createExercise("Rear Delt Fly", "arms"));

        // LEGS EXERCISES
        exercises.add(createExercise("Squat", "legs"));
        exercises.add(createExercise("Back Squat", "legs"));
        exercises.add(createExercise("Front Squat", "legs"));
        exercises.add(createExercise("Hack Squat", "legs"));
        exercises.add(createExercise("Leg Press", "legs"));
        exercises.add(createExercise("Bulgarian Split Squat", "legs"));
        exercises.add(createExercise("Walking Lunges", "legs"));
        exercises.add(createExercise("Leg Curl", "legs"));
        exercises. add(createExercise("Leg Extension", "legs"));
        exercises. add(createExercise("Standing Calf Raise", "legs"));
        exercises. add(createExercise("Seated Calf Raise", "legs"));
        exercises.add(createExercise("Calf Raise", "legs"));

        return exercises;
    }

    public static List<Exercise> getExercisesByMuscleGroup(String muscleGroup) {
        List<Exercise> filtered = new ArrayList<>();
        for (Exercise ex : getAllExercises()) {
            if (ex.muscleGroup.equalsIgnoreCase(muscleGroup)) {
                filtered. add(ex);
            }
        }
        return filtered;
    }

    private static Exercise createExercise(String name, String muscleGroup) {
        Exercise ex = new Exercise();
        ex.id = "ex_" + name.toLowerCase().replace(" ", "_");
        ex.name = name;
        ex.muscleGroup = muscleGroup;
        ex.sets = 3;  // Default values
        ex.reps = 10;
        ex.weight = null;
        return ex;
    }
}