package com.example.bytelift;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bytelift.databinding.ActivityOnboardingBinding;
import com.example.bytelift.models.FitnessProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OnboardingActivity extends BaseActivity {

    private ActivityOnboardingBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedGender = "Male";
    private String selectedWeightUnit = "kg";
    private String selectedHeightUnit = "cm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupDropdowns();
        setupRadioGroups();
        setupClickListeners();
    }

    private void setupDropdowns() {
        // Activity Level Dropdown
        String[] activityLevels = {
                "Sedentary (little to no exercise)",
                "Lightly Active (exercise 1-3 days/week)",
                "Moderately Active (exercise 3-5 days/week)",
                "Very Active (exercise 6-7 days/week)",
                "Extremely Active (physical job + exercise)"
        };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                activityLevels
        );
        binding.activityLevel.setAdapter(activityAdapter);

        // Fitness Goal Dropdown
        String[] fitnessGoals = {
                "Lose Weight",
                "Maintain Weight",
                "Build Muscle",
                "Get Stronger"
        };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                fitnessGoals
        );
        binding.fitnessGoal.setAdapter(goalAdapter);
    }

    private void setupRadioGroups() {
        // Gender selection
        binding.rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMale) {
                selectedGender = "Male";
            } else if (checkedId == R.id.rbFemale) {
                selectedGender = "Female";
            } else if (checkedId == R.id.rbOther) {
                selectedGender = "Other";
            }
        });

        // Weight unit selection
        binding.rgWeightUnit.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbKg) {
                selectedWeightUnit = "kg";
                binding.tilWeight.setHint("Weight (kg)");
            } else if (checkedId == R.id.rbLbs) {
                selectedWeightUnit = "lbs";
                binding.tilWeight.setHint("Weight (lbs)");
            }
        });

        // Height unit selection
        binding.rgHeightUnit.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCm) {
                selectedHeightUnit = "cm";
                binding.tilHeight.setHint("Height (cm)");
            } else if (checkedId == R.id.rbFt) {
                selectedHeightUnit = "ft";
                binding.tilHeight.setHint("Height (total inches)");
            }
        });
    }

    private void setupClickListeners() {
        binding.btnComplete.setOnClickListener(v -> saveFitnessProfile());
        binding.tvSkipForNow.setOnClickListener(v -> skipToMainActivity());
    }

    private void saveFitnessProfile() {
        // Validate inputs
        String ageStr = binding.etAge.getText().toString().trim();
        String weightStr = binding.etWeight.getText().toString().trim();
        String heightStr = binding.etHeight.getText().toString().trim();
        String activityLevelText = binding.activityLevel.getText().toString().trim();
        String fitnessGoalText = binding.fitnessGoal.getText().toString().trim();

        // Age validation
        if (ageStr.isEmpty()) {
            binding.etAge.setError("Age is required");
            binding.etAge.requestFocus();
            return;
        }

        // Weight validation
        if (weightStr.isEmpty()) {
            binding.etWeight.setError("Weight is required");
            binding.etWeight.requestFocus();
            return;
        }

        // Height validation
        if (heightStr.isEmpty()) {
            binding.etHeight.setError("Height is required");
            binding.etHeight.requestFocus();
            return;
        }

        // Activity level validation
        if (activityLevelText.isEmpty()) {
            binding.activityLevel.setError("Please select activity level");
            binding.activityLevel.requestFocus();
            return;
        }

        // Fitness goal validation
        if (fitnessGoalText.isEmpty()) {
            binding.fitnessGoal.setError("Please select fitness goal");
            binding.fitnessGoal.requestFocus();
            return;
        }

        int age = Integer.parseInt(ageStr);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        // Validate ranges
        if (age < 13 || age > 120) {
            binding.etAge.setError("Please enter a valid age (13-120)");
            binding.etAge.requestFocus();
            return;
        }

        if (weight <= 0 || weight > 500) {
            binding.etWeight.setError("Please enter a valid weight");
            binding.etWeight.requestFocus();
            return;
        }

        if (height <= 0 || height > 300) {
            binding.etHeight.setError("Please enter a valid height");
            binding.etHeight.requestFocus();
            return;
        }

        // Get simplified activity level
        String activityLevel = getActivityLevelSimple(activityLevelText);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnComplete.setEnabled(false);

        // Create fitness profile
        FitnessProfile fitnessProfile = new FitnessProfile(
                userId, selectedGender, age, weight, height,
                selectedWeightUnit, selectedHeightUnit, activityLevel, fitnessGoalText
        );

        // Save to Firestore
        db.collection("fitnessProfiles").document(userId)
                .set(fitnessProfile)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(OnboardingActivity.this,
                            "Profile completed! Daily calorie goal: " + fitnessProfile.getDailyCalorieGoal() + " kcal",
                            Toast.LENGTH_LONG).show();
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnComplete.setEnabled(true);
                    Toast.makeText(OnboardingActivity.this,
                            "Failed to save profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private String getActivityLevelSimple(String activityText) {
        if (activityText.contains("Sedentary")) return "Sedentary";
        if (activityText.contains("Lightly")) return "Lightly Active";
        if (activityText.contains("Moderately")) return "Moderately Active";
        if (activityText.contains("Very")) return "Very Active";
        if (activityText.contains("Extremely")) return "Extremely Active";
        return "Moderately Active";
    }

    private void skipToMainActivity() {
        Toast.makeText(this, "You can complete your profile later in Settings", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}