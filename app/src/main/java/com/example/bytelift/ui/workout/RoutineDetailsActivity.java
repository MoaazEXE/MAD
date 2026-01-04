package com.example.bytelift.ui.workout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutineDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RoutineDetailsActivity";
    private static final String EXTRA_INDEX = "routine_index";

    public static void start(Context ctx, int routineIndex) {
        Intent intent = new Intent(ctx, RoutineDetailsActivity.class);
        intent.putExtra(EXTRA_INDEX, routineIndex);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ctx.startActivity(intent);
    }

    private RecyclerView rvDays;
    private RecyclerView rvExercises;
    private TextView tvName;
    private Button btnEditRoutine;
    private Button btnDeleteRoutine;

    private final List<String> dayKeys = new ArrayList<>();
    private Map<?, ?> dayToExercises;
    private int selectedDayPos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        tvName = findViewById(R.id.tvRoutineName);
        rvDays = findViewById(R.id.rvDays);
        rvExercises = findViewById(R.id.rvExercises);
        Button btnCustomizeDay = findViewById(R.id.btnCustomizeDay);
        btnEditRoutine = findViewById(R.id.btnEditRoutine);
        btnDeleteRoutine = findViewById(R.id.btnDeleteRoutine);

        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        if (index < 0) {
            Toast.makeText(this, "Invalid routine", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<WorkoutRoutineActivity.WorkoutRoutine> list = RoutineStorage.loadRoutines(this);
        if (list == null || index >= list.size()) {
            Toast.makeText(this, "Routine not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        WorkoutRoutineActivity.WorkoutRoutine routine = list.get(index);

        String routineName = extractString(routine, "getName", "name");
        tvName.setText(routineName == null ? "Routine" : routineName);

        dayToExercises = extractMap(routine, "getDayToExercises", "dayToExercises");

        if (dayToExercises != null && !dayToExercises.isEmpty()) {
            dayKeys.clear();
            for (Object k : dayToExercises.keySet()) {
                dayKeys.add(k == null ? "?" : k.toString());
            }

            rvDays.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            DaysAdapter daysAdapter = new DaysAdapter(dayKeys);
            rvDays.setAdapter(daysAdapter);

            selectedDayPos = 0;
            showExercisesForDayIndex(selectedDayPos);
        } else {
            dayKeys.clear();
            rvDays.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvDays.setAdapter(new DaysAdapter(dayKeys));
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new SimpleTextAdapter(singleton("No days configured")));
        }

        btnCustomizeDay.setOnClickListener(v -> {
            if (selectedDayPos < 0 || selectedDayPos >= dayKeys.size()) {
                Toast.makeText(this, "No day selected", Toast.LENGTH_SHORT).show();
                return;
            }
            String dayName = dayKeys.get(selectedDayPos);
            Toast.makeText(this,
                    "Customize " + dayName + " (coming soon)",
                    Toast.LENGTH_SHORT).show();
        });

        btnEditRoutine.setOnClickListener(v -> showEditRoutineNameDialog());

        btnDeleteRoutine.setOnClickListener(v -> showDeleteRoutineDialog());
    }

    private void showExercisesForDayIndex(int dayIndex) {
        if (dayIndex < 0 || dayIndex >= dayKeys.size()) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new SimpleTextAdapter(singleton("No exercises")));
            return;
        }

        Object key = dayKeys.get(dayIndex);
        Object exObj = dayToExercises.get(key);
        List<?> exList = null;
        if (exObj instanceof List) {
            exList = (List<?>) exObj;
        }

        if (exList == null || exList.isEmpty()) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new SimpleTextAdapter(singleton("No exercises for " + key)));
            return;
        }

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        ExercisesAdapter adapter = new ExercisesAdapter(exList);
        rvExercises.setAdapter(adapter);
    }

    private void showEditRoutineNameDialog() {
        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        List<WorkoutRoutineActivity.WorkoutRoutine> list = RoutineStorage.loadRoutines(this);
        if (index < 0 || list == null || index >= list.size()) return;

        WorkoutRoutineActivity.WorkoutRoutine routine = list.get(index);

        final EditText input = new EditText(this);
        input.setText(routine.name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Edit Routine Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        routine.name = newName;
                        RoutineStorage.saveRoutines(this, list);
                        tvName.setText(newName);
                        Toast.makeText(this, "Routine renamed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteRoutineDialog() {
        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        List<WorkoutRoutineActivity.WorkoutRoutine> list = RoutineStorage.loadRoutines(this);
        if (index < 0 || list == null || index >= list.size()) return;

        WorkoutRoutineActivity.WorkoutRoutine routine = list.get(index);

        new AlertDialog.Builder(this)
                .setTitle("Delete Routine")
                .setMessage("Delete \"" + routine.name + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    list.remove(index);
                    RoutineStorage.saveRoutines(this, list);
                    Toast.makeText(this, "Routine deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // DaysAdapter
    private class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DH> {
        private final List<String> days;

        DaysAdapter(List<String> days) {
            this.days = days;
        }

        class DH extends RecyclerView.ViewHolder {
            TextView tv;
            DH(TextView v) {
                super(v);
                tv = v;
            }
        }

        @Override
        public DH onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView t = new TextView(parent.getContext());
            int pad = (int) (12 * parent.getContext().getResources()
                    .getDisplayMetrics().density);
            t.setPadding(pad, pad / 2, pad, pad / 2);
            t.setBackgroundColor(Color.TRANSPARENT);
            t.setTextColor(Color.BLACK);
            t.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            t.setGravity(Gravity.CENTER);
            return new DH(t);
        }

        @Override
        public void onBindViewHolder(DH holder, int position) {
            String label = days.get(position);
            holder.tv.setText(label);
            holder.tv.setAlpha(position == selectedDayPos ? 1f : 0.6f);
            holder.tv.setOnClickListener(v -> {
                int old = selectedDayPos;
                selectedDayPos = holder.getAdapterPosition();
                notifyItemChanged(old);
                notifyItemChanged(selectedDayPos);
                showExercisesForDayIndex(selectedDayPos);
            });
        }

        @Override
        public int getItemCount() {
            return days.size();
        }
    }

    // ExercisesAdapter for detail screen
    private class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.EH> {
        private final List<?> items;

        ExercisesAdapter(List<?> items) {
            this.items = items;
        }

        class EH extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tvName;
            TextView tvMeta;

            EH(View v) {
                super(v);
                iv = v.findViewWithTag("iv");
                tvName = v.findViewWithTag("tvName");
                tvMeta = v.findViewWithTag("tvMeta");
            }
        }

        @Override
        public EH onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout row = new LinearLayout(parent.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8, 8, 8, 8);
            row.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView iv = new ImageView(parent.getContext());
            int size = (int) (64 * parent.getContext().getResources()
                    .getDisplayMetrics().density);
            LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(size, size);
            ivlp.setMargins(8, 8, 16, 8);
            iv.setLayoutParams(ivlp);
            iv.setTag("iv");

            LinearLayout texts = new LinearLayout(parent.getContext());
            texts.setOrientation(LinearLayout.VERTICAL);
            texts.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f));

            TextView tvName = new TextView(parent.getContext());
            tvName.setTag("tvName");
            tvName.setTextSize(16f);
            tvName.setPadding(0, 0, 0, 4);

            TextView tvMeta = new TextView(parent.getContext());
            tvMeta.setTag("tvMeta");
            tvMeta.setTextSize(14f);
            tvMeta.setTextColor(0xFF777777);

            texts.addView(tvName);
            texts.addView(tvMeta);

            row.addView(iv);
            row.addView(texts);

            return new EH(row);
        }

        @Override
        public void onBindViewHolder(EH holder, int position) {
            Object ex = items.get(position);

            String name = extractString(ex, "getName", "name");
            holder.tvName.setText(name == null ? ex.toString() : name);

            String meta = "";
            try {
                Object repsOrNotesObj = ex.getClass().getField("repsOrNotes").get(ex);
                if (repsOrNotesObj != null) {
                    meta = repsOrNotesObj.toString();
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not load repsOrNotes field", e);
            }
            holder.tvMeta.setText(meta);

            int imageResId = 0;
            try {
                Object imageResObj = ex.getClass().getField("imageRes").get(ex);
                if (imageResObj instanceof Integer) {
                    imageResId = (Integer) imageResObj;
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not load imageRes: " + e.getMessage());
            }

            if (imageResId != 0) {
                try {
                    holder.iv.setImageResource(imageResId);
                } catch (Exception e) {
                    Log.w(TAG, "Invalid imageRes: " + imageResId);
                    holder.iv.setImageResource(R.drawable.ic_placeholder);
                }
            } else {
                holder.iv.setImageResource(R.drawable.ic_placeholder);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    // Helpers
    private static List<String> singleton(String s) {
        List<String> l = new ArrayList<>();
        l.add(s);
        return l;
    }

    private static String extractString(Object obj, String getterName, String fieldName) {
        if (obj == null) return null;
        try {
            try {
                Object v = obj.getClass().getMethod(getterName).invoke(obj);
                if (v != null) return v.toString();
            } catch (NoSuchMethodException ignored) { }
            try {
                Object v = obj.getClass().getField(fieldName).get(obj);
                if (v != null) return v.toString();
            } catch (Exception ignored) { }
        } catch (Exception e) {
            Log.w(TAG, "extractString error", e);
        }
        return null;
    }

    private static Map<?, ?> extractMap(Object obj, String getterName, String fieldName) {
        if (obj == null) return null;
        try {
            try {
                Object v = obj.getClass().getMethod(getterName).invoke(obj);
                if (v instanceof Map) return (Map<?, ?>) v;
            } catch (NoSuchMethodException ignored) { }
            try {
                Object v = obj.getClass().getField(fieldName).get(obj);
                if (v instanceof Map) return (Map<?, ?>) v;
            } catch (Exception ignored) { }
        } catch (Exception e) {
            Log.w(TAG, "extractMap error", e);
        }
        return null;
    }

    private static class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.H> {
        private final List<String> items;

        SimpleTextAdapter(List<String> items) {
            this.items = items;
        }

        static class H extends RecyclerView.ViewHolder {
            TextView tv;
            H(TextView v) {
                super(v);
                tv = v;
            }
        }

        @Override
        public H onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView t = new TextView(parent.getContext());
            t.setPadding(16, 16, 16, 16);
            return new H(t);
        }

        @Override
        public void onBindViewHolder(H holder, int position) {
            holder.tv.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
