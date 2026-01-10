package com.example.bytelift.ui.workout.models;

import java.util.ArrayList;
import java.util.List;

public class StreakData {
    public int currentStreak;
    public int longestStreak;
    public long weekStartDate;
    public int workoutsRequiredThisWeek;
    public int workoutsCompletedThisWeek;
    public List<String> workoutDatesThisWeek;
    public String userId;
    public long lastUpdated;
    public String activeRoutineId = "";  // ✅ NEW FIELD

    public StreakData() {
        this.currentStreak = 0;
        this.longestStreak = 0;
        this. weekStartDate = 0;
        this.workoutsRequiredThisWeek = 0;
        this.workoutsCompletedThisWeek = 0;
        this.workoutDatesThisWeek = new ArrayList<>();
        this.userId = "";
        this.lastUpdated = System.currentTimeMillis();
        this.activeRoutineId = "";  // ✅ Initialize
    }
}