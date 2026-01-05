package com.example.bytelift;

import android.app.Application;
import com.example.bytelift.data.settings.SettingsRepository;
import com.example.bytelift.ui.workout.utils.DataManager;
import com.example.bytelift.ui.workout.utils. FirebaseManager;

public class ByteLiftApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize settings repository and apply theme
        SettingsRepository settingsRepository = SettingsRepository. getInstance(this);
        settingsRepository. initializeThemeOnStartup();

        // ✅ Initialize Firebase Manager
        FirebaseManager.init();

        // ✅ Initialize DataManager (loads routines from local cache + Firebase sync)
        DataManager.init(this);
    }
}