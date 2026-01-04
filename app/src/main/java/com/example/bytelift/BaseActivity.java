package com.example.bytelift;

import android.os.Bundle;
import androidx.annotation. Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bytelift.data.settings.SettingsRepository;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Ensure theme is applied before onCreate
        SettingsRepository settingsRepository = SettingsRepository.getInstance(this);
        if (settingsRepository.getSettings() != null) {
            settingsRepository.initializeThemeOnStartup();
        }

        super.onCreate(savedInstanceState);
    }
}