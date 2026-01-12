package com.example.bytelift.ui.workout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Preset;

import java.util.ArrayList;
import java.util.List;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    public interface OnPresetClickListener {
        void onOpen(Preset preset);
    }

    private final List<Preset> presets;
    private final OnPresetClickListener listener;

    public PresetAdapter(List<Preset> presets, OnPresetClickListener listener) {
        this.presets = (presets != null) ? presets : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Preset p = presets.get(position);

        h.tvName.setText(p.name);
        h.tvDays.setText(p.daysPerWeek + " days/week");
        h.tvType.setText("PRESET");

        h.card.setOnClickListener(v -> {
            if (listener != null) listener.onOpen(p);
        });

        // hide edit/delete for presets
        h.btnEdit.setVisibility(View.GONE);
        h.btnDelete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View card;
        public TextView tvName, tvDays, tvType;
        public ImageButton btnEdit, btnDelete;

        public ViewHolder(View v) {
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
