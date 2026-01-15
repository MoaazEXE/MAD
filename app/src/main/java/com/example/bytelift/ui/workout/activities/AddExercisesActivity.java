package com.example.bytelift.ui.workout.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;
import com.example.bytelift.ui.workout.adapters.ExerciseAdapter;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AddExercisesActivity extends AppCompatActivity {

    private ArrayList<Integer> selectedDays;
    private int currentDayIdx = 0;
    private HashMap<Integer, ArrayList<Exercise>> exercisesByDay = new HashMap<>();
    private String[] dayNames = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> currentExercises = new ArrayList<>();

    private boolean isEditMode = false;
    private Routine existingRoutine = null;

    private static final int REQUEST_EXERCISE_LIBRARY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercises);

        selectedDays = getIntent().getIntegerArrayListExtra("selectedDays");
        isEditMode = getIntent().getBooleanExtra("editMode", false);

        if (isEditMode) {
            existingRoutine = (Routine) getIntent().getSerializableExtra("routine");
            preloadExercises();
        } else {
            for (int d : selectedDays) {
                exercisesByDay.put(d, new ArrayList<>());
            }
        }

        RecyclerView recycler = findViewById(R.id.recycler_exercises);
        exerciseAdapter = new ExerciseAdapter(currentExercises, new ExerciseAdapter.OnExerciseActionListener() {
            @Override public void onEdit(Exercise e) { editExercise(e); }
            @Override public void onDelete(Exercise e) { removeExercise(e); }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(exerciseAdapter);

        updateDay();

        findViewById(R.id.btn_add_exercise).setOnClickListener(v -> openExerciseLibrary());
        findViewById(R.id.btn_next).setOnClickListener(v -> nextDayOrFinish());
    }

    private void preloadExercises() {
        String[] keys = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};
        if (existingRoutine == null || existingRoutine.days == null) return;

        for (int idx : selectedDays) {
            String key = keys[idx];
            List<Exercise> src = existingRoutine.days.get(key);
            ArrayList<Exercise> copy = new ArrayList<>();

            if (src != null) {
                for (Exercise e : src) {
                    Exercise c = new Exercise();
                    c.id = e.id;
                    c.name = e.name;
                    c.muscleGroup = e.muscleGroup;
                    c.sets = e.sets;
                    c.reps = e.reps;
                    c.weight = e.weight;
                    copy.add(c);
                }
            }
            exercisesByDay.put(idx, copy);
        }
    }

    private void updateDay() {
        int dayIndex = selectedDays.get(currentDayIdx);
        ((TextView) findViewById(R.id.tv_day)).setText(dayNames[dayIndex]);
        ((TextView) findViewById(R.id.tv_subtitle))
                .setText("Day " + (currentDayIdx + 1) + " of " + selectedDays.size());

        currentExercises.clear();
        currentExercises.addAll(exercisesByDay.get(dayIndex));
        exerciseAdapter.notifyDataSetChanged();

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setText(currentDayIdx < selectedDays.size() - 1 ? "Next Day" : "Review");
    }

    private void nextDayOrFinish() {
        if (currentDayIdx < selectedDays.size() - 1) {
            currentDayIdx++;
            updateDay();
            return;
        }

        Intent intent = new Intent(this, ReviewRoutineActivity.class);
        intent.putIntegerArrayListExtra("selectedDays", selectedDays);
        intent.putExtra("editMode", isEditMode);
        intent.putExtra("exercisesByDay", new Gson().toJson(exercisesByDay));

        if (isEditMode && existingRoutine != null) {
            existingRoutine.days = new HashMap<>();
            String[] keys = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};
            for (int idx : selectedDays) {
                existingRoutine.days.put(keys[idx], exercisesByDay.get(idx));
            }
            existingRoutine.updatedAt = System.currentTimeMillis();
            intent.putExtra("routine", existingRoutine);
        }

        startActivity(intent);
    }

    private void openExerciseLibrary() {
        int idx = selectedDays.get(currentDayIdx);
        Intent i = new Intent(this, ExerciseLibraryActivity.class);
        i.putExtra("dayIndex", idx);
        startActivityForResult(i, REQUEST_EXERCISE_LIBRARY);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQUEST_EXERCISE_LIBRARY && res == RESULT_OK && data != null) {
            Exercise e = (Exercise) data.getSerializableExtra("exercise");
            if (e != null) showConfigureExerciseDialog(e);
        }
    }

    private void editExercise(Exercise exercise) {
        showConfigureExerciseDialog(exercise);
    }

    private void showConfigureExerciseDialog(Exercise exercise) {
        View v = getLayoutInflater().inflate(R.layout.dialog_configure_exercise, null);

        NumberPicker sets = v.findViewById(R.id.picker_sets);
        NumberPicker reps = v.findViewById(R.id.picker_reps);
        EditText weight = v.findViewById(R.id.input_weight);

        sets.setMinValue(1); sets.setMaxValue(10); sets.setValue(exercise.sets);
        reps.setMinValue(1); reps.setMaxValue(50); reps.setValue(exercise.reps);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(exercise.name)
                .setView(v)
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button save = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            save.setOnClickListener(x -> {
                exercise.sets = sets.getValue();
                exercise.reps = reps.getValue();
                try { exercise.weight = Double.parseDouble(weight.getText().toString()); }
                catch (Exception e) { exercise.weight = null; }

                exerciseAdapter.notifyDataSetChanged();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void removeExercise(Exercise e) {
        int idx = selectedDays.get(currentDayIdx);
        exercisesByDay.get(idx).remove(e);
        currentExercises.remove(e);
        exerciseAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
    }
}
