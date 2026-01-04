package com.example.bytelift. data.settings;

public class SettingsState {
    private Theme theme;
    private WeightUnit weightUnit;
    private HeightUnit heightUnit;
    private boolean notificationsEnabled;
    private boolean workoutReminders;
    private String reminderDays;
    private String reminderTime;

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

        public static Theme fromString(String value) {
            try {
                return valueOf(value);
            } catch (IllegalArgumentException e) {
                return SYSTEM;
            }
        }
    }

    public enum WeightUnit {
        KG("kg"),
        LB("lb");

        private final String displayName;

        WeightUnit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static WeightUnit fromString(String value) {
            return value. equalsIgnoreCase("lb") ? LB : KG;
        }
    }

    public enum HeightUnit {
        CM("cm"),
        FT_IN("ft & in");

        private final String displayName;

        HeightUnit(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static HeightUnit fromString(String value) {
            return value.equalsIgnoreCase("ft") ? FT_IN : CM;
        }
    }

    // Constructor
    public SettingsState(Theme theme, String language, WeightUnit weightUnit,
                         HeightUnit heightUnit, boolean notificationsEnabled,
                         boolean workoutReminders, String reminderDays, String reminderTime) {
        this.theme = theme;
        this.weightUnit = weightUnit;
        this.heightUnit = heightUnit;
        this.notificationsEnabled = notificationsEnabled;
        this.workoutReminders = workoutReminders;
        this.reminderDays = reminderDays;
        this.reminderTime = reminderTime;
    }

    // Getters
    public Theme getTheme() { return theme; }
    public WeightUnit getWeightUnit() { return weightUnit; }
    public HeightUnit getHeightUnit() { return heightUnit; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public boolean isWorkoutReminders() { return workoutReminders; }
    public String getReminderDays() { return reminderDays; }
    public String getReminderTime() { return reminderTime; }

    // Setters
    public void setTheme(Theme theme) { this.theme = theme; }
    public void setWeightUnit(WeightUnit weightUnit) { this.weightUnit = weightUnit; }
    public void setHeightUnit(HeightUnit heightUnit) { this.heightUnit = heightUnit; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
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
        return reminderDays. replace(",", ", ") + " at " + reminderTime;
    }
}