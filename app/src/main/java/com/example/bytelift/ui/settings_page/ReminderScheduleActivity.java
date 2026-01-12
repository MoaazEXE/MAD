package com.example.bytelift.ui.settings_page;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bytelift.BaseActivity;
import com.example.bytelift.data.settings.SettingsRepository;
import com.example.bytelift.data.settings.SettingsState;
import com.example.bytelift.databinding.ActivityReminderScheduleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /**
     * Reads saved reminder days/time from SettingsRepository and updates the UI:
     * - ticks the correct day checkboxes
     * - sets the TimePicker to saved hour/minute
     */
    private void loadCurrentSettings() {
        SettingsState settings = settingsRepository.getSettings();
        if (settings == null) return;

        // Load selected days (stored like "MON,WED,FRI")
        String reminderDays = settings.getReminderDays();
        if (reminderDays != null && !reminderDays.isEmpty()) {
            String[] days = reminderDays.split(",");
            for (String day : days) {
                setDayChecked(day.trim(), true);
            }
        }

        // Load saved time (stored like "07:00")
        String reminderTime = settings.getReminderTime();
        if (reminderTime != null && !reminderTime.isEmpty()) {
            String[] timeParts = reminderTime.split(":");
            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                // TimePicker in 24h mode: these APIs require API 23+
                binding.timePicker.setHour(hour);
                binding.timePicker.setMinute(minute);
            }
        }
    }

    /**
     * Helper to tick/untick the right checkbox based on the short day code.
     */
    private void setDayChecked(String day, boolean checked) {
        switch (day) {
            case "MON":
                binding.checkMonday.setChecked(checked);
                break;
            case "TUE":
                binding.checkTuesday.setChecked(checked);
                break;
            case "WED":
                binding.checkWednesday.setChecked(checked);
                break;
            case "THU":
                binding.checkThursday.setChecked(checked);
                break;
            case "FRI":
                binding.checkFriday.setChecked(checked);
                break;
            case "SAT":
                binding.checkSaturday.setChecked(checked);
                break;
            case "SUN":
                binding.checkSunday.setChecked(checked);
                break;
        }
    }

    /**
     * Wire the Save button.
     */
    private void setupClickListeners() {
        binding.btnSave.setOnClickListener(v -> saveSchedule());
    }

    /**
     * Collects:
     * - selected day checkboxes into "MON,WED,FRI"
     * - time picker into "HH:mm"
     * Saves into SettingsRepository, shows toast, closes the screen.
     */
    private void saveSchedule() {
        List<String> selectedDays = new ArrayList<>();

        if (binding.checkMonday.isChecked()) selectedDays.add("MON");
        if (binding.checkTuesday.isChecked()) selectedDays.add("TUE");
        if (binding.checkWednesday.isChecked()) selectedDays.add("WED");
        if (binding.checkThursday.isChecked()) selectedDays.add("THU");
        if (binding.checkFriday.isChecked()) selectedDays.add("FRI");
        if (binding.checkSaturday.isChecked()) selectedDays.add("SAT");
        if (binding.checkSunday.isChecked()) selectedDays.add("SUN");

        // Must have at least 1 day selected
        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store day list like: "MON,WED,FRI"
        String days = String.join(",", selectedDays);

        // Store time like: "07:00"
        String time = String.format(
                Locale.US,
                "%02d:%02d",
                binding.timePicker.getHour(),
                binding.timePicker.getMinute()
        );

        settingsRepository.setReminderSchedule(days, time);

        Toast.makeText(this, "Reminder schedule saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Handles the back arrow in the top app bar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
