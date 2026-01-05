package com.example.bytelift.ui.workout.activities;

import android.content.Intent;
import android.graphics. Typeface;
import android.os. Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget. LinearLayout;
import android.widget. TextView;
import android.widget. Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com. google.gson.reflect.TypeToken;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.utils.DataManager;
import java. lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class ReviewRoutineActivity extends AppCompatActivity {

    private ArrayList<Integer> selectedDays;
    private HashMap<Integer, ArrayList<Exercise>> exercisesByDay;
    private String[] dayNames = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private String[] dayDisplayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private boolean isEditMode = false;
    private Routine existingRoutine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_routine);

        // Check if edit mode
        isEditMode = getIntent().getBooleanExtra("editMode", false);

        if (isEditMode) {
            // Load existing routine
            existingRoutine = (Routine) getIntent().getSerializableExtra("routine");
            loadExistingRoutine();
        } else {
            // New routine from creation flow
            selectedDays = getIntent().getIntegerArrayListExtra("selectedDays");
            String json = getIntent().getStringExtra("exercisesByDay");

            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<Integer, ArrayList<Exercise>>>() {}.getType();
            exercisesByDay = gson.fromJson(json, type);
        }

        LinearLayout layoutDays = findViewById(R.id.layout_days);
        EditText etName = findViewById(R.id.et_routine_name);
        Button btnSave = findViewById(R.id. btn_save);
        TextView tvHeader = findViewById(R.id.tv_header);

        // Set title based on mode
        tvHeader.setText(isEditMode ?  "Edit Routine" : "Review Routine");

        // Pre-fill name if editing
        if (isEditMode && existingRoutine != null) {
            etName.setText(existingRoutine.name);
        }

        // Display days and exercises
        if (isEditMode) {
            displayEditModeExercises(layoutDays);
        } else {
            displayNewRoutineExercises(layoutDays);
        }

        btnSave.setOnClickListener(v -> {
            String routineName = etName.getText().toString().trim();
            if (routineName.isEmpty()) {
                etName.setError("Required");
                return;
            }

            saveRoutine(routineName);
        });
    }

    private void loadExistingRoutine() {
        // ✅ CHECK if we have updated data from AddExercisesActivity first
        String json = getIntent().getStringExtra("exercisesByDay");

        if (json != null && !json.isEmpty()) {
            // Use the updated data from AddExercisesActivity
            selectedDays = getIntent().getIntegerArrayListExtra("selectedDays");
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<Integer, ArrayList<Exercise>>>() {}.getType();
            exercisesByDay = gson.fromJson(json, type);
        } else {
            // Fallback:  Convert existing routine days (for direct edits)
            selectedDays = new ArrayList<>();
            exercisesByDay = new HashMap<>();

            if (existingRoutine. days != null) {
                for (int i = 0; i < dayNames.length; i++) {
                    if (existingRoutine.days.containsKey(dayNames[i])) {
                        selectedDays.add(i);
                        ArrayList<Exercise> exercises = new ArrayList<>(existingRoutine.days.get(dayNames[i]));
                        exercisesByDay.put(i, exercises);
                    }
                }
            }
        }
    }

    private void displayNewRoutineExercises(LinearLayout layoutDays) {
        for (int dayIdx : selectedDays) {
            addDaySection(layoutDays, dayIdx);
        }
    }

    private void displayEditModeExercises(LinearLayout layoutDays) {
        for (int dayIdx : selectedDays) {
            addDaySection(layoutDays, dayIdx);
        }
    }

    private void addDaySection(LinearLayout layoutDays, int dayIdx) {
        TextView dayLabel = new TextView(this);
        dayLabel.setText(dayDisplayNames[dayIdx]);
        dayLabel.setTextColor(getResources().getColor(R.color.gym_green));
        dayLabel.setTextSize(20f);
        dayLabel.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout. LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 16, 0, 12);
        dayLabel.setLayoutParams(lp);
        layoutDays.addView(dayLabel);

        ArrayList<Exercise> exercises = exercisesByDay.get(dayIdx);
        if (exercises != null) {
            for (Exercise ex : exercises) {
                TextView exView = new TextView(this);
                String detail = "• " + ex.name + " (" + ex.sets + "x" + ex.reps;
                if (ex.weight != null) detail += " • " + ex.weight + "kg";
                detail += ")";
                exView.setText(detail);
                exView.setTextColor(getResources().getColor(R.color.text_white));
                exView.setTextSize(15f);
                exView.setPadding(24, 0, 0, 12);
                layoutDays.addView(exView);
            }
        }
    }

    private void saveRoutine(String name) {
        Routine routine;

        if (isEditMode && existingRoutine != null) {
            routine = existingRoutine;
            routine.name = name;
            routine.updatedAt = System. currentTimeMillis();
        } else {
            routine = new Routine();
            routine.routineId = "routine_" + System.currentTimeMillis();
            routine.name = name;
            routine.type = "custom";
            routine.createdAt = System.currentTimeMillis();
            routine.updatedAt = routine. createdAt;
        }

        routine.daysPerWeek = selectedDays.size();
        routine.days = new HashMap<>();
        for (int dayIdx : selectedDays) {
            routine.days.put(dayNames[dayIdx], exercisesByDay.get(dayIdx));
        }

        // ✅ Save in BOTH new and edit mode
        if (isEditMode) {
            DataManager.updateRoutine(routine);  // Update existing
        } else {
            DataManager. addRoutine(routine);      // Add new
        }

        Toast.makeText(this, isEditMode ? "Routine updated!" :  "Routine saved!", Toast.LENGTH_SHORT).show();

        // ✅ Clear all activities and go directly to WorkoutActivity
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}