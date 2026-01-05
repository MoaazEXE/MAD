package com.example.bytelift.ui.workout.activities;

import android.app.AlertDialog;
import android. content.Intent;
import android. os.Bundle;
import android. text.InputType;
import android. view.View;
import android. widget.Button;
import android.widget.EditText;
import android.widget. LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app. AppCompatActivity;
import androidx. recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview. widget.RecyclerView;
import com.google.gson. Gson;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.adapters. ExerciseAdapter;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Routine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AddExercisesActivity extends AppCompatActivity {

    private ArrayList<Integer> selectedDays;
    private int currentDayIdx = 0;
    private HashMap<Integer, ArrayList<Exercise>> exercisesByDay = new HashMap<>();
    private String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
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
            for (int day : selectedDays) {
                exercisesByDay.put(day, new ArrayList<>());
            }
        }

        RecyclerView recycler = findViewById(R.id.recycler_exercises);

        // ✅ UPDATED: Use new action listener interface
        exerciseAdapter = new ExerciseAdapter(currentExercises, new ExerciseAdapter.OnExerciseActionListener() {
            @Override
            public void onEdit(Exercise exercise) {
                editExercise(exercise);
            }

            @Override
            public void onDelete(Exercise exercise) {
                removeExercise(exercise);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(exerciseAdapter);

        updateDay();

        findViewById(R.id.btn_add_exercise).setOnClickListener(v -> openExerciseLibrary());
        findViewById(R.id.btn_next).setOnClickListener(v -> nextDayOrFinish());
    }

    private void preloadExercises() {
        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        if (existingRoutine != null && existingRoutine.days != null) {
            for (int dayIdx : selectedDays) {
                String dayKey = dayKeys[dayIdx];

                if (existingRoutine.days.containsKey(dayKey)) {
                    List<Exercise> exercises = existingRoutine.days.get(dayKey);
                    if (exercises != null && !exercises. isEmpty()) {
                        ArrayList<Exercise> copiedExercises = new ArrayList<>();
                        for (Exercise ex :  exercises) {
                            Exercise copy = new Exercise();
                            copy.id = ex.id;
                            copy.name = ex.name;
                            copy. muscleGroup = ex.muscleGroup;
                            copy.sets = ex.sets;
                            copy.reps = ex.reps;
                            copy.weight = ex.weight;
                            copiedExercises.add(copy);
                        }
                        exercisesByDay.put(dayIdx, copiedExercises);
                    } else {
                        exercisesByDay.put(dayIdx, new ArrayList<>());
                    }
                } else {
                    exercisesByDay.put(dayIdx, new ArrayList<>());
                }
            }
        }
    }

    private void updateDay() {
        int dayIndex = selectedDays.get(currentDayIdx);
        ((TextView) findViewById(R.id.tv_day)).setText(dayNames[dayIndex]);
        ((TextView) findViewById(R.id.tv_subtitle)).setText("Day " + (currentDayIdx + 1) + " of " + selectedDays.size());

        currentExercises.clear();
        currentExercises.addAll(exercisesByDay.get(dayIndex));
        exerciseAdapter.notifyDataSetChanged();

        Button btnNext = findViewById(R.id. btn_next);
        btnNext.setText(currentDayIdx < selectedDays.size() - 1 ? "Next Day" :  "Review");
    }

    private void nextDayOrFinish() {
        if (currentDayIdx < selectedDays.size() - 1) {
            currentDayIdx++;
            updateDay();
        } else {
            Intent intent = new Intent(this, ReviewRoutineActivity.class);
            intent.putIntegerArrayListExtra("selectedDays", selectedDays);

            Gson gson = new Gson();
            String json = gson.toJson(exercisesByDay);
            intent.putExtra("exercisesByDay", json);
            intent.putExtra("editMode", isEditMode);

            if (isEditMode) {
                // ✅ CRITICAL: Update the existing routine with new data BEFORE passing it
                if (existingRoutine != null) {
                    existingRoutine. days = new HashMap<>();
                    String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

                    for (int dayIdx : selectedDays) {
                        String dayKey = dayKeys[dayIdx];
                        existingRoutine.days.put(dayKey, exercisesByDay. get(dayIdx));
                    }

                    existingRoutine.updatedAt = System.currentTimeMillis();
                }
                intent.putExtra("routine", existingRoutine);
            }

            startActivity(intent);
        }
    }

    private void openExerciseLibrary() {
        int currentDay = selectedDays.get(currentDayIdx);
        String dayKey = dayNames[currentDay]. toLowerCase();

        Intent intent = new Intent(this, ExerciseLibraryActivity.class);
        intent.putExtra("day", dayKey);
        intent.putExtra("dayIndex", currentDay);
        startActivityForResult(intent, REQUEST_EXERCISE_LIBRARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EXERCISE_LIBRARY && resultCode == RESULT_OK && data != null) {
            Exercise exercise = (Exercise) data.getSerializableExtra("exercise");

            if (exercise != null) {
                showConfigureExerciseDialog(exercise);
            }
        }
    }

    // ✅ NEW METHOD: Edit existing exercise
    private void editExercise(Exercise exercise) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_configure_exercise, null);

        NumberPicker pickerSets = dialogView.findViewById(R.id.picker_sets);
        NumberPicker pickerReps = dialogView.findViewById(R. id.picker_reps);
        EditText inputWeight = dialogView.findViewById(R.id.input_weight);

        // Pre-fill with existing values
        pickerSets.setMinValue(1);
        pickerSets.setMaxValue(10);
        pickerSets.setValue(exercise.sets);
        pickerSets.setWrapSelectorWheel(false);

        pickerReps.setMinValue(1);
        pickerReps.setMaxValue(50);
        pickerReps.setValue(exercise.reps);
        pickerReps.setWrapSelectorWheel(false);

        // Pre-fill weight if exists
        if (exercise.weight != null && exercise.weight > 0) {
            inputWeight. setText(String.valueOf(exercise. weight));
        }

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Edit " + exercise.name)
                .setView(dialogView)
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // Apply styling
            hideNumberPickerDivider(pickerSets);
            hideNumberPickerDivider(pickerReps);
            setNumberPickerTextColor(pickerSets, getResources().getColor(R.color.text_white));
            setNumberPickerTextColor(pickerReps, getResources().getColor(R.color.text_white));

            Button btnSave = dialog.getButton(AlertDialog. BUTTON_POSITIVE);
            Button btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            btnSave.setTextColor(getResources().getColor(R.color.gym_green));
            btnCancel.setTextColor(getResources().getColor(R.color.text_muted));

            btnSave.setOnClickListener(v -> {
                // ✅ Update existing exercise in place
                exercise.sets = pickerSets.getValue();
                exercise.reps = pickerReps.getValue();

                String weightText = inputWeight.getText().toString().trim();
                if (!weightText.isEmpty()) {
                    try {
                        exercise.weight = Double.parseDouble(weightText);
                    } catch (Exception e) {
                        exercise. weight = null;
                    }
                } else {
                    exercise.weight = null;
                }

                // ✅ CRITICAL: Update in the HashMap as well
                int dayIndex = selectedDays.get(currentDayIdx);
                ArrayList<Exercise> dayExercises = exercisesByDay.get(dayIndex);

                // Find and update the exercise in the HashMap
                if (dayExercises != null) {
                    for (int i = 0; i < dayExercises.size(); i++) {
                        if (dayExercises.get(i).id. equals(exercise.id)) {
                            dayExercises.set(i, exercise);
                            break;
                        }
                    }
                }

                exerciseAdapter.notifyDataSetChanged();
                Toast.makeText(this, exercise.name + " updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }
    private void showConfigureExerciseDialog(Exercise exercise) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_configure_exercise, null);

        NumberPicker pickerSets = dialogView.findViewById(R.id.picker_sets);
        NumberPicker pickerReps = dialogView.findViewById(R.id. picker_reps);
        EditText inputWeight = dialogView.findViewById(R.id.input_weight);

        // Configure Sets picker (1-10)
        pickerSets.setMinValue(1);
        pickerSets. setMaxValue(10);
        pickerSets.setValue(exercise.sets);
        pickerSets.setWrapSelectorWheel(false);

        // Configure Reps picker (1-50)
        pickerReps. setMinValue(1);
        pickerReps.setMaxValue(50);
        pickerReps.setValue(exercise.reps);
        pickerReps. setWrapSelectorWheel(false);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Configure " + exercise.name)
                .setView(dialogView)
                .setPositiveButton("ADD", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // Apply styling AFTER dialog is shown
            hideNumberPickerDivider(pickerSets);
            hideNumberPickerDivider(pickerReps);
            setNumberPickerTextColor(pickerSets, getResources().getColor(R.color.text_white));
            setNumberPickerTextColor(pickerReps, getResources().getColor(R.color.text_white));

            Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnCancel = dialog. getButton(AlertDialog.BUTTON_NEGATIVE);

            btnAdd.setTextColor(getResources().getColor(R.color.gym_green));
            btnCancel.setTextColor(getResources().getColor(R.color.text_muted));

            btnAdd.setOnClickListener(v -> {
                Exercise configuredExercise = new Exercise();
                configuredExercise. id = java.util.UUID.randomUUID().toString();
                configuredExercise.name = exercise.name;
                configuredExercise.muscleGroup = exercise.muscleGroup;
                configuredExercise.sets = pickerSets.getValue();
                configuredExercise.reps = pickerReps.getValue();

                String weightText = inputWeight.getText().toString().trim();
                if (! weightText.isEmpty()) {
                    try {
                        configuredExercise.weight = Double. parseDouble(weightText);
                    } catch (Exception e) {
                        configuredExercise.weight = null;
                    }
                } else {
                    configuredExercise.weight = null;
                }

                int dayIndex = selectedDays.get(currentDayIdx);
                exercisesByDay.get(dayIndex).add(configuredExercise);
                currentExercises. add(configuredExercise);
                exerciseAdapter.notifyDataSetChanged();

                Toast.makeText(this, exercise.name + " added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private void hideNumberPickerDivider(NumberPicker picker) {
        try {
            java.lang.reflect.Field dividerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
            dividerField.setAccessible(true);
            android.graphics.drawable.ColorDrawable transparent = new android.graphics.drawable. ColorDrawable(android.graphics.Color.TRANSPARENT);
            dividerField.set(picker, transparent);
        } catch (Exception e1) {
            try {
                java.lang.reflect.Field[] fields = NumberPicker.class.getDeclaredFields();
                for (java. lang.reflect.Field field : fields) {
                    if (field.getName().contains("Divider")) {
                        field.setAccessible(true);
                        android.graphics.drawable. ColorDrawable transparent = new android. graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT);
                        field.set(picker, transparent);
                    }
                }
            } catch (Exception e2) {
                try {
                    java.lang. reflect.Field dividerHeight = NumberPicker.class.getDeclaredField("mSelectionDividerHeight");
                    dividerHeight.setAccessible(true);
                    dividerHeight.set(picker, 0);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
        picker.invalidate();
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        try {
            Class<?> classType = numberPicker.getClass();
            java.lang.reflect. Field field = classType.getDeclaredField("mSelectorWheelPaint");
            field.setAccessible(true);
            android.graphics.Paint paint = (android.graphics.Paint) field.get(numberPicker);

            if (paint != null) {
                // Set color
                paint.setColor(color);

                // Set text size (change 28f to adjust size)
                float textSizePx = 28f * getResources().getDisplayMetrics().scaledDensity;
                paint.setTextSize(textSizePx);

                // Make it bold
                paint.setFakeBoldText(true);
                paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            }

            numberPicker.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExerciseLibrary() {
        final String[] muscles = {"Back", "Arms", "Legs", "Chest"};
        final String[][] exerciseNames = {
                {"Pull-ups", "Deadlift", "Barbell Row", "Lat Pulldown"},
                {"Barbell Curl", "Tricep Dips", "Hammer Curl", "Overhead Extension"},
                {"Squat", "Leg Press", "Leg Curl", "Calf Raise"},
                {"Bench Press", "Dumbbell Fly", "Incline Press", "Cable Crossover"}
        };

        new AlertDialog.Builder(this)
                .setTitle("Select Muscle Group")
                .setItems(muscles, (dlg, which) -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Select Exercise")
                            .setItems(exerciseNames[which], (d, eIdx) -> {
                                String exName = exerciseNames[which][eIdx];
                                String muscleGroup = muscles[which]. toLowerCase();
                                showAddExerciseConfigDialog(exName, muscleGroup);
                            })
                            . show();
                })
                .show();
    }

    private void showAddExerciseConfigDialog(String name, String group) {
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(40, 20, 40, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputSets = new EditText(this);
        inputSets.setHint("Sets (e.g.  4)");
        inputSets. setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputReps = new EditText(this);
        inputReps.setHint("Reps (e.g.  10)");
        inputReps. setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputWeight = new EditText(this);
        inputWeight. setHint("Weight (kg) [optional]");
        inputWeight. setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

        layout.addView(inputSets);
        layout.addView(inputReps);
        layout.addView(inputWeight);

        new AlertDialog.Builder(this)
                .setTitle("Configure Exercise")
                .setView(layout)
                .setPositiveButton("Add", (d, w) -> {
                    if (inputSets.getText().toString().isEmpty() || inputReps.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Sets and Reps required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Exercise exercise = new Exercise();
                    exercise.id = UUID.randomUUID().toString();
                    exercise.name = name;
                    exercise.muscleGroup = group;
                    exercise. sets = Integer.parseInt(inputSets.getText().toString());
                    exercise.reps = Integer. parseInt(inputReps.getText().toString());

                    try {
                        exercise.weight = Double.parseDouble(inputWeight.getText().toString());
                    } catch (Exception ex) {
                        exercise.weight = null;
                    }

                    int dayIndex = selectedDays.get(currentDayIdx);
                    exercisesByDay.get(dayIndex).add(exercise);
                    currentExercises.add(exercise);
                    exerciseAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeExercise(Exercise exercise) {
        int dayIndex = selectedDays.get(currentDayIdx);
        exercisesByDay.get(dayIndex).remove(exercise);
        currentExercises.remove(exercise);
        exerciseAdapter.notifyDataSetChanged();
        Toast.makeText(this, exercise.name + " removed", Toast.LENGTH_SHORT).show();
    }
}