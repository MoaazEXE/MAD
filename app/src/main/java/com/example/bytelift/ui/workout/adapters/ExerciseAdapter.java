package com.example.bytelift.ui.workout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget. RecyclerView;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Exercise;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnExerciseActionListener {
        void onEdit(Exercise exercise);
        void onDelete(Exercise exercise);
    }

    private List<Exercise> exercises;
    private OnExerciseActionListener listener;

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseActionListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exercise ex = exercises.get(position);

        holder.tvName.setText(ex.name);

        String details = ex.sets + " sets × " + ex.reps + " reps";
        if (ex.weight != null && ex.weight > 0) {
            details += " • " + ex.weight + "kg";
        }
        holder.tvDetails.setText(details);

        // Click card to edit
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(ex);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(ex);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        ImageButton btnDelete;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_exercise_name);
            tvDetails = v.findViewById(R.id.tv_ex_detail);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}