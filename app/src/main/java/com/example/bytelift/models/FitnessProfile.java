package com.example.bytelift.models;

import com.google.firebase.Timestamp;

public class FitnessProfile {
    private String userId;
    private String gender;           // "Male", "Female", "Other"
    private int age;                 // Years
    private double weight;           // kg or lbs
    private double height;           // cm or inches
    private String weightUnit;       // "kg" or "lbs"
    private String heightUnit;       // "cm" or "ft"
    private String activityLevel;    // "Sedentary", "Lightly Active", etc.
    private String fitnessGoal;      // "Lose Weight", "Build Muscle", etc.
    private int dailyCalorieGoal;    // Calculated TDEE
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Empty constructor for Firestore
    public FitnessProfile() {
    }

    // Constructor
    public FitnessProfile(String userId, String gender, int age, double weight, double height,
                          String weightUnit, String heightUnit, String activityLevel, String fitnessGoal) {
        this.userId = userId;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.weightUnit = weightUnit;
        this.heightUnit = heightUnit;
        this.activityLevel = activityLevel;
        this.fitnessGoal = fitnessGoal;
        this.dailyCalorieGoal = calculateTDEE();
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Calculate TDEE (Total Daily Energy Expenditure)
    private int calculateTDEE() {
        // Convert to metric if needed
        double weightKg = weightUnit.equals("kg") ? weight : weight * 0.453592;
        double heightCm = heightUnit.equals("cm") ? height : height * 2.54;

        // Mifflin-St Jeor Equation
        double bmr;
        if (gender.equals("Male")) {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }

        // Activity multiplier
        double activityMultiplier = getActivityMultiplier();
        double tdee = bmr * activityMultiplier;

        // Adjust based on goal
        if (fitnessGoal.equals("Lose Weight")) {
            tdee -= 500; // 500 calorie deficit for ~1lb/week loss
        } else if (fitnessGoal.equals("Build Muscle")) {
            tdee += 300; // 300 calorie surplus for muscle gain
        }

        return (int) tdee;
    }

    private double getActivityMultiplier() {
        switch (activityLevel) {
            case "Sedentary":
                return 1.2;
            case "Lightly Active":
                return 1.375;
            case "Moderately Active":
                return 1.55;
            case "Very Active":
                return 1.725;
            case "Extremely Active":
                return 1.9;
            default:
                return 1.2;
        }
    }

    // Calculate BMI
    public double calculateBMI() {
        double weightKg = weightUnit.equals("kg") ? weight : weight * 0.453592;
        double heightM = heightUnit.equals("cm") ? height / 100 : (height * 2.54) / 100;
        return weightKg / (heightM * heightM);
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGender(String gender) {
        this.gender = gender;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setAge(int age) {
        this.age = age;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setWeight(double weight) {
        this.weight = weight;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setHeight(double height) {
        this.height = height;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
        this.updatedAt = Timestamp.now();
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
        this.updatedAt = Timestamp.now();
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
        this.dailyCalorieGoal = calculateTDEE();
        this.updatedAt = Timestamp.now();
    }

    public void setDailyCalorieGoal(int dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.updatedAt = Timestamp.now();
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to get formatted weight
    public String getFormattedWeight() {
        return String.format("%.1f %s", weight, weightUnit);
    }

    // Helper method to get formatted height
    public String getFormattedHeight() {
        if (heightUnit.equals("cm")) {
            return String.format("%.0f cm", height);
        } else {
            int feet = (int) (height / 12);
            int inches = (int) (height % 12);
            return String.format("%d'%d\"", feet, inches);
        }
    }
}