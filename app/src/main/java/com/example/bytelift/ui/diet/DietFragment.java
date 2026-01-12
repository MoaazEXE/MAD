package com.example.bytelift.ui.diet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.bytelift.R;
import com.example.bytelift.databinding.FragmentDietBinding;import android.widget.Button;

public class DietFragment extends Fragment {

    private FragmentDietBinding binding;
    private DietViewModel dietViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dietViewModel = new ViewModelProvider(this).get(DietViewModel.class);
        binding = FragmentDietBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupObservers();
        setupButtons();

        return root;
    }

    private void setupObservers() {
        // 1. Loading State
        dietViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.layoutSetup.setVisibility(View.GONE);
                binding.layoutDashboard.setVisibility(View.GONE);
            } else {
                Integer target = dietViewModel.getDailyTarget().getValue();
                if (target != null && target > 0) {
                    showDashboard(target);
                } else {
                    showSetup();
                }
            }
        });

        // 2. Dashboard Visibility & Logic
        dietViewModel.getDailyTarget().observe(getViewLifecycleOwner(), target -> {
            binding.tvTarget.setText(String.valueOf(target));

            Integer eaten = dietViewModel.getCaloriesEaten().getValue();
            if (eaten == null) eaten = 0;
            updateRemainingText(target, eaten);

            Boolean isLoading = dietViewModel.getIsLoading().getValue();
            if (isLoading != null && !isLoading) {
                if (target > 0) {
                    showDashboard(target);
                } else {
                    showSetup();
                }
            }
        });

        // 3. Calorie Updates
        dietViewModel.getCaloriesEaten().observe(getViewLifecycleOwner(), eaten -> {
            binding.tvEaten.setText(String.valueOf(eaten));

            Integer target = dietViewModel.getDailyTarget().getValue();
            if (target == null) target = 0;
            updateRemainingText(target, eaten);
        });

        // 4. Macro Updates
        dietViewModel.getProteinEaten().observe(getViewLifecycleOwner(), p -> binding.tvProteinStat.setText("Protein: " + p + "g"));
        dietViewModel.getCarbsEaten().observe(getViewLifecycleOwner(), c -> binding.tvCarbsStat.setText("Carbs: " + c + "g"));
        dietViewModel.getFatsEaten().observe(getViewLifecycleOwner(), f -> binding.tvFatsStat.setText("Fats: " + f + "g"));

        // 5. PRE-FILL DATA
        dietViewModel.getExistingWeight().observe(getViewLifecycleOwner(), val -> { if (val != null) binding.etWeight.setText(val); });
        dietViewModel.getExistingAge().observe(getViewLifecycleOwner(), val -> { if (val != null) binding.etAge.setText(val); });
        dietViewModel.getExistingHeight().observe(getViewLifecycleOwner(), val -> { if (val != null) binding.etHeight.setText(val); });
        dietViewModel.getExistingGender().observe(getViewLifecycleOwner(), val -> { if (val != null) binding.etGender.setText(val); });
        dietViewModel.getExistingActivity().observe(getViewLifecycleOwner(), val -> { if (val != null) binding.etActivityLevel.setText(val); });

        // PRE-FILL GOAL (Reads text instead of toggle buttons)
        dietViewModel.getExistingGoal().observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                binding.etGoal.setText(goal);
            }
        });
    }

    private void updateRemainingText(int target, int eaten) {
        int remaining = target - eaten;
        if (remaining < 0) {
            binding.tvRemaining.setText(Math.abs(remaining) + " Extra");
            binding.tvRemaining.setTextColor(Color.parseColor("#FF5252"));
        } else {
            binding.tvRemaining.setText(String.valueOf(remaining));
            binding.tvRemaining.setTextColor(Color.parseColor("#42A5F5"));
        }
    }

    private void setupButtons() {
        // CALCULATE
        binding.btnCalculateTarget.setOnClickListener(v -> {
            String weightStr = binding.etWeight.getText().toString();
            String heightStr = binding.etHeight.getText().toString();
            String ageStr = binding.etAge.getText().toString();
            String gender = binding.etGender.getText().toString();
            String activityStr = binding.etActivityLevel.getText().toString();

            // UPDATED: Reads directly from Text Field now
            String goal = binding.etGoal.getText().toString();

            if (weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty() ||
                    gender.isEmpty() || activityStr.isEmpty() || goal.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                double weight = Double.parseDouble(weightStr);
                double height = Double.parseDouble(heightStr);
                int activity = Integer.parseInt(activityStr);
                String formattedGender = gender.toLowerCase().startsWith("m") ? "Male" : "Female";

                // ViewModel handles the string normalization for "goal"
                dietViewModel.saveTarget(age, formattedGender, weight, height, activity, goal);

                Integer newTarget = dietViewModel.getDailyTarget().getValue();
                if (newTarget != null && newTarget > 0) {
                    showDashboard(newTarget);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        // EDIT
        binding.btnEditGoal.setOnClickListener(v -> {
            showSetup();
        });

        // CANCEL
        binding.btnCancelEdit.setOnClickListener(v -> {
            Integer currentTarget = dietViewModel.getDailyTarget().getValue();
            if (currentTarget != null && currentTarget > 0) {
                String savedGoal = dietViewModel.getExistingGoal().getValue();
                if (savedGoal != null) binding.etGoal.setText(savedGoal);
                showDashboard(currentTarget);
            } else {
                Toast.makeText(getContext(), "You need to set a goal first!", Toast.LENGTH_SHORT).show();
            }
        });

        // MEAL
        binding.btnAddMeal.setOnClickListener(v -> {
            int p = parseInput(binding.etProteinInput);
            int c = parseInput(binding.etCarbsInput);
            int f = parseInput(binding.etFatsInput);
            dietViewModel.addMeal(p, c, f);
            binding.etProteinInput.setText("");
            binding.etCarbsInput.setText("");
            binding.etFatsInput.setText("");
            Toast.makeText(getContext(), "Meal added!", Toast.LENGTH_SHORT).show();
        });

        binding.btnReset.setOnClickListener(v -> dietViewModel.resetDay());

        // RECIPES NAVIGATION
        binding.btnRecipes.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_diet_to_nav_recipes);
        });
    }

    private void showDashboard(int target) {
        binding.layoutSetup.setVisibility(View.GONE);
        binding.layoutDashboard.setVisibility(View.VISIBLE);
        binding.tvTarget.setText(String.valueOf(target));
    }

    private void showSetup() {
        binding.layoutSetup.setVisibility(View.VISIBLE);
        binding.layoutDashboard.setVisibility(View.GONE);
    }

    private int parseInput(android.widget.EditText et) {
        try {
            return et.getText().toString().isEmpty() ? 0 : Integer.parseInt(et.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}