package com.example.bytelift.ui.workout.models;

import java.io.Serializable;

public class Exercise implements Serializable {
    public String id;
    public String name;
    public String muscleGroup; // "back", "arms", "legs", "chest"
    public int sets;
    public int reps;
    public Double weight; // nullable

    public Exercise() {}

    public Exercise(String id, String name, String muscleGroup, int sets, int reps, Double weight) {
        this.id = id;
        this. name = name;
        this. muscleGroup = muscleGroup;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }
}
