package com.example.bytelift;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bytelift.data.settings.SettingsRepository;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply theme BEFORE super.onCreate so it takes effect immediately
        SettingsRepository repo = SettingsRepository.getInstance(getApplicationContext());
        repo.initializeThemeOnStartup();

        super.onCreate(savedInstanceState);
    }
}
