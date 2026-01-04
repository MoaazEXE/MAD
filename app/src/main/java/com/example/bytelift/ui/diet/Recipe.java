package com.example.bytelift.ui.diet;

public class Recipe {

    public String name;
    public int calories;
    public int protein;
    public int carbs;
    public int fats;
    public String ingredients;
    public String instructions;

    // REQUIRED empty constructor for Firestore
    public Recipe() {
    }

    public Recipe(String name, int calories, int protein, int carbs, int fats, String instructions) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.instructions = instructions;
    }
}