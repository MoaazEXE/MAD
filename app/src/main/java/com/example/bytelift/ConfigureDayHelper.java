package com.example.bytelift;

import android. app.AlertDialog;
import android.content. Context;
import android.view.LayoutInflater;
import android. view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview. widget.RecyclerView;

import java.util.List;

public class ConfigureDayHelper {

    public interface ConfiguredCallback {
        void onConfigured(List<WorkoutRoutineActivity. SelectedExercise> selected);
    }

    public static void showConfigureDayDialog(Context ctx, String dayName,
                                              List<WorkoutRoutineActivity.SelectedExercise> existing,
                                              ConfiguredCallback cb) {
        View view = LayoutInflater.from(ctx).inflate(R. layout.dialog_configure_day, null);
        RecyclerView rv = view.findViewById(R.id.recycler_exercises);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        List<WorkoutRoutineActivity.Exercise> all = WorkoutRoutineActivity. ExercisesProvider. getAllExercises();
        ConfigureExercisesAdapter adapter = new ConfigureExercisesAdapter(all, existing);
        rv.setAdapter(adapter);

        new AlertDialog.Builder(ctx)
                . setTitle("Configure " + dayName)
                .setView(view)
                . setPositiveButton("Save Day", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface d, int w) {
                        cb.onConfigured(adapter.getSelectedExercises());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}