package com.example.bytelift.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.bytelift.R;
import com.example.bytelift.ui.diet.DietViewModel;
import com.example.bytelift.ui.workout.activities.WorkoutActivity;
import com.example.bytelift.ui.workout.models.Routine;
import com.example.bytelift.ui.workout.utils.DataManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private DietViewModel dietViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Views
        TextView tvGreeting = root.findViewById(R.id.tvGreeting);
        TextView tvStreak = root.findViewById(R.id.tvStreak);
        TextView tvFocusText = root.findViewById(R.id.tvFocusText);

        TextView tvWorkoutSummary = root.findViewById(R.id.tvWorkoutSummary);
        Button btnOpenWorkout = root.findViewById(R.id.btnOpenWorkout);

        TextView tvDietSummary = root.findViewById(R.id.tvDietSummary);
        TextView tvMacroSummary = root.findViewById(R.id.tvMacroSummary);
        Button btnOpenDiet = root.findViewById(R.id.btnOpenDiet);

        Button btnViewRecipes = root.findViewById(R.id.btnViewRecipes);

        TextView tvInsight = root.findViewById(R.id.tvInsight);

        ImageButton btnProfile = root.findViewById(R.id.btnProfile);

        // Nav
        NavController navController = NavHostFragment.findNavController(this);

        // PROFILE -> nav_profile
        btnProfile.setOnClickListener(v -> navController.navigate(R.id.nav_profile));

        // Greeting / streak
        tvGreeting.setText("Stay consistent 👋");
        tvStreak.setText("🔥 Streak: 1 day");

        // -------------------------
        // WORKOUT: pull from DataManager
        // -------------------------
        DataManager.init(requireContext());
        List<Routine> routines = DataManager.getRoutines();

        String nextRoutineName = "—";
        if (routines != null && !routines.isEmpty()) {
            Routine r0 = routines.get(0);
            if (r0 != null && r0.name != null && !r0.name.trim().isEmpty()) {
                nextRoutineName = r0.name;
            }
        }

        int routineCount = (routines == null) ? 0 : routines.size();
        tvWorkoutSummary.setText("Routines: " + routineCount + " • Next: " + nextRoutineName);

        if (routineCount > 0) {
            tvFocusText.setText(nextRoutineName + " • Tap Workout to continue");
        } else {
            tvFocusText.setText("No workout routine yet. Create one in Workout.");
        }

        // Workout is Activity
        btnOpenWorkout.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), WorkoutActivity.class))
        );

        // -------------------------
        // DIET: ViewModel LiveData
        // -------------------------
        dietViewModel = new ViewModelProvider(this).get(DietViewModel.class);

        tvDietSummary.setText("Goal not set • Tap Add Total");
        tvMacroSummary.setText("P: 0g • C: 0g • F: 0g");

        dietViewModel.getDailyTarget().observe(getViewLifecycleOwner(), target -> {
            Integer eaten = dietViewModel.getCaloriesEaten().getValue();
            if (eaten == null) eaten = 0;

            int safeTarget = (target == null) ? 0 : target;

            if (safeTarget <= 0) {
                tvDietSummary.setText("Goal not set • Tap Add Total");
            } else {
                int remaining = safeTarget - eaten;
                tvDietSummary.setText("Eaten: " + eaten + " / " + safeTarget + " kcal • Remaining: " + remaining);
            }

            updateInsight(tvInsight, routineCount, safeTarget, eaten);
        });

        dietViewModel.getCaloriesEaten().observe(getViewLifecycleOwner(), eaten -> {
            Integer target = dietViewModel.getDailyTarget().getValue();
            int safeTarget = (target == null) ? 0 : target;
            int safeEaten = (eaten == null) ? 0 : eaten;

            if (safeTarget <= 0) {
                tvDietSummary.setText("Goal not set • Tap Add Total");
            } else {
                int remaining = safeTarget - safeEaten;
                tvDietSummary.setText("Eaten: " + safeEaten + " / " + safeTarget + " kcal • Remaining: " + remaining);
            }

            updateInsight(tvInsight, routineCount, safeTarget, safeEaten);
        });

        dietViewModel.getProteinEaten().observe(getViewLifecycleOwner(), p -> {
            Integer c = dietViewModel.getCarbsEaten().getValue();
            Integer f = dietViewModel.getFatsEaten().getValue();
            tvMacroSummary.setText("P: " + safeInt(p) + "g • C: " + safeInt(c) + "g • F: " + safeInt(f) + "g");
        });

        dietViewModel.getCarbsEaten().observe(getViewLifecycleOwner(), c -> {
            Integer p = dietViewModel.getProteinEaten().getValue();
            Integer f = dietViewModel.getFatsEaten().getValue();
            tvMacroSummary.setText("P: " + safeInt(p) + "g • C: " + safeInt(c) + "g • F: " + safeInt(f) + "g");
        });

        dietViewModel.getFatsEaten().observe(getViewLifecycleOwner(), f -> {
            Integer p = dietViewModel.getProteinEaten().getValue();
            Integer c = dietViewModel.getCarbsEaten().getValue();
            tvMacroSummary.setText("P: " + safeInt(p) + "g • C: " + safeInt(c) + "g • F: " + safeInt(f) + "g");
        });

        // ADD TOTAL -> open Diet page
        btnOpenDiet.setOnClickListener(v -> navController.navigate(R.id.nav_diet));

        // ✅ VIEW RECIPES -> open Recipes destination
        // IMPORTANT: this requires you to have a destination with id nav_recipes in your nav graph.
        btnViewRecipes.setOnClickListener(v -> navController.navigate(R.id.nav_recipes));

        return root;
    }

    private int safeInt(Integer v) {
        return (v == null) ? 0 : v;
    }

    private void updateInsight(TextView tvInsight, int routineCount, int target, int eaten) {
        if (target <= 0) {
            tvInsight.setText("💡 Tip: Set a calorie goal in Diet to track progress.");
            return;
        }

        int remaining = target - eaten;

        if (routineCount <= 0) {
            tvInsight.setText("💡 Tip: Create your first workout routine to start building consistency.");
            return;
        }

        if (remaining < 0) {
            tvInsight.setText("💡 Tip: You’re over target today — consider a lighter meal later.");
        } else if (remaining == 0) {
            tvInsight.setText("💡 Nice! You hit your calorie target today.");
        } else {
            tvInsight.setText("💡 You still have " + remaining + " kcal left today.");
        }
    }
}
