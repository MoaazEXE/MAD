package com.example.bytelift.ui.workout.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget. TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget. RecyclerView;
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

    private List<Routine> routines;
    private OnRoutineClickListener listener;
    private Context context;

    public RoutineAdapter(List<Routine> routines, OnRoutineClickListener listener) {
        this.routines = routines;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater. from(context).inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Routine rt = routines.get(position);

        android.util.Log.d("DEBUG_ADAPTER", "===== BINDING POSITION " + position + " =====");
        android.util.Log.d("DEBUG_ADAPTER", "Routine name: '" + rt.name + "'");
        android.util.Log. d("DEBUG_ADAPTER", "Routine ID: " + rt. routineId);
        android.util.Log.d("DEBUG_ADAPTER", "Routine type:  " + rt.type);

        holder.tvName.setText(rt.name);
        holder.tvDays.setText(rt.daysPerWeek + " days/week");
        holder.tvType.setText(rt.type. equals("custom") ? "CUSTOM" : "PRESET");
        holder.tvType.setBackgroundResource(R.drawable.tag_green_outline);
        holder.tvType.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gym_green));

        holder.card.setOnClickListener(v -> listener.onOpen(rt));

        // ✅ UPDATED EDIT LOGIC
        holder.btnEdit.setOnClickListener(v -> {
            // Extract which days are in this routine
            ArrayList<Integer> selectedDays = extractDaysFromRoutine(rt);

            android.util.Log.d("DEBUG_ADAPTER", "Edit clicked for:  " + rt.name);
            android.util.Log.d("DEBUG_ADAPTER", "Extracted days: " + selectedDays);
            android.util.Log.d("DEBUG_ADAPTER", "Routine has days: " + (rt.days != null ? rt. days.keySet() : "null"));

            // Navigate to SelectDaysActivity with edit mode
            Intent intent = new Intent(context, SelectDaysActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("routine", rt);
            intent.putIntegerArrayListExtra("preSelectedDays", selectedDays);
            context. startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(rt));
    }

    @Override
    public int getItemCount() {
        return routines. size();
    }

    // ✅ NEW METHOD: Extract day indices from routine
    private ArrayList<Integer> extractDaysFromRoutine(Routine routine) {
        ArrayList<Integer> selectedDays = new ArrayList<>();

        if (routine.days == null || routine.days.isEmpty()) {
            android.util.Log. d("DEBUG_ADAPTER", "Routine has no days!");
            return selectedDays;
        }

        // Map day keys to indices
        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (int i = 0; i < dayKeys.length; i++) {
            if (routine.days.containsKey(dayKeys[i])) {
                selectedDays. add(i);
                android.util.Log.d("DEBUG_ADAPTER", "Found day: " + dayKeys[i] + " at index " + i);
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
            tvType = v.findViewById(R. id.tv_type);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}