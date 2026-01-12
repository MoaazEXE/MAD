package com.example.bytelift.ui.diet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bytelift.models.FitnessProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class DietViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final String userId;

    // LiveData
    private final MutableLiveData<Integer> mDailyTarget = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> mCaloriesEaten = new MutableLiveData<>(0);

    private final MutableLiveData<Integer> mProteinEaten = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> mCarbsEaten = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> mFatsEaten = new MutableLiveData<>(0);

    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>(true);

    // EXISTING USER DATA
    private final MutableLiveData<String> mExistingWeight = new MutableLiveData<>(null);
    private final MutableLiveData<String> mExistingAge = new MutableLiveData<>(null);
    private final MutableLiveData<String> mExistingHeight = new MutableLiveData<>(null);
    private final MutableLiveData<String> mExistingGender = new MutableLiveData<>(null);
    private final MutableLiveData<String> mExistingActivity = new MutableLiveData<>(null);
    private final MutableLiveData<String> mExistingGoal = new MutableLiveData<>(null);

    public DietViewModel() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        mIsLoading.setValue(true);
        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Long target = document.getLong("dailyCalorieGoal");
                        if (target == null) target = document.getLong("dailyTarget");

                        Long eaten = document.getLong("caloriesEaten");
                        Long p = document.getLong("proteinEaten");
                        Long c = document.getLong("carbsEaten");
                        Long f = document.getLong("fatsEaten");

                        Double weight = document.getDouble("weight");
                        Double height = document.getDouble("height");
                        Long age = document.getLong("age");
                        String gender = document.getString("gender");
                        String activityLevel = document.getString("activityLevel");
                        String goal = document.getString("fitnessGoal");

                        if (weight != null) mExistingWeight.setValue(String.valueOf(weight.intValue()));
                        if (height != null) mExistingHeight.setValue(String.valueOf(height.intValue()));
                        if (age != null) mExistingAge.setValue(String.valueOf(age.intValue()));
                        if (gender != null) mExistingGender.setValue(gender);
                        if (goal != null) mExistingGoal.setValue(goal);

                        if (activityLevel != null) {
                            int level = 1;
                            if (activityLevel.equals("Lightly Active")) level = 2;
                            if (activityLevel.equals("Moderately Active")) level = 3;
                            if (activityLevel.equals("Very Active")) level = 4;
                            if (activityLevel.equals("Extremely Active")) level = 5;
                            mExistingActivity.setValue(String.valueOf(level));
                        }

                        if (target != null && target > 0) {
                            mDailyTarget.setValue(target.intValue());
                            if (eaten != null) mCaloriesEaten.setValue(eaten.intValue());
                            if (p != null) mProteinEaten.setValue(p.intValue());
                            if (c != null) mCarbsEaten.setValue(c.intValue());
                            if (f != null) mFatsEaten.setValue(f.intValue());
                        }
                    }
                    mIsLoading.setValue(false);
                })
                .addOnFailureListener(e -> mIsLoading.setValue(false));
    }

    public void saveTarget(int age, String gender, double weight, double height, int activityLevelInt, String rawGoal) {

        // 1. Normalize Activity Level
        String activityString;
        switch (activityLevelInt) {
            case 2: activityString = "Lightly Active"; break;
            case 3: activityString = "Moderately Active"; break;
            case 4: activityString = "Very Active"; break;
            case 5: activityString = "Extremely Active"; break;
            default: activityString = "Sedentary"; break;
        }

        // 2. Normalize Goal String (THE FIX)
        // This ensures that "lose", "Lose", or "lose weight" all become the exact string required by FitnessProfile
        String normalizedGoal = "Maintain Weight"; // Default
        String lowerGoal = rawGoal.toLowerCase();

        if (lowerGoal.contains("lose") || lowerGoal.contains("cut") || lowerGoal.contains("diet")) {
            normalizedGoal = "Lose Weight";
        } else if (lowerGoal.contains("gain") || lowerGoal.contains("muscle") || lowerGoal.contains("bulk")) {
            normalizedGoal = "Build Muscle";
        }

        // 3. Create Profile & Calculate
        FitnessProfile profile = new FitnessProfile(
                userId, gender, age, weight, height, "kg", "cm", activityString, normalizedGoal
        );

        int finalTarget = profile.getDailyCalorieGoal();

        // 4. Update LiveData
        mDailyTarget.setValue(finalTarget);

        // Update Pre-fill Data
        mExistingAge.setValue(String.valueOf(age));
        mExistingWeight.setValue(String.valueOf((int)weight));
        mExistingHeight.setValue(String.valueOf((int)height));
        mExistingGender.setValue(gender);
        mExistingGoal.setValue(normalizedGoal); // Save the CLEAN goal, not the raw one
        mExistingActivity.setValue(String.valueOf(activityLevelInt));

        // 5. Save to Firebase
        db.collection("users").document(userId).set(profile, SetOptions.merge());
    }

    public void addMeal(int protein, int carbs, int fats) {
        int currentCals = mCaloriesEaten.getValue() != null ? mCaloriesEaten.getValue() : 0;
        int currentP = mProteinEaten.getValue() != null ? mProteinEaten.getValue() : 0;
        int currentC = mCarbsEaten.getValue() != null ? mCarbsEaten.getValue() : 0;
        int currentF = mFatsEaten.getValue() != null ? mFatsEaten.getValue() : 0;

        int mealCals = (protein * 4) + (carbs * 4) + (fats * 9);

        int newCals = currentCals + mealCals;
        int newP = currentP + protein;
        int newC = currentC + carbs;
        int newF = currentF + fats;

        mCaloriesEaten.setValue(newCals);
        mProteinEaten.setValue(newP);
        mCarbsEaten.setValue(newC);
        mFatsEaten.setValue(newF);

        Map<String, Object> updates = new HashMap<>();
        updates.put("caloriesEaten", newCals);
        updates.put("proteinEaten", newP);
        updates.put("carbsEaten", newC);
        updates.put("fatsEaten", newF);

        db.collection("users").document(userId).update(updates);
    }

    public void resetDay() {
        mCaloriesEaten.setValue(0);
        mProteinEaten.setValue(0);
        mCarbsEaten.setValue(0);
        mFatsEaten.setValue(0);

        Map<String, Object> updates = new HashMap<>();
        updates.put("caloriesEaten", 0);
        updates.put("proteinEaten", 0);
        updates.put("carbsEaten", 0);
        updates.put("fatsEaten", 0);

        db.collection("users").document(userId).update(updates);
    }
    public LiveData<Integer> getDailyTarget() { return mDailyTarget; }
    public LiveData<Integer> getCaloriesEaten() { return mCaloriesEaten; }
    public LiveData<Integer> getProteinEaten() { return mProteinEaten; }
    public LiveData<Integer> getCarbsEaten() { return mCarbsEaten; }
    public LiveData<Integer> getFatsEaten() { return mFatsEaten; }
    public LiveData<Boolean> getIsLoading() { return mIsLoading; }

    public LiveData<String> getExistingWeight() { return mExistingWeight; }
    public LiveData<String> getExistingAge() { return mExistingAge; }
    public LiveData<String> getExistingHeight() { return mExistingHeight; }
    public LiveData<String> getExistingGender() { return mExistingGender; }
    public LiveData<String> getExistingActivity() { return mExistingActivity; }
    public LiveData<String> getExistingGoal() { return mExistingGoal; }
}