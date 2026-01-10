package com.example.bytelift. ui.workout.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Routine implements Serializable {
    public String routineId;
    public String userId;
    public String name;
    public String type;
    public int daysPerWeek;
    public Map<String, List<Exercise>> days;
    public long createdAt;
    public long updatedAt;
    public String notes;
    public boolean isActiveForStreak = false;  // ✅ NEW FIELD

    public Routine() {}
}