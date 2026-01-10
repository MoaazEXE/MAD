package com.example.bytelift.ui. workout.activities;

import android. graphics. Typeface;
import android.os. Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android. widget.LinearLayout;
import android.widget. ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example. bytelift.R;
import com.example.bytelift. ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import com. example.bytelift.ui. workout.utils.DataManager;
import com. example.bytelift.ui.workout.models.StreakData;
import com.example.bytelift.utils. StreakManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutineDetailActivity extends AppCompatActivity {

    private Routine routine;
    private EditText etNotes;
    private Button btnStart;
    private TextView tvActiveBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);

        routine = (Routine) getIntent().getSerializableExtra("routine");

        if (routine == null) {
            Toast.makeText(this, "Error loading routine", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tv_routine_name);
        TextView tvDays = findViewById(R.id. tv_days_per_week);
        TextView tvType = findViewById(R.id.tv_type);
        LinearLayout layoutDays = findViewById(R.id.layout_days);
        etNotes = findViewById(R.id.et_notes);
        Button btnSaveNotes = findViewById(R.id. btn_save_notes);
        btnStart = findViewById(R.id. btn_start_workout);
        tvActiveBadge = findViewById(R.id.tv_active_badge);

        tvName.setText(routine.name);
        tvDays.setText(routine. daysPerWeek + " days/week");
        tvType.setText(routine.type. equals("custom") ? "CUSTOM" : "PRESET");

        // Update button appearance based on active status
        updateButtonAppearance();

        // Load existing notes
        if (routine.notes != null && ! routine.notes.isEmpty()) {
            etNotes.setText(routine.notes);
        }

        // Display all days and exercises
        if (routine.days != null && ! routine.days.isEmpty()) {
            for (Map.Entry<String, List<Exercise>> entry : routine.days.entrySet()) {
                String dayKey = entry.getKey();
                List<Exercise> exercises = entry. getValue();

                // Day label
                TextView dayLabel = new TextView(this);
                dayLabel.setText(capitalize(dayKey));
                dayLabel.setTextColor(getResources().getColor(R.color.gym_green));
                dayLabel.setTextSize(20f);
                dayLabel.setTypeface(null, Typeface. BOLD);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout. LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, 16, 0, 12);
                dayLabel.setLayoutParams(lp);
                layoutDays.addView(dayLabel);

                // Exercises
                if (exercises != null) {
                    for (Exercise ex : exercises) {
                        TextView exView = new TextView(this);
                        String detail = "• " + ex.name + " (" + ex.sets + "x" + ex.reps;
                        if (ex.weight != null) detail += " • " + ex.weight + "kg";
                        detail += ")";
                        exView.setText(detail);
                        exView.setTextColor(getResources().getColor(R.color.text_white));
                        exView.setTextSize(15f);
                        exView. setPadding(24, 0, 0, 12);
                        layoutDays. addView(exView);
                    }
                }
            }
        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText("No exercises available");
            placeholder.setTextColor(getResources().getColor(R.color.text_muted));
            layoutDays.addView(placeholder);
        }

        // Save notes button
        btnSaveNotes. setOnClickListener(v -> saveNotes());

        // ✅ Start workout button
        btnStart.setOnClickListener(v -> {
            // Step 1: Set as active if not already
            boolean wasNotActive = ! routine.isActiveForStreak;

            if (! routine.isActiveForStreak) {
                StreakManager.setActiveRoutine(this, routine. routineId);
                routine.isActiveForStreak = true;
                updateButtonAppearance();
            }

            // Step 2: Check if already logged today
            if (StreakManager.isWorkoutLoggedToday(this)) {
                // Already logged - just show progress dialog
                showWorkoutLoggedDialog(wasNotActive);
            } else {
                // Not logged yet - show day selection dialog
                showSelectWorkoutDayDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (routine != null) {
            Routine updatedRoutine = DataManager.getRoutineById(routine.routineId);
            if (updatedRoutine != null) {
                routine = updatedRoutine;
                updateButtonAppearance();
            }
        }
    }

    // ✅ Update button text and color
    private void updateButtonAppearance() {
        if (routine. isActiveForStreak) {
            btnStart.setText("Start Workout");
            btnStart.setBackgroundColor(getResources().getColor(R.color.gym_green));

            if (tvActiveBadge != null) {
                tvActiveBadge. setVisibility(android.view.View.VISIBLE);
            }
        } else {
            btnStart.setText("Set as Active & Start");
            btnStart.setBackgroundColor(getResources().getColor(R.color.gym_green_dark));

            if (tvActiveBadge != null) {
                tvActiveBadge. setVisibility(android.view. View.GONE);
            }
        }
    }

    // ✅ Show dialog to select which day to log
    private void showSelectWorkoutDayDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout. dialog_select_workout_day, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout layoutDays = dialogView.findViewById(R.id. layout_workout_days);

        // Get scheduled days from routine
        List<String> scheduledDays = StreakManager.getScheduledDays(this);
        StreakData streakData = StreakManager. getStreakData(this);

        if (scheduledDays. isEmpty() && routine.days != null) {
            scheduledDays = new ArrayList<>(routine.days.keySet());
        }

        // Create button for each day
        for (String dayKey : scheduledDays) {
            android.view.View dayItem = inflater.inflate(R. layout.workout_day_item, layoutDays, false);

            TextView tvIcon = dayItem.findViewById(R. id.tv_status_icon);
            TextView tvDayName = dayItem.findViewById(R. id.tv_day_name);
            TextView tvStatus = dayItem.findViewById(R. id.tv_day_status);

            String dayName = capitalize(dayKey);
            tvDayName.setText(dayName);

            // Simple completion check (completed count vs position)
            int dayIndex = new ArrayList<>(scheduledDays).indexOf(dayKey);
            boolean isCompleted = streakData.workoutsCompletedThisWeek > dayIndex;

            if (isCompleted) {
                tvIcon.setText("✅");
                tvStatus.setText("Completed");
                tvStatus.setTextColor(getResources().getColor(R.color.gym_green));
            } else {
                tvIcon. setText("⬜");
                tvStatus.setText("Not completed");
                tvStatus.setTextColor(getResources().getColor(R.color.text_muted));
            }

            // Click listener
            dayItem.setOnClickListener(v -> {
                dialog.dismiss();
                StreakManager.markWorkoutComplete(this, routine.routineId, dayName);
                showWorkoutLoggedDialog(false);
            });

            layoutDays.addView(dayItem);
        }

        // Cancel button
        android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // ✅ Show workout logged dialog
    private void showWorkoutLoggedDialog(boolean justSetActive) {
        android.app. AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view. LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_workout_logged, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String progress = StreakManager.getWeekProgress(this);
        int currentStreak = StreakManager. getCurrentStreak(this);
        int longestStreak = StreakManager.getLongestStreak(this);
        int workoutsRemaining = StreakManager.getWorkoutsRemaining(this);
        boolean weekComplete = StreakManager.isWeekComplete(this);

        // Calculate progress percentage
        String[] parts = progress.split(" of ");
        int completed = Integer.parseInt(parts[0]);
        int total = Integer. parseInt(parts[1]. split(" ")[0]);
        int percentage = (int) ((completed / (float) total) * 100);

        // Set routine name
        TextView tvRoutineName = dialogView.findViewById(R.id.tv_dialog_routine_name);
        tvRoutineName.setText(routine. name);

        // Set progress
        TextView tvProgress = dialogView. findViewById(R.id.tv_dialog_progress);
        tvProgress.setText(progress);

        // Set progress bar
        ProgressBar progressBar = dialogView.findViewById(R. id.progress_bar);
        progressBar. setProgress(percentage);

        // Set streaks
        TextView tvCurrentStreak = dialogView.findViewById(R.id.tv_dialog_current_streak);
        tvCurrentStreak.setText(currentStreak + " weeks");

        TextView tvLongestStreak = dialogView.findViewById(R. id.tv_dialog_longest_streak);
        tvLongestStreak.setText(longestStreak + " weeks");

        // Set motivation message
        TextView tvMessage = dialogView.findViewById(R. id.tv_dialog_message);
        if (justSetActive) {
            tvMessage.setText("✅ " + routine.name + " is now your active routine!\n\n" +
                    "Complete " + workoutsRemaining + " more workout" +
                    (workoutsRemaining == 1 ? "" : "s") + " this week!");
        } else if (weekComplete) {
            tvMessage.setText("✨ Week complete! Amazing work!\n\nKeep this streak alive next week!  🔥");
        } else {
            tvMessage.setText("⏰ " + workoutsRemaining + " more workout" +
                    (workoutsRemaining == 1 ? "" : "s") + " to maintain your streak!");
        }

        // Close button
        Button btnClose = dialogView.findViewById(R.id.btn_dialog_close);
        btnClose.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void saveNotes() {
        String notes = etNotes.getText().toString().trim();
        routine.notes = notes;
        routine.updatedAt = System.currentTimeMillis();
        DataManager.updateRoutine(routine);
        Toast.makeText(this, "Notes saved!", Toast.LENGTH_SHORT).show();

        etNotes.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etNotes.getWindowToken(), 0);
    }

    private String capitalize(String str) {
        if (str == null || str. isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}