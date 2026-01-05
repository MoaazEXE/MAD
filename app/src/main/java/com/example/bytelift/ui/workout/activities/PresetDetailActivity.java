package com.example.bytelift.ui.workout.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget. TextView;
import android.widget. Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Preset;
import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.utils.DataManager;

import java.util.List;
import java.util.Map;

public class PresetDetailActivity extends AppCompatActivity {

    private Preset preset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_detail);

        preset = (Preset) getIntent().getSerializableExtra("preset");

        TextView tvName = findViewById(R.id. tv_preset_name);
        TextView tvDays = findViewById(R.id.tv_days_per_week);
        TextView tvDesc = findViewById(R.id.tv_preset_desc);
        LinearLayout layoutDays = findViewById(R.id.layout_preset_days);
        Button btnCopy = findViewById(R.id.btn_copy_workout);

        tvName. setText(preset.name);
        tvDays.setText(preset. daysPerWeek + " days/week");
        tvDesc.setText(preset.description != null ? preset.description : "");

        if (preset.days != null && !preset.days.isEmpty()) {
            for (Map.Entry<String, List<Exercise>> entry : preset.days.entrySet()) {
                String dayKey = entry.getKey();
                List<Exercise> exercises = entry. getValue();

                TextView dayLabel = new TextView(this);
                dayLabel.setText(capitalize(dayKey));
                dayLabel.setTextColor(getResources().getColor(R.color.gym_green));
                dayLabel.setTextSize(20f);
                dayLabel.setTypeface(null, Typeface. BOLD);
                LinearLayout. LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout. LayoutParams.WRAP_CONTENT,
                        LinearLayout. LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(0, 16, 0, 12);
                dayLabel.setLayoutParams(lp);
                layoutDays.addView(dayLabel);

                for (Exercise ex : exercises) {
                    TextView exView = new TextView(this);
                    String detail = "• " + ex.name + " (" + ex.sets + "x" + ex.reps;
                    if (ex.weight != null) detail += " • " + ex.weight + "kg";
                    detail += ")";
                    exView.setText(detail);
                    exView.setTextColor(getResources().getColor(R.color.text_white));
                    exView.setTextSize(15f);
                    exView. setPadding(24, 0, 0, 12);
                    layoutDays.addView(exView);
                }
            }
        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText("No exercises available yet");
            placeholder.setTextColor(getResources().getColor(R.color.text_muted));
            layoutDays.addView(placeholder);
        }

        btnCopy.setOnClickListener(v -> copyPresetToUser());
    }

    private void copyPresetToUser() {
        // Create routine from preset
        Routine routine = new Routine();
        routine.routineId = "routine_" + System.currentTimeMillis();
        routine.name = preset.name;
        routine. type = "preset";
        routine.daysPerWeek = preset.daysPerWeek;
        routine.days = preset.days;
        routine.createdAt = System.currentTimeMillis();
        routine.updatedAt = routine.createdAt;

        // Save to data manager
        DataManager.addRoutine(routine);

        Toast.makeText(this, "Workout copied!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String capitalize(String str) {
        if (str == null || str. isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}