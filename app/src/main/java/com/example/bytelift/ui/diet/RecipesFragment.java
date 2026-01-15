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
import com.example.bytelift.ui.diet.Recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;

public class RecipesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load our hardcoded data
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecipesAdapter adapter = new RecipesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        db.collection("recipes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Recipe> recipes = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        if (recipe != null) {

                            // 🔢 Recalculate calories from macros
                            int protein = Math.max(recipe.protein, 0);
                            int carbs   = Math.max(recipe.carbs, 0);
                            int fats    = Math.max(recipe.fats, 0);

                            int calculatedCalories =
                                    (protein * 4) +
                                            (carbs * 4) +
                                            (fats * 9);

                            recipe.calories = calculatedCalories;

                            recipes.add(recipe);
                        }
                    }

                    adapter.updateData(recipes);
                })
                .addOnFailureListener(e -> {
                    Log.e("RecipesFragment", "Failed to load recipes", e);
                });
        return root;
    }
    // --- INTERNAL MODEL CLASS ---

    // --- INTERNAL ADAPTER CLASS ---
    static class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

        private final List<Recipe> recipes;

        public RecipesAdapter(List<Recipe> recipes) {
            this.recipes = recipes;
        }

        public void updateData(List<Recipe> newRecipes) {
            recipes.clear();
            recipes.addAll(newRecipes);
            notifyDataSetChanged();
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
            holder.tvCalories.setText(recipe.calories + " kcal (Cal)");
            holder.tvMacros.setText("Protein: " + recipe.protein + "g • Carbs: " + recipe.carbs + "g • Fat: " + recipe.fats + "g");
            holder.tvIngredients.setText("Tap to view recipe");

            holder.itemView.setOnClickListener(v -> {

                String message =
                        "Calories: " + recipe.calories + " kcal\n\n" +
                                "Macros:\n" +
                                "• Protein: " + recipe.protein + " g\n" +
                                "• Carbs: " + recipe.carbs + " g\n" +
                                "• Fat: " + recipe.fats + " g\n\n" +
                                "Ingredients:\n" +
                                (recipe.ingredients != null ? recipe.ingredients : "Not available") +
                                "\n\nInstructions:\n" +
                                (recipe.instructions != null ? recipe.instructions : "Not available");

                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                        .setTitle(recipe.name)
                        .setMessage(message)
                        .setPositiveButton("Close", null)
                        .show();
            });
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