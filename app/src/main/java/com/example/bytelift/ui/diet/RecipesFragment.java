package com.example.bytelift.ui.diet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load our hardcoded data
        List<Recipe> recipeList = getMockRecipes();
        RecipesAdapter adapter = new RecipesAdapter(recipeList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    // --- HARDCODED DATA HERE ---
    private List<Recipe> getMockRecipes() {
        List<Recipe> list = new ArrayList<>();

        list.add(new Recipe("Grilled Chicken & Quinoa", 450, 45, 35, 12,
                "200g Chicken Breast, 1 cup Quinoa, Mixed Veggies. Grill chicken with lemon pepper."));

        list.add(new Recipe("Greek Yogurt Power Bowl", 320, 25, 40, 8,
                "1 cup Greek Yogurt (0%), 1 scoop Whey Protein, Berries, 1 tbsp Honey. Mix and enjoy."));

        list.add(new Recipe("Tuna Salad Wrap", 380, 35, 25, 10,
                "1 can Tuna, 1 tbsp Light Mayo, Lettuce, Whole Wheat Wrap. Mix tuna and mayo, wrap tight."));

        list.add(new Recipe("Egg White Omelette", 250, 30, 5, 8,
                "6 Egg Whites, Spinach, Mushrooms, 1 slice Low Fat Cheese. Cook in non-stick pan."));

        list.add(new Recipe("Beef Stir Fry", 500, 50, 40, 18,
                "150g Lean Beef Strips, Broccoli, Soy Sauce, Rice Noodles. Stir fry beef first, then veggies."));

        return list;
    }

    // --- INTERNAL MODEL CLASS ---
    static class Recipe {
        String name;
        int calories;
        int protein;
        int carbs;
        int fats;
        String instructions;

        public Recipe(String name, int calories, int protein, int carbs, int fats, String instructions) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
            this.instructions = instructions;
        }
    }

    // --- INTERNAL ADAPTER CLASS ---
    static class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

        private final List<Recipe> recipes;

        public RecipesAdapter(List<Recipe> recipes) {
            this.recipes = recipes;
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe recipe = recipes.get(position);
            holder.tvName.setText(recipe.name);
            holder.tvCalories.setText(recipe.calories + " kcal");
            holder.tvMacros.setText("Protein: " + recipe.protein + "g • Carbs: " + recipe.carbs + "g • Fat: " + recipe.fats + "g");
            holder.tvIngredients.setText(recipe.instructions);
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }

        static class RecipeViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvCalories, tvMacros, tvIngredients;

            public RecipeViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvRecipeName);
                tvCalories = itemView.findViewById(R.id.tvCaloriesBadge);
                tvMacros = itemView.findViewById(R.id.tvMacros);
                tvIngredients = itemView.findViewById(R.id.tvIngredients);
            }
        }
    }
}