package com.example.bytelift. ui.workout.adapters;

import android.view.LayoutInflater;
import android.view. View;
import android.view. ViewGroup;
import android.widget.ImageButton;
import android.widget. TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bytelift.R;
import com. example.bytelift.ui.workout.models.Routine;
import com. example.bytelift.utils.StreakManager;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private final List<Routine> routines;
    private final OnRoutineClickListener listener;

    public interface OnRoutineClickListener {
        void onOpen(Routine routine);
        void onEdit(Routine routine);
        void onDelete(Routine routine);

        void onSetActive(Routine routine);
    }

    public RoutineAdapter(List<Routine> routines, OnRoutineClickListener listener) {
        this.routines = routines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);

        holder.tvName. setText(routine.name);
        holder.tvDays.setText(routine.daysPerWeek + " days/week");
        holder.tvType.setText(routine. type.equals("custom") ? "CUSTOM" : "PRESET");

        // ✅ Show green border, active tag, and progress if active
        if (routine.isActiveForStreak) {
            holder. activeBorder.setVisibility(View.VISIBLE);
            holder.tvActiveTag.setVisibility(View.VISIBLE);
            holder.tvCardProgress.setVisibility(View.VISIBLE);

            // Get and display progress
            String progress = StreakManager.getWeekProgress(holder.itemView.getContext());
            // Format:  "2 of 3 workouts" → display as "• 📊 2/3 workouts"
            String[] parts = progress.split(" of ");
            if (parts.length == 2) {
                String completed = parts[0];
                String total = parts[1]. split(" ")[0];
                holder.tvCardProgress.setText("• 📊 " + completed + "/" + total + " workouts");
            } else {
                holder.tvCardProgress.setText("• 📊 " + progress);
            }
        } else {
            holder.activeBorder. setVisibility(View.GONE);
            holder.tvActiveTag.setVisibility(View. GONE);
            holder.tvCardProgress.setVisibility(View. GONE);
        }

        // Button clicks
        holder.cardRoutine.setOnClickListener(v -> listener.onOpen(routine));
        holder.btnEdit. setOnClickListener(v -> listener.onEdit(routine));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(routine));
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        CardView cardRoutine;
        TextView tvName, tvDays, tvType, tvActiveTag, tvCardProgress;
        ImageButton btnEdit, btnDelete;
        View activeBorder;

        RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoutine = itemView.findViewById(R. id.card_routine);
            tvName = itemView.findViewById(R.id.tv_routine_name);
            tvDays = itemView.findViewById(R.id.tv_days);
            tvType = itemView.findViewById(R.id.tv_type);
            tvActiveTag = itemView. findViewById(R.id.tv_active_tag);
            tvCardProgress = itemView.findViewById(R.id.tv_card_progress);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            activeBorder = itemView.findViewById(R.id.active_border);
        }
    }
}