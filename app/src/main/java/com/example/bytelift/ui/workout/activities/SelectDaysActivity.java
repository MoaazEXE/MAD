package com.example.bytelift.ui.workout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget. Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.models.Routine;
import com.google.android.material.button.MaterialButton;
import java. util.ArrayList;

public class SelectDaysActivity extends AppCompatActivity {

    private GridLayout gridDays;
    private ArrayList<Integer> selectedDays = new ArrayList<>();
    private String[] dayLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private MaterialButton[] dayButtons = new MaterialButton[7];

    // ✅ Edit mode support
    private boolean isEditMode = false;
    private Routine existingRoutine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R. layout.activity_select_days);

        gridDays = findViewById(R.id.grid_days);
        Button btnNext = findViewById(R.id.btn_next);

        // ✅ Check if in edit mode
        isEditMode = getIntent().getBooleanExtra("editMode", false);

        if (isEditMode) {
            existingRoutine = (Routine) getIntent().getSerializableExtra("routine");
            ArrayList<Integer> preSelectedDays = getIntent().getIntegerArrayListExtra("preSelectedDays");

            if (preSelectedDays != null && ! preSelectedDays.isEmpty()) {
                selectedDays. addAll(preSelectedDays);

                android.util.Log.d("SelectDays", "Edit mode: Loading pre-selected days:  " + selectedDays);
            }
        }

        createDayButtons();

        // ✅ Pre-select days if in edit mode
        if (isEditMode && ! selectedDays.isEmpty()) {
            for (int dayIndex : selectedDays) {
                if (dayIndex >= 0 && dayIndex < dayButtons.length) {
                    dayButtons[dayIndex].setBackgroundColor(getResources().getColor(R.color.gym_green));
                }
            }
        }

        btnNext.setOnClickListener(v -> {
            if (selectedDays.isEmpty()) {
                Toast. makeText(this, "Select at least one day", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, AddExercisesActivity.class);
            intent.putIntegerArrayListExtra("selectedDays", selectedDays);

            // ✅ Pass edit mode and routine data
            intent.putExtra("editMode", isEditMode);
            if (isEditMode && existingRoutine != null) {
                intent.putExtra("routine", existingRoutine);
            }

            startActivity(intent);
        });
    }

    private void createDayButtons() {
        for (int i = 0; i < 7; i++) {
            MaterialButton btn = new MaterialButton(this);
            final int dayIndex = i;

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params. columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setText(dayLabels[i]);
            btn.setTextColor(getResources().getColor(R.color.text_white));
            btn.setBackgroundColor(getResources().getColor(R.color.card_dark));
            btn.setCornerRadius(50);
            btn.setMinWidth(0);
            btn.setMinimumWidth(0);
            btn.setPadding(16, 32, 16, 32);

            btn.setOnClickListener(v -> toggleDay(dayIndex, btn));

            dayButtons[i] = btn;
            gridDays.addView(btn);
        }
    }

    private void toggleDay(int index, MaterialButton btn) {
        if (selectedDays.contains(index)) {
            selectedDays.remove(Integer.valueOf(index));
            btn.setBackgroundColor(getResources().getColor(R.color.card_dark));
        } else {
            selectedDays.add(index);
            btn.setBackgroundColor(getResources().getColor(R.color.gym_green));
        }
    }
}