package com.example.bytelift.data.settings;

import java.util.Locale;

public class SettingsState {

    private Theme theme;
    private WeightUnit weightUnit;
    private HeightUnit heightUnit;
    private boolean notificationsEnabled;
    private boolean workoutReminders;
    private String reminderDays;
    private String reminderTime;

    // ✅ Required: NO-ARG constructor (fixes "required: no arguments")
    public SettingsState() {
        this.theme = Theme.SYSTEM;
        this.weightUnit = WeightUnit.KG;
        this.heightUnit = HeightUnit.CM;
        this.notificationsEnabled = true;
        this.workoutReminders = false;
        this.reminderDays = "MON,WED,FRI";
        this.reminderTime = "19:00";
    }

    // ✅ Required: constructor signature your SettingsRepository expects
    public SettingsState(
            Theme theme,
            WeightUnit weightUnit,
            HeightUnit heightUnit,
            boolean notificationsEnabled,
            boolean workoutReminders,
            String reminderDays,
            String reminderTime
    ) {
        this.theme = theme;
        this.weightUnit = weightUnit;
        this.heightUnit = heightUnit;
        this.notificationsEnabled = notificationsEnabled;
        this.workoutReminders = workoutReminders;
        this.reminderDays = reminderDays;
        this.reminderTime = reminderTime;

        // if notifications off, reminders must be off
        if (!notificationsEnabled) this.workoutReminders = false;
    }

    public enum Theme {
        SYSTEM("System"),
        LIGHT("Light"),
        DARK("Dark");

        private final String displayName;

        Theme(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        // ✅ Safe parsing (supports "SYSTEM", "System", "Light", etc.)
        public static Theme fromString(String value) {
            if (value == null) return SYSTEM;

            String v = value.trim();
            if (v.equalsIgnoreCase("System")) return SYSTEM;
            if (v.equalsIgnoreCase("Light")) return LIGHT;
            if (v.equalsIgnoreCase("Dark")) return DARK;

            // try enum names
            try {
                return Theme.valueOf(v.toUpperCase(Locale.US));
            } catch (Exception e) {
                return SYSTEM;
            }
        }
    }

    public enum WeightUnit {
        KG("Kilograms (kg)"),
        LB("Pounds (lb)");

        private final String displayName;

        WeightUnit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static WeightUnit fromString(String value) {
            if (value == null) return KG;

            String v = value.trim();

            // accept enum-like values
            if (v.equalsIgnoreCase("KG")) return KG;
            if (v.equalsIgnoreCase("LB") || v.equalsIgnoreCase("LBS")) return LB;

            // accept short strings
            if (v.equalsIgnoreCase("kg")) return KG;
            if (v.equalsIgnoreCase("lb")) return LB;

            // accept display strings
            if (v.equalsIgnoreCase("Kilograms (kg)") || v.equalsIgnoreCase("Kilograms")) return KG;
            if (v.equalsIgnoreCase("Pounds (lb)") || v.equalsIgnoreCase("Pounds")) return LB;

            return KG;
        }
    }

    public enum HeightUnit {
        CM("Centimeters (cm)"),
        FT_IN("Feet & Inches (ft)");

        private final String displayName;

        HeightUnit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static HeightUnit fromString(String value) {
            if (value == null) return CM;

            String v = value.trim();

            // accept enum-like values
            if (v.equalsIgnoreCase("CM")) return CM;
            if (v.equalsIgnoreCase("FT_IN") || v.equalsIgnoreCase("FT") || v.equalsIgnoreCase("FEET")) return FT_IN;

            // accept short strings
            if (v.equalsIgnoreCase("cm")) return CM;
            if (v.equalsIgnoreCase("ft")) return FT_IN;

            // accept display strings
            if (v.equalsIgnoreCase("Centimeters (cm)") || v.equalsIgnoreCase("Centimeters")) return CM;
            if (v.equalsIgnoreCase("Feet & Inches (ft)") || v.equalsIgnoreCase("Feet & Inches")) return FT_IN;

            return CM;
        }
    }

    // Getters
    public Theme getTheme() { return theme; }
    public WeightUnit getWeightUnit() { return weightUnit; }
    public HeightUnit getHeightUnit() { return heightUnit; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }

    // ✅ This matches your Fragment/ViewModel call
    public boolean isWorkoutReminders() { return workoutReminders; }

    public String getReminderDays() { return reminderDays; }
    public String getReminderTime() { return reminderTime; }

    // Setters
    public void setTheme(Theme theme) { this.theme = theme; }
    public void setWeightUnit(WeightUnit weightUnit) { this.weightUnit = weightUnit; }
    public void setHeightUnit(HeightUnit heightUnit) { this.heightUnit = heightUnit; }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
        if (!notificationsEnabled) this.workoutReminders = false;
    }

    public void setWorkoutReminders(boolean workoutReminders) {
        this.workoutReminders = workoutReminders;
    }

    public void setReminderDays(String reminderDays) { this.reminderDays = reminderDays; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public String getFormattedReminderSchedule() {
        if (reminderDays == null || reminderDays.isEmpty() ||
                reminderTime == null || reminderTime.isEmpty()) {
            return "Not set";
        }
        return reminderDays.replace(",", ", ") + " at " + reminderTime;
    }
}
