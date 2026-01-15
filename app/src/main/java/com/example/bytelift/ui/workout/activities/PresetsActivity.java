package com.example.bytelift.ui.workout.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat. app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android. material.tabs.TabLayout;
import com.example.bytelift.R;
import com.example.bytelift.ui.workout.adapters.PresetAdapter;
import com.example.bytelift.ui.workout.models.Exercise;
import com.example.bytelift.ui.workout.models.Preset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PresetsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView presetRecycler;
    private PresetAdapter adapter;
    private List<Preset> presets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        tabLayout = findViewById(R.id.tab_levels);
        presetRecycler = findViewById(R.id.recycler_presets);

        adapter = new PresetAdapter(presets, this:: openPresetDetail);
        presetRecycler.setLayoutManager(new LinearLayoutManager(this));
        presetRecycler.setAdapter(adapter);

        tabLayout. addTab(tabLayout.newTab().setText("Beginner"));
        tabLayout.addTab(tabLayout.newTab().setText("Intermediate"));
        tabLayout.addTab(tabLayout. newTab().setText("Expert"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadPresets(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout. Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadPresets(0); // Load beginner by default
    }

    private void loadPresets(int idx) {
        String level = idx == 0 ? "beginner" : idx == 1 ? "intermediate" : "expert";

        // Mock data - replace with Firebase later
        presets.clear();
        presets.addAll(getMockPresets(level));
        adapter.notifyDataSetChanged();
    }

    private List<Preset> getMockPresets(String level) {
        List<Preset> mockPresets = new ArrayList<>();

        if (level.equals("beginner")) {
            // Preset 1: Full Body Beginner
            Preset p1 = new Preset();
            p1.presetId = "preset1";
            p1.name = "Full Body Beginner";
            p1.level = "beginner";
            p1.daysPerWeek = 3;
            p1.description = "Perfect for beginners starting their fitness journey";
            p1.days = new LinkedHashMap<>();

            // Monday exercises
            ArrayList<Exercise> mondayEx = new ArrayList<>();
            Exercise ex1 = new Exercise();
            ex1.id = "ex1";
            ex1.name = "Squat";
            ex1.muscleGroup = "legs";
            ex1.sets = 3;
            ex1.reps = 10;
            ex1.weight = 40.0;
            mondayEx.add(ex1);

            Exercise ex2 = new Exercise();
            ex2.id = "ex2";
            ex2.name = "Bench Press";
            ex2.muscleGroup = "chest";
            ex2.sets = 3;
            ex2.reps = 10;
            ex2.weight = 30.0;
            mondayEx.add(ex2);

            p1.days.put("monday", mondayEx);

            // Wednesday exercises
            ArrayList<Exercise> wedEx = new ArrayList<>();
            Exercise ex3 = new Exercise();
            ex3.id = "ex3";
            ex3.name = "Deadlift";
            ex3.muscleGroup = "back";
            ex3.sets = 3;
            ex3.reps = 8;
            ex3.weight = 50.0;
            wedEx. add(ex3);

            p1.days.put("wednesday", wedEx);

            mockPresets.add(p1);

            // Preset 2: Starting Strength
            Preset p2 = new Preset();
            p2.presetId = "preset2";
            p2.name = "Starting Strength";
            p2.level = "beginner";
            p2.daysPerWeek = 3;
            p2.description = "Classic beginner strength program";
            p2.days = new LinkedHashMap<>();  // ✅ Fixed

            ArrayList<Exercise> p2Monday = new ArrayList<>();
            Exercise p2ex1 = new Exercise();
            p2ex1.id = "ex4";
            p2ex1.name = "Squat";
            p2ex1.muscleGroup = "legs";
            p2ex1.sets = 5;
            p2ex1.reps = 5;
            p2ex1.weight = 60.0;
            p2Monday.add(p2ex1);

            p2.days.put("monday", p2Monday);
            mockPresets.add(p2);

        } else if (level.equals("intermediate")) {

            // 1️⃣ Push Pull Legs (PPL)
            Preset ppl = new Preset();
            ppl.presetId = "preset_ppl";
            ppl. name = "Push Pull Legs (PPL)";
            ppl.level = "intermediate";
            ppl.daysPerWeek = 6;
            ppl.description = "5-6 days | Classic PPL split for muscle building";
            ppl.days = new LinkedHashMap<>();

            // Monday - Push
            ArrayList<Exercise> pplPush = new ArrayList<>();
            pplPush.add(createExercise("Barbell Bench Press", "chest", 4, 8, 60.0));
            pplPush.add(createExercise("Incline Dumbbell Press", "chest", 3, 10, 25.0));
            pplPush.add(createExercise("Shoulder Press", "arms", 3, 10, 20.0));
            pplPush.add(createExercise("Lateral Raise", "arms", 3, 15, 10.0));
            pplPush.add(createExercise("Triceps Pushdown", "arms", 3, 12, 30.0));
            ppl.days.put("monday", pplPush);

            // Tuesday - Pull
            ArrayList<Exercise> pplPull = new ArrayList<>();
            pplPull.add(createExercise("Pull-Ups", "back", 4, 10, 0.0));
            pplPull.add(createExercise("Barbell Row", "back", 3, 10, 50.0));
            pplPull.add(createExercise("Seated Cable Row", "back", 3, 12, 40.0));
            pplPull.add(createExercise("Face Pull", "back", 3, 15, 20.0));
            pplPull.add(createExercise("Barbell Curl", "arms", 3, 10, 25.0));
            ppl.days.put("tuesday", pplPull);

            // Wednesday - Legs
            ArrayList<Exercise> pplLegs = new ArrayList<>();
            pplLegs.add(createExercise("Back Squat", "legs", 4, 8, 80.0));
            pplLegs.add(createExercise("Romanian Deadlift", "legs", 3, 10, 60.0));
            pplLegs.add(createExercise("Leg Press", "legs", 3, 12, 100.0));
            pplLegs.add(createExercise("Leg Curl", "legs", 3, 12, 40.0));
            pplLegs.add(createExercise("Standing Calf Raise", "legs", 4, 15, 50.0));
            ppl.days.put("wednesday", pplLegs);

            mockPresets.add(ppl);

            // 2️⃣ Upper / Lower (4 Day)
            Preset upperLower = new Preset();
            upperLower.presetId = "preset_upper_lower";
            upperLower.name = "Upper / Lower (4 Day)";
            upperLower.level = "intermediate";
            upperLower.daysPerWeek = 4;
            upperLower.description = "4 days | Balanced upper/lower split";
            upperLower.days = new LinkedHashMap<>();

            // Monday - Upper A
            ArrayList<Exercise> ulUpperA = new ArrayList<>();
            ulUpperA.add(createExercise("Bench Press", "chest", 4, 8, 60.0));
            ulUpperA.add(createExercise("Pull-Ups", "back", 4, 10, 0.0));
            ulUpperA.add(createExercise("Shoulder Press", "arms", 3, 8, 20.0));
            ulUpperA.add(createExercise("Dumbbell Row", "back", 3, 10, 25.0));
            ulUpperA.add(createExercise("Triceps Pushdown", "arms", 3, 12, 30.0));
            ulUpperA.add(createExercise("Dumbbell Curl", "arms", 3, 12, 15.0));
            upperLower.days.put("monday", ulUpperA);

            // Tuesday - Lower A
            ArrayList<Exercise> ulLowerA = new ArrayList<>();
            ulLowerA.add(createExercise("Squat", "legs", 4, 8, 80.0));
            ulLowerA.add(createExercise("Romanian Deadlift", "legs", 3, 8, 60.0));
            ulLowerA.add(createExercise("Leg Press", "legs", 3, 10, 100.0));
            ulLowerA.add(createExercise("Calf Raise", "legs", 4, 15, 50.0));
            upperLower. days.put("tuesday", ulLowerA);

            // Wednesday - Upper B
            ArrayList<Exercise> ulUpperB = new ArrayList<>();
            ulUpperB.add(createExercise("Incline Bench Press", "chest", 4, 8, 50.0));
            ulUpperB.add(createExercise("Seated Row", "back", 4, 10, 40.0));
            ulUpperB.add(createExercise("Lateral Raise", "arms", 3, 15, 10.0));
            ulUpperB.add(createExercise("Face Pull", "back", 3, 15, 20.0));
            ulUpperB.add(createExercise("Hammer Curl", "arms", 3, 10, 15.0));
            ulUpperB.add(createExercise("Overhead Triceps Extension", "arms", 3, 10, 20.0));
            upperLower.days.put("wednesday", ulUpperB);

            // Thursday - Lower B
            ArrayList<Exercise> ulLowerB = new ArrayList<>();
            ulLowerB.add(createExercise("Deadlift", "back", 3, 5, 100.0));
            ulLowerB.add(createExercise("Bulgarian Split Squat", "legs", 3, 8, 20.0));
            ulLowerB.add(createExercise("Leg Curl", "legs", 3, 12, 40.0));
            ulLowerB.add(createExercise("Seated Calf Raise", "legs", 4, 15, 40.0));
            upperLower.days.put("thursday", ulLowerB);

            mockPresets.add(upperLower);

            // 3️⃣ Bro Split (Classic)
            Preset broSplit = new Preset();
            broSplit.presetId = "preset_bro_split";
            broSplit.name = "Bro Split (Classic)";
            broSplit. level = "intermediate";
            broSplit.daysPerWeek = 5;
            broSplit.description = "5 days | One muscle group per day";
            broSplit.days = new LinkedHashMap<>();

            // Monday - Chest
            ArrayList<Exercise> bsChest = new ArrayList<>();
            bsChest.add(createExercise("Bench Press", "chest", 4, 8, 60.0));
            bsChest.add(createExercise("Incline Dumbbell Press", "chest", 3, 10, 25.0));
            bsChest.add(createExercise("Chest Fly", "chest", 3, 12, 15.0));
            bsChest.add(createExercise("Push-Ups", "chest", 2, 20, 0.0));
            broSplit.days.put("monday", bsChest);

            // Tuesday - Back
            ArrayList<Exercise> bsBack = new ArrayList<>();
            bsBack.add(createExercise("Deadlift", "back", 3, 5, 100.0));
            bsBack.add(createExercise("Pull-Ups", "back", 4, 8, 0.0));
            bsBack.add(createExercise("Barbell Row", "back", 3, 10, 50.0));
            bsBack.add(createExercise("Lat Pulldown", "back", 3, 12, 50.0));
            broSplit.days.put("tuesday", bsBack);

            // Wednesday - Shoulders
            ArrayList<Exercise> bsShoulders = new ArrayList<>();
            bsShoulders.add(createExercise("Overhead Press", "arms", 4, 8, 40.0));
            bsShoulders.add(createExercise("Lateral Raise", "arms", 4, 15, 10.0));
            bsShoulders.add(createExercise("Rear Delt Fly", "arms", 3, 15, 8.0));
            bsShoulders.add(createExercise("Shrugs", "back", 3, 12, 40.0));
            broSplit.days.put("wednesday", bsShoulders);

            // Thursday - Arms
            ArrayList<Exercise> bsArms = new ArrayList<>();
            bsArms.add(createExercise("Barbell Curl", "arms", 4, 10, 25.0));
            bsArms.add(createExercise("Hammer Curl", "arms", 3, 10, 15.0));
            bsArms.add(createExercise("Skull Crushers", "arms", 4, 10, 20.0));
            bsArms.add(createExercise("Triceps Pushdown", "arms", 3, 12, 30.0));
            broSplit.days.put("thursday", bsArms);

            // Friday - Legs
            ArrayList<Exercise> bsLegs = new ArrayList<>();
            bsLegs.add(createExercise("Squat", "legs", 4, 8, 80.0));
            bsLegs.add(createExercise("Leg Press", "legs", 3, 10, 100.0));
            bsLegs.add(createExercise("Romanian Deadlift", "legs", 3, 8, 60.0));
            bsLegs.add(createExercise("Leg Curl", "legs", 3, 12, 40.0));
            bsLegs.add(createExercise("Calf Raise", "legs", 4, 15, 50.0));
            broSplit.days.put("friday", bsLegs);

            mockPresets.add(broSplit);

        } else { // expert

            // 4️⃣ Push Pull Legs (6 Day)
            Preset ppl6 = new Preset();
            ppl6.presetId = "preset_ppl6";
            ppl6.name = "Push Pull Legs (6 Day)";
            ppl6.level = "expert";
            ppl6.daysPerWeek = 6;
            ppl6.description = "6 days | Advanced PPL with strength & hypertrophy";
            ppl6.days = new LinkedHashMap<>();

            // Monday - Push A (Strength)
            ArrayList<Exercise> ppl6PushA = new ArrayList<>();
            ppl6PushA.add(createExercise("Bench Press", "chest", 5, 5, 80.0));
            ppl6PushA.add(createExercise("Overhead Press", "arms", 4, 6, 50.0));
            ppl6PushA.add(createExercise("Weighted Dips", "chest", 3, 8, 20.0));
            ppl6PushA.add(createExercise("Lateral Raise", "arms", 3, 15, 10.0));
            ppl6.days.put("monday", ppl6PushA);

            // Tuesday - Pull A (Strength)
            ArrayList<Exercise> ppl6PullA = new ArrayList<>();
            ppl6PullA.add(createExercise("Deadlift", "back", 4, 5, 120.0));
            ppl6PullA.add(createExercise("Weighted Pull-Ups", "back", 4, 6, 10.0));
            ppl6PullA.add(createExercise("Barbell Row", "back", 3, 8, 60.0));
            ppl6PullA.add(createExercise("Barbell Curl", "arms", 3, 8, 30.0));
            ppl6.days.put("tuesday", ppl6PullA);

            // Wednesday - Legs A (Strength)
            ArrayList<Exercise> ppl6LegsA = new ArrayList<>();
            ppl6LegsA.add(createExercise("Squat", "legs", 5, 5, 100.0));
            ppl6LegsA.add(createExercise("Romanian Deadlift", "legs", 3, 8, 80.0));
            ppl6LegsA.add(createExercise("Walking Lunges", "legs", 3, 10, 20.0));
            ppl6LegsA.add(createExercise("Standing Calf Raise", "legs", 4, 15, 60.0));
            ppl6.days.put("wednesday", ppl6LegsA);

            // Thursday - Push B (Hypertrophy)
            ArrayList<Exercise> ppl6PushB = new ArrayList<>();
            ppl6PushB.add(createExercise("Incline Bench", "chest", 4, 10, 60.0));
            ppl6PushB.add(createExercise("Dumbbell Shoulder Press", "arms", 3, 10, 25.0));
            ppl6PushB.add(createExercise("Cable Fly", "chest", 3, 15, 20.0));
            ppl6PushB.add(createExercise("Triceps Pushdown", "arms", 3, 12, 35.0));
            ppl6.days.put("thursday", ppl6PushB);

            // Friday - Pull B (Hypertrophy)
            ArrayList<Exercise> ppl6PullB = new ArrayList<>();
            ppl6PullB.add(createExercise("Lat Pulldown", "back", 4, 10, 60.0));
            ppl6PullB.add(createExercise("Seated Row", "back", 3, 12, 50.0));
            ppl6PullB.add(createExercise("Face Pull", "back", 3, 15, 25.0));
            ppl6PullB.add(createExercise("Hammer Curl", "arms", 3, 10, 18.0));
            ppl6. days.put("friday", ppl6PullB);

            // Saturday - Legs B (Hypertrophy)
            ArrayList<Exercise> ppl6LegsB = new ArrayList<>();
            ppl6LegsB.add(createExercise("Hack Squat", "legs", 4, 10, 90.0));
            ppl6LegsB.add(createExercise("Leg Curl", "legs", 3, 12, 50.0));
            ppl6LegsB.add(createExercise("Leg Extension", "legs", 3, 15, 50.0));
            ppl6LegsB.add(createExercise("Seated Calf Raise", "legs", 4, 15, 50.0));
            ppl6.days.put("saturday", ppl6LegsB);

            mockPresets.add(ppl6);

            // 5️⃣ Arnold Split
            Preset arnold = new Preset();
            arnold.presetId = "preset_arnold";
            arnold.name = "Arnold Split";
            arnold.level = "expert";
            arnold.daysPerWeek = 6;
            arnold.description = "6 days | Chest+Back, Shoulders+Arms, Legs (repeat)";
            arnold.days = new LinkedHashMap<>();

            // Monday & Thursday - Chest + Back
            ArrayList<Exercise> arnoldChestBack = new ArrayList<>();
            arnoldChestBack. add(createExercise("Bench Press", "chest", 4, 8, 80.0));
            arnoldChestBack.add(createExercise("Incline Dumbbell Press", "chest", 3, 8, 30.0));
            arnoldChestBack.add(createExercise("Pull-Ups", "back", 4, 8, 0.0));
            arnoldChestBack.add(createExercise("Barbell Row", "back", 3, 10, 60.0));
            arnold.days.put("monday", arnoldChestBack);
            arnold.days.put("thursday", arnoldChestBack);

            // Tuesday & Friday - Shoulders + Arms
            ArrayList<Exercise> arnoldShouldersArms = new ArrayList<>();
            arnoldShouldersArms.add(createExercise("Overhead Press", "arms", 4, 8, 50.0));
            arnoldShouldersArms.add(createExercise("Lateral Raise", "arms", 4, 12, 12.0));
            arnoldShouldersArms.add(createExercise("Barbell Curl", "arms", 3, 10, 30.0));
            arnoldShouldersArms.add(createExercise("Skull Crushers", "arms", 3, 10, 25.0));
            arnold.days.put("tuesday", arnoldShouldersArms);
            arnold.days.put("friday", arnoldShouldersArms);

            // Wednesday & Saturday - Legs
            ArrayList<Exercise> arnoldLegs = new ArrayList<>();
            arnoldLegs.add(createExercise("Squat", "legs", 4, 8, 100.0));
            arnoldLegs.add(createExercise("Romanian Deadlift", "legs", 3, 8, 80.0));
            arnoldLegs.add(createExercise("Leg Press", "legs", 3, 12, 120.0));
            arnoldLegs.add(createExercise("Calf Raise", "legs", 4, 15, 60.0));
            arnold.days.put("wednesday", arnoldLegs);
            arnold.days.put("saturday", arnoldLegs);

            mockPresets.add(arnold);

            // 6️⃣ PHAT
            Preset phat = new Preset();
            phat.presetId = "preset_phat";
            phat.name = "PHAT";
            phat.level = "expert";
            phat.daysPerWeek = 5;
            phat.description = "5 days | Power Hypertrophy Adaptive Training";
            phat.days = new LinkedHashMap<>();

            // Monday - Upper Power
            ArrayList<Exercise> phatUpperPower = new ArrayList<>();
            phatUpperPower.add(createExercise("Bench Press", "chest", 5, 5, 85.0));
            phatUpperPower.add(createExercise("Bent Over Row", "back", 5, 5, 70.0));
            phatUpperPower.add(createExercise("Overhead Press", "arms", 3, 6, 50.0));
            phatUpperPower.add(createExercise("Weighted Pull-Ups", "back", 3, 6, 10.0));
            phat.days.put("monday", phatUpperPower);

            // Tuesday - Lower Power
            ArrayList<Exercise> phatLowerPower = new ArrayList<>();
            phatLowerPower.add(createExercise("Squat", "legs", 5, 5, 110.0));
            phatLowerPower.add(createExercise("Deadlift", "back", 3, 3, 130.0));
            phatLowerPower.add(createExercise("Leg Press", "legs", 2, 10, 140.0));
            phat.days.put("tuesday", phatLowerPower);

            // Wednesday - Back + Shoulders (Hypertrophy)
            ArrayList<Exercise> phatBackShoulders = new ArrayList<>();
            phatBackShoulders.add(createExercise("Lat Pulldown", "back", 4, 12, 60.0));
            phatBackShoulders.add(createExercise("Seated Row", "back", 3, 15, 50.0));
            phatBackShoulders.add(createExercise("Lateral Raise", "arms", 4, 15, 12.0));
            phatBackShoulders.add(createExercise("Rear Delt Fly", "arms", 3, 15, 8.0));
            phat.days.put("wednesday", phatBackShoulders);

            // Thursday - Lower Hypertrophy
            ArrayList<Exercise> phatLowerHyper = new ArrayList<>();
            phatLowerHyper.add(createExercise("Front Squat", "legs", 4, 8, 80.0));
            phatLowerHyper.add(createExercise("Leg Curl", "legs", 3, 12, 50.0));
            phatLowerHyper.add(createExercise("Leg Extension", "legs", 3, 15, 50.0));
            phatLowerHyper.add(createExercise("Calf Raise", "legs", 4, 20, 60.0));
            phat.days.put("thursday", phatLowerHyper);

            // Friday - Chest + Arms
            ArrayList<Exercise> phatChestArms = new ArrayList<>();
            phatChestArms. add(createExercise("Incline Bench", "chest", 4, 10, 65.0));
            phatChestArms.add(createExercise("Cable Fly", "chest", 3, 15, 20.0));
            phatChestArms.add(createExercise("Barbell Curl", "arms", 3, 10, 30.0));
            phatChestArms.add(createExercise("Triceps Pushdown", "arms", 3, 12, 35.0));
            phat.days.put("friday", phatChestArms);

            mockPresets.add(phat);
        }

        return mockPresets;
    }

    // Helper method to create exercises quickly
    private Exercise createExercise(String name, String muscleGroup, int sets, int reps, double weight) {
        Exercise ex = new Exercise();
        ex.id = "ex_" + System.currentTimeMillis() + "_" + Math.random();
        ex.name = name;
        ex.muscleGroup = muscleGroup;
        ex.sets = sets;
        ex.reps = reps;
        ex.weight = weight > 0 ? weight : null;
        return ex;
    }

    private void openPresetDetail(Preset preset) {
        Intent intent = new Intent(this, PresetDetailActivity.class);
        intent.putExtra("preset", preset);
        startActivity(intent);
    }
}