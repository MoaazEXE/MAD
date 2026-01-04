package com.example.bytelift;

import android.app.Application;
import com.example.bytelift.data.settings.SettingsRepository;

public class ByteLiftApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize settings repository and apply theme
        SettingsRepository settingsRepository = SettingsRepository.getInstance(this);
        settingsRepository. initializeThemeOnStartup();

    }
}