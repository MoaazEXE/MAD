package com.example.bytelift;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android. view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget. Button;
import android.widget. Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget. LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io. Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DayWorkoutActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE = "extra_routine";
    public static final String EXTRA_DAY = "extra_day";

    private WorkoutRoutineActivity. WorkoutRoutine routine;
    private String dayName;
    private ExercisesDetailAdapter adapter;

    public static void start(AppCompatActivity from, WorkoutRoutineActivity.WorkoutRoutine r, String day) {
        android.content.Intent i = new android.content.Intent(from, DayWorkoutActivity. class);
        i.putExtra(EXTRA_ROUTINE, (Serializable) r);
        i.putExtra(EXTRA_DAY, day);
        from.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_workout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView rvExercises = findViewById(R.id.recycler_day_exercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        Button btnCustomize = findViewById(R.id.btn_customize_day);
        btnCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfigureDialog();
            }
        });

        Object s = getIntent().getSerializableExtra(EXTRA_ROUTINE);
        Object d = getIntent().getSerializableExtra(EXTRA_DAY);
        if (s instanceof WorkoutRoutineActivity.WorkoutRoutine) {
            routine = (WorkoutRoutineActivity.WorkoutRoutine) s;
        }
        if (d instanceof String) {
            dayName = (String) d;
        }

        adapter = new ExercisesDetailAdapter();
        rvExercises.setAdapter(adapter);
        loadDay();
    }

    private void loadDay() {
        if (routine == null || dayName == null) return;
        List<WorkoutRoutineActivity. SelectedExercise> list = routine.dayToExercises. get(dayName);
        if (list == null) list = new ArrayList<>();
        adapter.setItems(list);
        setTitle(dayName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (routine != null && routine.id != null) {
            List<WorkoutRoutineActivity.WorkoutRoutine> all = RoutineStorage. loadRoutines(this);
            for (WorkoutRoutineActivity. WorkoutRoutine r : all) {
                if (r.id != null && r.id.equals(routine.id)) {
                    routine = r;
                    break;
                }
            }
            loadDay();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openConfigureDialog() {
        List<WorkoutRoutineActivity.SelectedExercise> existing = routine.dayToExercises.get(dayName);
        ConfigureDayHelper. showConfigureDayDialog(this, dayName, existing, new ConfigureDayHelper.ConfiguredCallback() {
            @Override
            public void onConfigured(List<WorkoutRoutineActivity.SelectedExercise> selected) {
                routine.dayToExercises.put(dayName, selected);
                List<WorkoutRoutineActivity.WorkoutRoutine> all = RoutineStorage.loadRoutines(DayWorkoutActivity. this);
                boolean replaced = false;
                for (int i = 0; i < all.size(); i++) {
                    if (all.get(i).id != null && all.get(i). id.equals(routine.id)) {
                        all.set(i, routine);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) all.add(routine);
                RoutineStorage.saveRoutines(DayWorkoutActivity.this, all);
                Toast.makeText(DayWorkoutActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                loadDay();
            }
        });
    }

    // ========== ADD THE ADAPTER CLASS HERE ==========
    private class ExercisesDetailAdapter extends RecyclerView.Adapter<ExercisesDetailAdapter.H> {
        private List<WorkoutRoutineActivity.SelectedExercise> items = new ArrayList<>();

        @SuppressLint("NotifyDataSetChanged")
        void setItems(List<WorkoutRoutineActivity.SelectedExercise> list) {
            items = list != null ? list : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R. layout.item_exercise_detail, parent, false);
            return new H(v);
        }

        @Override
        public void onBindViewHolder(@NonNull H holder, int position) {
            WorkoutRoutineActivity.SelectedExercise se = items.get(position);
            holder.tvName.setText(se.name);
            holder.tvReps.setText(se.repsOrNotes != null ? se.repsOrNotes : "");
            if (se.id != null) {
                WorkoutRoutineActivity. Exercise e = findExerciseById(se.id);
                if (e != null && e.imageRes != 0) {
                    holder.iv.setImageResource(e.imageRes);
                } else {
                    holder. iv.setImageResource(R. drawable.ic_placeholder);
                }
            } else {
                holder.iv.setImageResource(R.drawable.ic_placeholder);
            }
            holder.iv.setContentDescription(se.name != null ? se.name : "");
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class H extends RecyclerView.ViewHolder {
            androidx.appcompat.widget.AppCompatImageView iv;
            android.widget.TextView tvName;
            android.widget.TextView tvReps;

            H(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv_exercise_detail);
                tvName = itemView.findViewById(R.id.tv_exercise_detail_name);
                tvReps = itemView. findViewById(R.id.tv_exercise_detail_reps);
            }
        }
    }

    private WorkoutRoutineActivity. Exercise findExerciseById(String id) {
        for (WorkoutRoutineActivity.Exercise e : WorkoutRoutineActivity.ExercisesProvider.getAllExercises()) {
            if (e. id.equals(id)) return e;
        }
        return null;
    }
}