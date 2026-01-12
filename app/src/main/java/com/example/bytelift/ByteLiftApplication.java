package com.example.bytelift;

import android.app.Application;

import com.example.bytelift.data.settings.SettingsRepository;

public class ByteLiftApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved theme as early as possible (Light/Dark/System)
        SettingsRepository.getInstance(getApplicationContext())
                .initializeThemeOnStartup();
    }
}
