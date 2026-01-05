package com.example.bytelift.ui.workout.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android. widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.utils.DataManager;
import java.util. List;
import java.util. Map;

public class RoutineDetailActivity extends AppCompatActivity {

    private Routine routine;
    private EditText etNotes;

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

        TextView tvName = findViewById(R.id. tv_routine_name);
        TextView tvDays = findViewById(R.id.tv_days_per_week);
        TextView tvType = findViewById(R.id.tv_type);
        LinearLayout layoutDays = findViewById(R.id.layout_days);
        etNotes = findViewById(R.id.et_notes);
        Button btnSaveNotes = findViewById(R.id.btn_save_notes);
        Button btnStart = findViewById(R.id.btn_start_workout);

        tvName.setText(routine. name);
        tvDays.setText(routine.daysPerWeek + " days/week");
        tvType.setText(routine. type. equals("custom") ? "CUSTOM" : "PRESET");

        // Load existing notes
        if (routine.notes != null && ! routine.notes.isEmpty()) {
            etNotes.setText(routine.notes);
        }

        // Display all days and exercises
        if (routine.days != null && !routine.days.isEmpty()) {
            for (Map. Entry<String, List<Exercise>> entry : routine.days.entrySet()) {
                String dayKey = entry.getKey();
                List<Exercise> exercises = entry. getValue();

                // Day label
                TextView dayLabel = new TextView(this);
                dayLabel.setText(capitalize(dayKey));
                dayLabel.setTextColor(getResources().getColor(R.color.gym_green));
                dayLabel.setTextSize(20f);
                dayLabel.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout. LayoutParams.WRAP_CONTENT,
                        LinearLayout. LayoutParams.WRAP_CONTENT
                );
                lp. setMargins(0, 16, 0, 12);
                dayLabel.setLayoutParams(lp);
                layoutDays.addView(dayLabel);

                // Exercises
                if (exercises != null) {
                    for (Exercise ex : exercises) {
                        TextView exView = new TextView(this);
                        String detail = "• " + ex.name + " (" + ex.sets + "x" + ex.reps;
                        if (ex.weight != null) detail += " • " + ex. weight + "kg";
                        detail += ")";
                        exView.setText(detail);
                        exView.setTextColor(getResources().getColor(R.color.text_white));
                        exView.setTextSize(15f);
                        exView.setPadding(24, 0, 0, 12);
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
        btnSaveNotes.setOnClickListener(v -> saveNotes());

        // Start workout button
        btnStart.setOnClickListener(v -> {
            Toast.makeText(this, "Start workout feature coming soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement workout tracking
        });
    }

    private void saveNotes() {
        String notes = etNotes.getText().toString().trim();

        // Update routine object
        routine.notes = notes;
        routine.updatedAt = System.currentTimeMillis();

        // ✅ Save to DataManager
        DataManager. updateRoutine(routine);

        Toast.makeText(this, "Notes saved!", Toast.LENGTH_SHORT).show();

        // Hide keyboard
        etNotes.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etNotes.getWindowToken(), 0);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}