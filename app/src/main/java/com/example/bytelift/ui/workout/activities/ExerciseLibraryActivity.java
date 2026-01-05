package com.example.bytelift.ui.workout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat. app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.adapters. ExerciseLibraryAdapter;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.ExerciseLibrary;
import com. google.android.material.tabs.TabLayout;
import java.util. ArrayList;
import java.util.List;

public class ExerciseLibraryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recycler;
    private ExerciseLibraryAdapter adapter;
    private List<Exercise> allExercises;
    private List<Exercise> filteredExercises;
    private String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_library);

        // Get the day from intent
        selectedDay = getIntent().getStringExtra("day");

        ImageButton btnBack = findViewById(R. id.btn_back);
        tabLayout = findViewById(R.id. tab_muscle_groups);
        recycler = findViewById(R.id.recycler_exercises);

        // Load all exercises
        allExercises = ExerciseLibrary.getAllExercises();
        filteredExercises = new ArrayList<>(allExercises);

        // Setup adapter
        adapter = new ExerciseLibraryAdapter(filteredExercises, this:: onExerciseSelected);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Setup tabs
        tabLayout.addTab(tabLayout. newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Chest"));
        tabLayout.addTab(tabLayout.newTab().setText("Back"));
        tabLayout. addTab(tabLayout.newTab().setText("Arms"));
        tabLayout.addTab(tabLayout. newTab().setText("Legs"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterExercises(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout. Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void filterExercises(int tabIndex) {
        filteredExercises. clear();

        switch (tabIndex) {
            case 0: // All
                filteredExercises.addAll(allExercises);
                break;
            case 1: // Chest
                filteredExercises.addAll(ExerciseLibrary.getExercisesByMuscleGroup("chest"));
                break;
            case 2: // Back
                filteredExercises.addAll(ExerciseLibrary.getExercisesByMuscleGroup("back"));
                break;
            case 3: // Arms
                filteredExercises.addAll(ExerciseLibrary.getExercisesByMuscleGroup("arms"));
                break;
            case 4: // Legs
                filteredExercises. addAll(ExerciseLibrary.getExercisesByMuscleGroup("legs"));
                break;
        }

        adapter.notifyDataSetChanged();
    }

    private void onExerciseSelected(Exercise exercise) {
        // Return the selected exercise back to AddExercisesActivity
        Intent resultIntent = new Intent();
        resultIntent. putExtra("exercise", exercise);
        resultIntent.putExtra("day", selectedDay);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
