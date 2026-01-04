package com.example.bytelift.data.settings;

public class SettingsKeys {
    // Preferences file name
    public static final String PREFS_NAME = "bytelift_settings";

    // Keys
    public static final String KEY_THEME = "theme";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_WEIGHT_UNIT = "weight_unit";
    public static final String KEY_HEIGHT_UNIT = "height_unit";
    public static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String KEY_WORKOUT_REMINDERS = "workout_reminders";
    public static final String KEY_REMINDER_DAYS = "reminder_days"; // Comma-separated:  "MON,WED,FRI"
    public static final String KEY_REMINDER_TIME = "reminder_time"; // HH:mm format:  "07:00"
    public static final String KEY_FIRST_LAUNCH = "first_launch";

    // Default values
    public static final String DEFAULT_THEME = "SYSTEM";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_WEIGHT_UNIT = "kg";
    public static final String DEFAULT_HEIGHT_UNIT = "cm";
    public static final boolean DEFAULT_NOTIFICATIONS = true;
    public static final boolean DEFAULT_WORKOUT_REMINDERS = true;
    public static final String DEFAULT_REMINDER_DAYS = "MON,WED,FRI";
    public static final String DEFAULT_REMINDER_TIME = "07:00";
}