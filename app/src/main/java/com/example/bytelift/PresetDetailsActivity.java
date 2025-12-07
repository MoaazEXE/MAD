package com.example.bytelift;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows preset details (days + exercises). Has a "Use Routine" button which copies preset
 * into user's saved routines and returns to main workouts list.
 */
public class PresetDetailsActivity extends AppCompatActivity {
    private WorkoutRoutineActivity.WorkoutRoutine preset;
    private RecyclerView rvDays;
    private RecyclerView rvExercises;
    private TextView tvName;
    private int selectedDayPos = 0;
    private List<String> dayKeys = new ArrayList<>();
    private Map<?, ?> dayToExercises;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_details);

        tvName = findViewById(R.id.tvPresetName);
        rvDays = findViewById(R.id.rvPresetDays);
        rvExercises = findViewById(R.id.rvPresetExercises);
        Button btnUse = findViewById(R.id.btnUseRoutine);

        preset = PresetsProvider.getSelectedPreset();
        if (preset == null) {
            new AlertDialog.Builder(this).setMessage("Preset not found").setPositiveButton("OK", (d,w)->finish()).show();
            return;
        }

        tvName.setText(preset.name);
        dayToExercises = preset.dayToExercises;
        if (dayToExercises != null && !dayToExercises.isEmpty()) {
            for (Object k : dayToExercises.keySet()) dayKeys.add(k == null ? "?" : k.toString());
            rvDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvDays.setAdapter(new DaysAdapter(dayKeys));
            showExercisesForDayIndex(0);
        } else {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new PresetSimpleTextAdapter("No days"));
        }

        btnUse.setOnClickListener(v -> {
            // Add this preset to user's routines and save
            List<WorkoutRoutineActivity.WorkoutRoutine> list = RoutineStorage.loadRoutines(this);
            if (list == null) list = new ArrayList<>();
            // create a copy with a new id
            WorkoutRoutineActivity.WorkoutRoutine copy = new WorkoutRoutineActivity.WorkoutRoutine(preset.name, preset.dayToExercises);
            copy.id = java.util.UUID.randomUUID().toString();
            list.add(0, copy);
            RoutineStorage.saveRoutines(this, list);
            // return to previous screen (main workout list)
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            finish();
        });
    }

    private void showExercisesForDayIndex(int dayIndex) {
        if (dayIndex < 0 || dayIndex >= dayKeys.size()) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new PresetSimpleTextAdapter("No exercises"));
            return;
        }
        Object key = dayKeys.get(dayIndex);
        Object exObj = dayToExercises.get(key);
        List<?> exList = null;
        if (exObj instanceof List) exList = (List<?>) exObj;
        if (exList == null || exList.isEmpty()) {
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(new PresetSimpleTextAdapter("No exercises for " + key));
            return;
        }
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        PresetExercisesAdapter adapter = new PresetExercisesAdapter(exList);
        rvExercises.setAdapter(adapter);
    }

    private static class PresetSimpleTextAdapter extends RecyclerView.Adapter<PresetSimpleTextAdapter.H> {
        private final String text;
        PresetSimpleTextAdapter(String text) { this.text = text; }
        static class H extends RecyclerView.ViewHolder { TextView tv; H(TextView v) { super(v); tv = v; } }
        @Override public H onCreateViewHolder(ViewGroup parent, int viewType) { TextView t = new TextView(parent.getContext()); t.setPadding(16,16,16,16); return new H(t); }
        @Override public void onBindViewHolder(H holder, int position) { holder.tv.setText(text); }
        @Override public int getItemCount() { return 1; }
    }

    private class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DH> {
        private final List<String> days;
        DaysAdapter(List<String> days) { this.days = days; }
        class DH extends RecyclerView.ViewHolder { TextView tv; DH(TextView v) { super(v); tv = v; } }
        @Override public DH onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView t = new TextView(parent.getContext());
            int pad = (int) (12 * parent.getContext().getResources().getDisplayMetrics().density);
            t.setPadding(pad, pad/2, pad, pad/2);
            t.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            t.setGravity(Gravity.CENTER);
            return new DH(t);
        }
        @Override public void onBindViewHolder(DH holder, int position) {
            holder.tv.setText(days.get(position));
            holder.tv.setAlpha(position == selectedDayPos ? 1f : 0.6f);
            holder.tv.setOnClickListener(v -> {
                int old = selectedDayPos;
                selectedDayPos = holder.getAdapterPosition();
                notifyItemChanged(old);
                notifyItemChanged(selectedDayPos);
                showExercisesForDayIndex(selectedDayPos);
            });
        }
        @Override public int getItemCount() { return days.size(); }
    }

    private static class PresetExercisesAdapter extends RecyclerView.Adapter<PresetExercisesAdapter.H> {
        private final List<?> items;
        PresetExercisesAdapter(List<?> items) { this.items = items; }
        static class H extends RecyclerView.ViewHolder { ImageView iv; TextView tvName; TextView tvMeta; H(View v) { super(v); iv = new ImageView(v.getContext()); tvName = new TextView(v.getContext()); tvMeta = new TextView(v.getContext()); } }
        @Override public H onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout row = new LinearLayout(parent.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8,8,8,8);
            ImageView iv = new ImageView(parent.getContext());
            LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(120, 120);
            ivlp.setMargins(8,8,16,8);
            iv.setLayoutParams(ivlp);
            TextView tvName = new TextView(parent.getContext());
            TextView tvMeta = new TextView(parent.getContext());
            LinearLayout texts = new LinearLayout(parent.getContext());
            texts.setOrientation(LinearLayout.VERTICAL);
            texts.addView(tvName);
            texts.addView(tvMeta);
            row.addView(iv);
            row.addView(texts);
            H holder = new H(row);
            holder.iv = iv;
            holder.tvName = tvName;
            holder.tvMeta = tvMeta;
            return holder;
        }
        @Override public void onBindViewHolder(H holder, int position) {
            Object ex = items.get(position);
            String name = "";
            String meta = "";
            int imageRes = 0;
            try { Object n = ex.getClass().getField("name").get(ex); if (n!=null) name = n.toString(); } catch (Exception ignored) {}
            try { Object r = ex.getClass().getField("repsOrNotes").get(ex); if (r!=null) meta = r.toString(); } catch (Exception ignored) {}
            try { Object i = ex.getClass().getField("imageRes").get(ex); if (i instanceof Integer) imageRes = (Integer) i; } catch (Exception ignored) {}
            holder.tvName.setText(name);
            holder.tvMeta.setText(meta);
            if (imageRes != 0) holder.iv.setImageResource(imageRes); else holder.iv.setImageResource(R.drawable.ic_placeholder);
        }
        @Override public int getItemCount() { return items.size(); }
    }
}