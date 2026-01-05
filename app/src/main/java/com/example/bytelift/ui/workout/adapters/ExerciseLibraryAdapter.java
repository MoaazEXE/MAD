package com.example.bytelift.ui.workout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx. cardview.widget.CardView;
import androidx.recyclerview.widget. RecyclerView;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Exercise;
import java.util.List;

public class ExerciseLibraryAdapter extends RecyclerView.Adapter<ExerciseLibraryAdapter. ViewHolder> {

    private List<Exercise> exercises;
    private OnExerciseSelectListener listener;

    public interface OnExerciseSelectListener {
        void onExerciseSelected(Exercise exercise);
    }

    public ExerciseLibraryAdapter(List<Exercise> exercises, OnExerciseSelectListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvName.setText(exercise.name);
        holder.tvMuscleGroup.setText(capitalizeFirst(exercise.muscleGroup));

        holder.btnAdd.setOnClickListener(v -> listener.onExerciseSelected(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises. size();
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvName, tvMuscleGroup;
        Button btnAdd;

        ViewHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card_exercise);
            tvName = v.findViewById(R.id.tv_exercise_name);
            tvMuscleGroup = v.findViewById(R.id.tv_muscle_group);
            btnAdd = v. findViewById(R.id.btn_add);
        }
    }
}
