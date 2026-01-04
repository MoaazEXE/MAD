package com.example.bytelift.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle. MutableLiveData;

public class SettingsRepository {
    private static SettingsRepository instance;
    private final SharedPreferences prefs;
    private final MutableLiveData<SettingsState> settingsLiveData;

    private SettingsRepository(Context context) {
        prefs = context. getApplicationContext()
                .getSharedPreferences(SettingsKeys.PREFS_NAME, Context.MODE_PRIVATE);
        settingsLiveData = new MutableLiveData<>();
        loadSettings();
    }

    public static synchronized SettingsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsRepository(context);
        }
        return instance;
    }

    public LiveData<SettingsState> getSettingsLiveData() {
        return settingsLiveData;
    }

    public SettingsState getSettings() {
        return settingsLiveData. getValue();
    }

    private void loadSettings() {
        SettingsState. Theme theme = SettingsState. Theme.fromString(
                prefs.getString(SettingsKeys.KEY_THEME, SettingsKeys.DEFAULT_THEME)
        );

        String language = prefs.getString(SettingsKeys.KEY_LANGUAGE, SettingsKeys.DEFAULT_LANGUAGE);

        SettingsState.WeightUnit weightUnit = SettingsState.WeightUnit.fromString(
                prefs.getString(SettingsKeys.KEY_WEIGHT_UNIT, SettingsKeys.DEFAULT_WEIGHT_UNIT)
        );

        SettingsState.HeightUnit heightUnit = SettingsState.HeightUnit.fromString(
                prefs.getString(SettingsKeys.KEY_HEIGHT_UNIT, SettingsKeys.DEFAULT_HEIGHT_UNIT)
        );

        boolean notificationsEnabled = prefs. getBoolean(
                SettingsKeys.KEY_NOTIFICATIONS_ENABLED, SettingsKeys. DEFAULT_NOTIFICATIONS
        );

        boolean workoutReminders = prefs.getBoolean(
                SettingsKeys.KEY_WORKOUT_REMINDERS, SettingsKeys.DEFAULT_WORKOUT_REMINDERS
        );

        String reminderDays = prefs.getString(
                SettingsKeys.KEY_REMINDER_DAYS, SettingsKeys.DEFAULT_REMINDER_DAYS
        );

        String reminderTime = prefs.getString(
                SettingsKeys.KEY_REMINDER_TIME, SettingsKeys.DEFAULT_REMINDER_TIME
        );

        SettingsState state = new SettingsState(
                theme, language, weightUnit, heightUnit,
                notificationsEnabled, workoutReminders, reminderDays, reminderTime
        );

        settingsLiveData.setValue(state);
    }

    public void setTheme(SettingsState.Theme theme) {
        prefs.edit().putString(SettingsKeys. KEY_THEME, theme.name()).apply();
        SettingsState currentState = settingsLiveData. getValue();
        if (currentState != null) {
            currentState.setTheme(theme);
            settingsLiveData.setValue(currentState);
        }
        applyTheme(theme);
    }

    private void applyTheme(SettingsState. Theme theme) {
        int mode;
        switch (theme) {
            case LIGHT:
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case DARK:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case SYSTEM:
            default:
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public void setWeightUnit(SettingsState.WeightUnit unit) {
        prefs.edit().putString(SettingsKeys.KEY_WEIGHT_UNIT, unit.getDisplayName()).apply();
        SettingsState currentState = settingsLiveData. getValue();
        if (currentState != null) {
            currentState.setWeightUnit(unit);
            settingsLiveData. setValue(currentState);
        }
    }

    public void setHeightUnit(SettingsState.HeightUnit unit) {
        String value = unit == SettingsState.HeightUnit.FT_IN ?  "ft" : "cm";
        prefs.edit().putString(SettingsKeys.KEY_HEIGHT_UNIT, value).apply();
        SettingsState currentState = settingsLiveData. getValue();
        if (currentState != null) {
            currentState.setHeightUnit(unit);
            settingsLiveData. setValue(currentState);
        }
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(SettingsKeys.KEY_NOTIFICATIONS_ENABLED, enabled).apply();
        SettingsState currentState = settingsLiveData.getValue();
        if (currentState != null) {
            currentState.setNotificationsEnabled(enabled);
            settingsLiveData.setValue(currentState);
        }
    }

    public void setWorkoutReminders(boolean enabled) {
        prefs.edit().putBoolean(SettingsKeys.KEY_WORKOUT_REMINDERS, enabled).apply();
        SettingsState currentState = settingsLiveData.getValue();
        if (currentState != null) {
            currentState.setWorkoutReminders(enabled);
            settingsLiveData.setValue(currentState);
        }
    }

    public void setReminderSchedule(String days, String time) {
        prefs.edit()
                .putString(SettingsKeys.KEY_REMINDER_DAYS, days)
                .putString(SettingsKeys. KEY_REMINDER_TIME, time)
                .apply();
        SettingsState currentState = settingsLiveData.getValue();
        if (currentState != null) {
            currentState.setReminderDays(days);
            currentState.setReminderTime(time);
            settingsLiveData.setValue(currentState);
        }
    }

    public void clearAllSettings() {
        prefs.edit().clear().apply();
        loadSettings();
    }

    public void initializeThemeOnStartup() {
        SettingsState state = settingsLiveData.getValue();
        if (state != null) {
            applyTheme(state.getTheme());
        }
    }
}