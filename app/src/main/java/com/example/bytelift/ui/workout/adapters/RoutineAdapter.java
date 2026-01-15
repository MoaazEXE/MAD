package com.example.bytelift.ui.workout.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;
import com.example.bytelift.ui.workout.activities.SelectDaysActivity;
import com.example.bytelift.ui.workout.models.Routine;

import java.util.ArrayList;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    public interface OnRoutineClickListener {
        void onOpen(Routine routine);
        void onEdit(Routine routine);
        void onDelete(Routine routine);
    }

    private final List<Routine> routines;
    private final OnRoutineClickListener listener;
    private Context context;

    public RoutineAdapter(List<Routine> routines, OnRoutineClickListener listener) {
        this.routines = (routines != null) ? routines : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Routine rt = routines.get(position);

        holder.tvName.setText(rt.name);
        holder.tvDays.setText(rt.daysPerWeek + " days/week");

        boolean isCustom = rt.type != null && rt.type.equalsIgnoreCase("custom");
        holder.tvType.setText(isCustom ? "CUSTOM" : "PRESET");

        // styling
        holder.tvType.setBackgroundResource(R.drawable.tag_green_outline);
        holder.tvType.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gym_green));

        // open
        holder.card.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(rt);
        });

        // edit (go to SelectDays with pre-selected days + edit mode)
        holder.btnEdit.setOnClickListener(v -> {
            ArrayList<Integer> selectedDays = extractDaysFromRoutine(rt);

            Intent intent = new Intent(context, SelectDaysActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("routine", rt);
            intent.putIntegerArrayListExtra("preSelectedDays", selectedDays);
            context.startActivity(intent);

            // Optional: if your caller wants a callback too
            if (listener != null) listener.onEdit(rt);
        });

        // delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(rt);
        });
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    // Extract day indices from routine.days keys (expects lowercase keys like "monday", ...)
    private ArrayList<Integer> extractDaysFromRoutine(Routine routine) {
        ArrayList<Integer> selectedDays = new ArrayList<>();

        if (routine == null || routine.days == null || routine.days.isEmpty()) {
            return selectedDays;
        }

        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (int i = 0; i < dayKeys.length; i++) {
            if (routine.days.containsKey(dayKeys[i])) {
                selectedDays.add(i);
            }
        }

        return selectedDays;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvName, tvDays, tvType;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View v) {
            super(v);
            card = v.findViewById(R.id.card_routine);
            tvName = v.findViewById(R.id.tv_routine_name);
            tvDays = v.findViewById(R.id.tv_days);
            tvType = v.findViewById(R.id.tv_type);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}
