package com.example.bytelift.ui.settings_page;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget. TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx. appcompat.app.AppCompatActivity;

import com.example.bytelift.BaseActivity;
import com.example.bytelift.R;
import com.example.bytelift.data.settings.SettingsRepository;
import com.example.bytelift.data.settings.SettingsState;
import com.example.bytelift.databinding.ActivityReminderScheduleBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util. Locale;

public class ReminderScheduleActivity extends BaseActivity {

    private ActivityReminderScheduleBinding binding;
    private SettingsRepository settingsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reminder Schedule");
        }

        settingsRepository = SettingsRepository.getInstance(this);

        loadCurrentSettings();
        setupClickListeners();
    }

    private void loadCurrentSettings() {
        SettingsState settings = settingsRepository.getSettings();
        if (settings == null) return;

        // Load selected days
        String reminderDays = settings.getReminderDays();
        if (reminderDays != null && !reminderDays.isEmpty()) {
            String[] days = reminderDays.split(",");
            for (String day : days) {
                setDayChecked(day.trim(), true);
            }
        }

        // Load time
        String reminderTime = settings.getReminderTime();
        if (reminderTime != null && !reminderTime.isEmpty()) {
            String[] timeParts = reminderTime.split(":");
            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                binding.timePicker.setHour(hour);
                binding. timePicker.setMinute(minute);
            }
        }
    }

    private void setDayChecked(String day, boolean checked) {
        switch (day) {
            case "MON":  binding.checkMonday.setChecked(checked); break;
            case "TUE": binding.checkTuesday.setChecked(checked); break;
            case "WED":  binding.checkWednesday.setChecked(checked); break;
            case "THU": binding. checkThursday.setChecked(checked); break;
            case "FRI": binding.checkFriday.setChecked(checked); break;
            case "SAT": binding.checkSaturday.setChecked(checked); break;
            case "SUN":  binding.checkSunday.setChecked(checked); break;
        }
    }

    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> saveSchedule());
    }

    private void saveSchedule() {
        List<String> selectedDays = new ArrayList<>();

        if (binding.checkMonday.isChecked()) selectedDays.add("MON");
        if (binding.checkTuesday.isChecked()) selectedDays.add("TUE");
        if (binding.checkWednesday. isChecked()) selectedDays.add("WED");
        if (binding.checkThursday. isChecked()) selectedDays.add("THU");
        if (binding.checkFriday. isChecked()) selectedDays.add("FRI");
        if (binding.checkSaturday. isChecked()) selectedDays.add("SAT");
        if (binding.checkSunday. isChecked()) selectedDays.add("SUN");

        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        String days = String.join(",", selectedDays);
        String time = String.format(Locale.US, "%02d:%02d",
                binding.timePicker. getHour(),
                binding.timePicker. getMinute());

        settingsRepository.setReminderSchedule(days, time);

        Toast.makeText(this, "Reminder schedule saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}