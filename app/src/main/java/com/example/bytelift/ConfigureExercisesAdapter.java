package com.example.bytelift;

import android. text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android. view.View;
import android.view. ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx. annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java. util.ArrayList;
import java. util.List;

public class ConfigureExercisesAdapter extends RecyclerView. Adapter<ConfigureExercisesAdapter.H> {

    private final List<WorkoutRoutineActivity.Exercise> exercises;
    private final List<Boolean> checked;
    private final List<String> repsTexts;

    public ConfigureExercisesAdapter(List<WorkoutRoutineActivity.Exercise> exercises,
                                     List<WorkoutRoutineActivity. SelectedExercise> existing) {
        this.exercises = exercises;
        this.checked = new ArrayList<>();
        this.repsTexts = new ArrayList<>();
        for (WorkoutRoutineActivity.Exercise ex : exercises) {
            boolean isChecked = false;
            String reps = "";
            if (existing != null) {
                for (WorkoutRoutineActivity. SelectedExercise se : existing) {
                    if (se.id != null && se.id.equals(ex.id)) {
                        isChecked = true;
                        reps = se.repsOrNotes != null ? se.repsOrNotes : "";
                        break;
                    }
                }
            }
            checked. add(isChecked);
            repsTexts.add(reps);
        }
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()). inflate(R.layout.item_exercise, parent, false);
        return new H(v);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        final int adapterPosition = position; // Create final variable to use in anonymous classes
        WorkoutRoutineActivity.Exercise e = exercises.get(adapterPosition);
        holder.tvName.setText(e.name);

        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setChecked(checked.get(adapterPosition));
        holder.inputReps.setEnabled(checked.get(adapterPosition));
        holder.cb.setOnCheckedChangeListener(new CompoundButton. OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                checked.set(adapterPosition, isChecked); // Use final variable
                holder.inputReps.setEnabled(isChecked);
            }
        });

        if (holder.watcher != null) {
            holder.inputReps. removeTextChangedListener(holder.watcher);
        }
        holder.inputReps.setText(repsTexts.get(adapterPosition));
        holder.watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                repsTexts.set(adapterPosition, s.toString()); // Use final variable
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.inputReps.addTextChangedListener(holder.watcher);

        if (e.imageRes != 0) {
            holder.iv.setImageResource(e.imageRes);
        } else {
            holder.iv.setImageResource(R.drawable.ic_placeholder);
        }
        holder. iv.setContentDescription(e.name != null ? e.name : "");
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public List<WorkoutRoutineActivity. SelectedExercise> getSelectedExercises() {
        List<WorkoutRoutineActivity.SelectedExercise> out = new ArrayList<>();
        for (int i = 0; i < exercises.size(); i++) {
            if (checked. get(i)) {
                WorkoutRoutineActivity. Exercise e = exercises.get(i);
                out.add(new WorkoutRoutineActivity.SelectedExercise(e.id, e.name, repsTexts.get(i)));
            }
        }
        return out;
    }

    static class H extends RecyclerView.ViewHolder {
        CheckBox cb;
        TextView tvName;
        EditText inputReps;
        AppCompatImageView iv;
        TextWatcher watcher;

        H(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R. id.cb_exercise);
            tvName = itemView. findViewById(R.id.tv_exercise_name);
            inputReps = itemView.findViewById(R. id.et_reps);
            iv = itemView.findViewById(R.id. iv_exercise);
            watcher = null;
        }
    }
}