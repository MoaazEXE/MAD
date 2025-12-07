package com.example.bytelift;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows preset templates for a single category.
 * You will add presets to PresetsProvider (sample provided) or extend as needed.
 */
public class PresetCategoryFragment extends Fragment {
    private static final String ARG_CATEGORY = "arg_category";
    private String category;

    public static PresetCategoryFragment newInstance(String category) {
        PresetCategoryFragment f = new PresetCategoryFragment();
        Bundle b = new Bundle();
        b.putString(ARG_CATEGORY, category);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) category = getArguments().getString(ARG_CATEGORY, "beginner");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preset_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.rvPresets);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<WorkoutRoutineActivity.WorkoutRoutine> presets = PresetsProvider.getPresetsForCategory(category);
        PresetListAdapter adapter = new PresetListAdapter(presets);
        rv.setAdapter(adapter);
    }

    private class PresetListAdapter extends RecyclerView.Adapter<PresetListAdapter.H> {
        private final List<WorkoutRoutineActivity.WorkoutRoutine> items;
        PresetListAdapter(List<WorkoutRoutineActivity.WorkoutRoutine> items) { this.items = items; }

        class H extends RecyclerView.ViewHolder {
            ImageView ivThumb;
            TextView tvTitle;
            H(View v) { super(v); ivThumb = v.findViewById(R.id.ivPresetThumb); tvTitle = v.findViewById(R.id.tvPresetTitle); }
        }

        @NonNull
        @Override
        public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
            return new H(v);
        }

        @Override
        public void onBindViewHolder(@NonNull H holder, int position) {
            WorkoutRoutineActivity.WorkoutRoutine preset = items.get(position);
            holder.tvTitle.setText(preset.name != null ? preset.name : "Preset " + position);
            // placeholder image: you will replace with actual images inside preset data if desired
            holder.ivThumb.setImageResource(R.drawable.ic_placeholder);
            holder.itemView.setOnClickListener(v -> {
                // open preset detail screen
                Intent i = new Intent(requireContext(), PresetDetailsActivity.class);
                // pass preset index via a small static holder or serialize — we'll pass via static store for simplicity
                PresetsProvider.setSelectedPreset(preset);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }
    }
}