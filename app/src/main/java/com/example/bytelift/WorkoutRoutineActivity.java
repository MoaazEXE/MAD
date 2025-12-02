package com.example.bytelift;

import android. os.Bundle;
import android.text. Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view. LayoutInflater;
import android.view. View;
import android.view.ViewGroup;
import android.widget.Button;
import android. widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget. TextView;
import android. widget.Toast;

import androidx.annotation. NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx. recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview. widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io. Serializable;
import java.util.ArrayList;
import java. util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util. UUID;

public class WorkoutRoutineActivity extends AppCompatActivity {

    private RecyclerView routinesRecycler;
    private RoutineAdapter routineAdapter;
    private List<WorkoutRoutine> routines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_routine);

        routinesRecycler = findViewById(R.id.recycler_routines);
        routinesRecycler. setLayoutManager(new LinearLayoutManager(this));
        routineAdapter = new RoutineAdapter(routines);
        routinesRecycler. setAdapter(routineAdapter);

        Button fabCreate = findViewById(R.id.fab_create_routine);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateRoutineFlow();
            }
        });

        loadRoutines();
        updateUI();
    }

    private void loadRoutines() {
        List<WorkoutRoutine> loaded = RoutineStorage.loadRoutines(this);
        routines.clear();
        routines.addAll(loaded);
    }

    private void saveRoutines() {
        RoutineStorage.saveRoutines(this, routines);
    }

    private void updateUI() {
        routineAdapter.notifyDataSetChanged();
        findViewById(R.id. text_empty).setVisibility(routines.isEmpty() ?  View.VISIBLE : View.GONE);
    }

    private void startCreateRoutineFlow() {
        showSelectDaysDialog(new OnDaysSelected() {
            @Override
            public void onSelected(List<String> selectedDays) {
                if (selectedDays.isEmpty()) {
                    Toast. makeText(WorkoutRoutineActivity. this, "Pick at least one day", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, List<SelectedExercise>> configuredDays = new LinkedHashMap<>();
                configureDayRecursive(selectedDays, 0, configuredDays, new Runnable() {
                    @Override
                    public void run() {
                        askForRoutineNameAndSave(configuredDays);
                    }
                });
            }
        });
    }

    private void showSelectDaysDialog(OnDaysSelected callback) {
        View view = getLayoutInflater().inflate(R.layout. dialog_select_days, null);

        CheckBox cbMon = view.findViewById(R.id.cb_mon);
        CheckBox cbTue = view.findViewById(R.id.cb_tue);
        CheckBox cbWed = view.findViewById(R.id.cb_wed);
        CheckBox cbThu = view.findViewById(R.id.cb_thu);
        CheckBox cbFri = view.findViewById(R.id.cb_fri);
        CheckBox cbSat = view.findViewById(R.id.cb_sat);
        CheckBox cbSun = view.findViewById(R.id.cb_sun);

        new AlertDialog.Builder(this)
                . setTitle("Which days will you work out?")
                .setView(view)
                .setPositiveButton("Continue", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        List<String> days = new ArrayList<>();
                        if (cbMon. isChecked()) days.add("Monday");
                        if (cbTue.isChecked()) days. add("Tuesday");
                        if (cbWed.isChecked()) days.add("Wednesday");
                        if (cbThu.isChecked()) days.add("Thursday");
                        if (cbFri.isChecked()) days.add("Friday");
                        if (cbSat. isChecked()) days.add("Saturday");
                        if (cbSun.isChecked()) days. add("Sunday");
                        callback.onSelected(days);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void configureDayRecursive(List<String> days, int index, Map<String, List<SelectedExercise>> outMap, Runnable onComplete) {
        if (index >= days.size()) {
            onComplete.run();
            return;
        }
        String dayName = days.get(index);
        showConfigureDayDialog(dayName, new OnDayConfigured() {
            @Override
            public void onConfigured(List<SelectedExercise> exercisesSelected) {
                outMap.put(dayName, exercisesSelected);
                configureDayRecursive(days, index + 1, outMap, onComplete);
            }
        });
    }

    private void showConfigureDayDialog(String dayName, OnDayConfigured callback) {
        View view = getLayoutInflater().inflate(R. layout.dialog_configure_day, null);
        TextView tvTitle = view.findViewById(R.id.tv_configure_day_title);
        RecyclerView rvExercises = view.findViewById(R. id.recycler_exercises);

        tvTitle.setText("Configure " + dayName);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        ExercisesAdapter adapter = new ExercisesAdapter(ExercisesProvider.getAllExercises());
        rvExercises.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Configure " + dayName)
                .setView(view)
                .setPositiveButton("Save Day", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content. DialogInterface dialog, int which) {
                        callback.onConfigured(adapter.getSelectedExercises());
                    }
                })
                .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface d, int w) {
                        callback.onConfigured(new ArrayList<>());
                    }
                })
                . show();
    }

    private void askForRoutineNameAndSave(Map<String, List<SelectedExercise>> configuredDays) {
        final EditText input = new EditText(this);
        input.setHint("e.g.  Upper Body Split");
        input. setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                . setTitle("Name your routine")
                .setView(input)
                .setPositiveButton("Save Routine", new android. content.DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        String name = input.getText().toString().trim();
                        if (name. isEmpty()) name = "Routine " + (routines.size() + 1);
                        WorkoutRoutine r = new WorkoutRoutine(name, configuredDays);
                        r.id = UUID.randomUUID().toString();
                        routines. add(0, r);
                        saveRoutines();
                        updateUI();
                        Toast.makeText(WorkoutRoutineActivity. this, "Routine saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Callback interfaces
    private interface OnDaysSelected {
        void onSelected(List<String> days);
    }

    private interface OnDayConfigured {
        void onConfigured(List<SelectedExercise> selected);
    }

    // Models
    public static class WorkoutRoutine implements Serializable {
        public String id;
        public String name;
        public Map<String, List<SelectedExercise>> dayToExercises;

        public WorkoutRoutine() {}

        public WorkoutRoutine(String name, Map<String, List<SelectedExercise>> dayToExercises) {
            this.name = name;
            this.dayToExercises = dayToExercises;
        }
    }

    public static class SelectedExercise implements Serializable {
        public String id;
        public String name;
        public String repsOrNotes;
        public int imageRes;  // NEW: holds the drawable resource ID (e.g., R.drawable.pushup)

        public SelectedExercise() {}

        public SelectedExercise(String id, String name, String repsOrNotes) {
            this.id = id;
            this.name = name;
            this.repsOrNotes = repsOrNotes;
            this.imageRes = 0;  // default: no image
        }

        // Optional: constructor with image
        public SelectedExercise(String id, String name, String repsOrNotes, int imageRes) {
            this.id = id;
            this.name = name;
            this.repsOrNotes = repsOrNotes;
            this.imageRes = imageRes;
        }
    }

    public static class Exercise {
        public String id;
        public String name;
        public int imageRes;

        public Exercise(String id, String name) {
            this(id, name, 0);
        }

        public Exercise(String id, String name, int imageRes) {
            this. id = id;
            this.name = name;
            this.imageRes = imageRes;
        }
    }

    public static class ExercisesProvider {
        public static List<Exercise> getAllExercises() {
            List<Exercise> list = new ArrayList<>();
            list.add(new Exercise("pushup", "Push Ups", R.drawable.pushup));  // ✅ changed
            list.add(new Exercise("squat", "Bodyweight Squats", R.drawable.squat));  // ✅ changed
            list.add(new Exercise("plank", "Plank", R.drawable.plank));
            list.add(new Exercise("lunge", "Lunges", R.drawable.lunge));
            list.add(new Exercise("situp", "Sit Ups", R.drawable. situp));
            return list;
        }
    }

    // Adapters
    private static class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter. Holder> {
        private final List<Exercise> exercises;
        private final List<Boolean> checked;
        private final List<String> repsTexts;

        ExercisesAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
            this.checked = new ArrayList<>();
            this.repsTexts = new ArrayList<>();
            for (int i = 0; i < exercises.size(); i++) {
                checked.add(false);
                repsTexts. add("");
            }
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent. getContext()). inflate(R.layout.item_exercise, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final int adapterPosition = position; // Create final variable to use in anonymous classes
            Exercise e = exercises.get(adapterPosition);
            holder.tvName.setText(e.name);

            // checkbox binding - avoid triggering listener during recycle
            holder.cb.setOnCheckedChangeListener(null);
            holder.cb.setChecked(checked. get(adapterPosition));
            holder. inputReps.setEnabled(checked.get(adapterPosition));
            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checked.set(adapterPosition, isChecked); // Use final variable
                    holder. inputReps.setEnabled(isChecked);
                }
            });

            // reps text binding with TextWatcher
            if (holder.watcher != null) {
                holder.inputReps.removeTextChangedListener(holder.watcher);
            }
            holder.inputReps. setText(repsTexts.get(adapterPosition));
            holder.watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

                @Override
                public void onTextChanged(CharSequence s, int st, int b, int c) {
                    repsTexts.set(adapterPosition, s. toString()); // Use final variable
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };
            holder.inputReps.addTextChangedListener(holder. watcher);

            // image binding
            if (e.imageRes != 0) {
                holder. ivExercise. setImageResource(e.imageRes);
            } else {
                holder.ivExercise.setImageResource(R.drawable.ic_placeholder);
            }
            holder.ivExercise.setContentDescription(e.name != null ? e.name : "");
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        List<SelectedExercise> getSelectedExercises() {
            List<SelectedExercise> out = new ArrayList<>();
            for (int i = 0; i < exercises.size(); i++) {
                if (checked.get(i)) {
                    Exercise e = exercises.get(i);
                    String reps = repsTexts. get(i);
                    SelectedExercise selected = new SelectedExercise(e.id, e.name, reps);
                    selected.imageRes = e.imageRes;  // ✅ ADD THIS LINE - copy the image
                    out.add(selected);
                }
            }
            return out;
        }

        static class Holder extends RecyclerView.ViewHolder {
            CheckBox cb;
            TextView tvName;
            EditText inputReps;
            AppCompatImageView ivExercise;
            TextWatcher watcher;

            Holder(@NonNull View itemView) {
                super(itemView);
                cb = itemView.findViewById(R. id.cb_exercise);
                tvName = itemView. findViewById(R.id.tv_exercise_name);
                inputReps = itemView.findViewById(R.id.et_reps);
                ivExercise = itemView.findViewById(R.id.iv_exercise);
                watcher = null;
            }
        }
    }

    // Adapter to show saved routines
    private class RoutineAdapter extends RecyclerView. Adapter<RoutineAdapter. RoutineHolder> {
        private final List<WorkoutRoutine> items;

        RoutineAdapter(List<WorkoutRoutine> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RoutineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent. getContext()).inflate(R.layout.item_routine, parent, false);
            return new RoutineHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RoutineHolder holder, int position) {
            WorkoutRoutine r = items.get(position);
            holder. tvName.setText(r.name);

            // ... rest of your binding code ...

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        WorkoutRoutine routine = items.get(currentPosition);
                        new AlertDialog.Builder(WorkoutRoutineActivity.this)
                                .setTitle("Delete routine")
                                .setMessage("Delete " + routine. name + "?")
                                .setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content. DialogInterface d, int w) {
                                        items.remove(currentPosition);
                                        saveRoutines();
                                        updateUI();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition(); if (pos == RecyclerView.NO_POSITION) return; RoutineDetailsActivity.start(v.getContext(), pos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class RoutineHolder extends RecyclerView. ViewHolder {
            TextView tvName;
            TextView tvDetails;
            Button btnDelete;

            RoutineHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_routine_name);
                tvDetails = itemView.findViewById(R.id.tv_routine_details);
                btnDelete = itemView.findViewById(R.id.btn_delete_routine);
            }
        }
    }
}