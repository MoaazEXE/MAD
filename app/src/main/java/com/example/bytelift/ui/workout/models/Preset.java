package com.example.bytelift.ui.workout.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Preset implements Serializable {
    public String presetId;
    public String name;
    public String level;
    public int daysPerWeek;
    public String description;
    public Map<String, List<Exercise>> days;  // Will use LinkedHashMap
}
