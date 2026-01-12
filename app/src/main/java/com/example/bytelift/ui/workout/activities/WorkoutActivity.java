package com.example.bytelift.ui.workout.activities;

import android.app.AlertDialog;
import android.content. Intent;
import android.os.Bundle;
import android.view. LayoutInflater;
import android. view.View;
import android. widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app. AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview. widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com. google.android.material.bottomsheet. BottomSheetDialog;
import com.example.bytelift. R;
import com.example.bytelift.ui.workout.adapters.RoutineAdapter;
import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.utils.DataManager;  // ← ADD THIS
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private RoutineAdapter routineAdapter;
    private List<Routine> routines;  // ← CHANGED
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        DataManager.init(this);

        routines = DataManager.getRoutines();  // ← GET FROM DATA MANAGER

        recycler = findViewById(R.id. recycler_routines);
        tvEmpty = findViewById(R.id.tv_empty);
        Button btnAdd = findViewById(R.id.btn_add_routine);

        routineAdapter = new RoutineAdapter(routines, new RoutineAdapter.OnRoutineClickListener() {
            @Override
            public void onOpen(Routine routine) {
                Intent intent = new Intent(WorkoutActivity. this, RoutineDetailActivity. class);
                intent.putExtra("routine", routine);
                startActivity(intent);
            }

            @Override
            public void onEdit(Routine routine) {
                // Navigate to edit flow starting from day selection
                Intent intent = new Intent(WorkoutActivity.this, SelectDaysActivity. class);
                intent.putExtra("editMode", true);
                intent.putExtra("routine", routine);
                startActivity(intent);
            }

            @Override
            public void onDelete(Routine routine) {
                deleteRoutine(routine);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(routineAdapter);

        btnAdd.setOnClickListener(v -> showAddRoutineSheet());

        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        android.util.Log.d("DEBUG_WORKOUT", "===== WORKOUT ACTIVITY onResume =====");
        android.util.Log.d("DEBUG_WORKOUT", "Routines in DataManager: " + DataManager. getRoutines().size());

        List<Routine> dmRoutines = DataManager.getRoutines();
        for (int i = 0; i < dmRoutines.size(); i++) {
            Routine r = dmRoutines.get(i);
            android.util.Log.d("DEBUG_WORKOUT", "DataManager Routine " + i + ": '" + r.name + "'");
        }

        android.util.Log.d("DEBUG_WORKOUT", "Routines in adapter list: " + routines.size());
        for (int i = 0; i < routines.size(); i++) {
            Routine r = routines.get(i);
            android.util.Log.d("DEBUG_WORKOUT", "Adapter Routine " + i + ":  '" + r.name + "'");
        }

        routineAdapter.notifyDataSetChanged();
        updateEmptyState();

        android.util.Log.d("DEBUG_WORKOUT", "===== onResume END =====");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Refresh the list when coming back via intent
        routineAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
    private void updateEmptyState() {
        tvEmpty.setVisibility(routines.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void deleteRoutine(Routine routine) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Routine")
                .setMessage("Delete \"" + routine.name + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    DataManager.removeRoutine(routine);  // ← USE DATA MANAGER
                    routineAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddRoutineSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_routine, null);
        sheet.setContentView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> sheet.dismiss());

        CardView cardCustom = view.findViewById(R. id.card_custom);
        CardView cardPresets = view. findViewById(R.id.card_presets);

        cardCustom.setOnClickListener(v -> {
            sheet.dismiss();
            startActivity(new Intent(this, SelectDaysActivity. class));
        });

        cardPresets.setOnClickListener(v -> {
            sheet.dismiss();
            startActivity(new Intent(this, PresetsActivity.class));
        });

        sheet.show();
    }
}