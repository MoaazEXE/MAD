package com.example.bytelift.ui.diet;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.List;

public class RecipeSeeder {

    public static void seed() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // ===== 1 =====
        Recipe r1 = new Recipe();
        r1.name = "Grilled Chicken & Quinoa Bowl";
        r1.calories = 450;
        r1.protein = 45;
        r1.carbs = 35;
        r1.fats = 12;
        r1.ingredients = "Chicken breast (200g)\nQuinoa (1 cup)\nBroccoli\nOlive oil";
        r1.instructions = "Season chicken and grill 6–7 minutes per side. Cook quinoa in boiling water. Serve with broccoli.";
        db.collection("recipes").add(r1);

        // ===== 2 =====
        Recipe r2 = new Recipe();
        r2.name = "Turkey & Brown Rice Meal Prep";
        r2.calories = 480;
        r2.protein = 42;
        r2.carbs = 45;
        r2.fats = 10;
        r2.ingredients = "Lean ground turkey (200g)\nBrown rice\nGreen beans\nGarlic";
        r2.instructions = "Cook turkey until browned. Boil rice. Steam green beans. Combine for meal prep.";
        db.collection("recipes").add(r2);

        // ===== 3 =====
        Recipe r3 = new Recipe();
        r3.name = "Salmon & Sweet Potato Plate";
        r3.calories = 520;
        r3.protein = 38;
        r3.carbs = 40;
        r3.fats = 20;
        r3.ingredients = "Salmon fillet\nSweet potato\nAsparagus\nOlive oil";
        r3.instructions = "Bake salmon at 200°C for 12–15 minutes. Roast sweet potato. Serve with asparagus.";
        db.collection("recipes").add(r3);

        // ===== 4 =====
        Recipe r4 = new Recipe();
        r4.name = "Egg White Omelette";
        r4.calories = 250;
        r4.protein = 30;
        r4.carbs = 5;
        r4.fats = 8;
        r4.ingredients = "Egg whites (6)\nSpinach\nMushrooms\nLow-fat cheese";
        r4.instructions = "Cook vegetables first. Add egg whites and cook until set. Fold with cheese.";
        db.collection("recipes").add(r4);

        // ===== 5 =====
        Recipe r5 = new Recipe();
        r5.name = "Tuna Salad Wrap";
        r5.calories = 380;
        r5.protein = 35;
        r5.carbs = 30;
        r5.fats = 10;
        r5.ingredients = "Canned tuna\nWhole wheat wrap\nLettuce\nLight mayonnaise";
        r5.instructions = "Mix tuna with mayonnaise. Wrap with lettuce in tortilla.";
        db.collection("recipes").add(r5);

        // ===== 6 =====
        Recipe r6 = new Recipe();
        r6.name = "Greek Yogurt Protein Bowl";
        r6.calories = 320;
        r6.protein = 28;
        r6.carbs = 35;
        r6.fats = 6;
        r6.ingredients = "Greek yogurt (0%)\nProtein powder\nMixed berries\nHoney";
        r6.instructions = "Mix yogurt with protein powder. Top with berries and drizzle honey.";
        db.collection("recipes").add(r6);

        // ===== 7 =====
        Recipe r7 = new Recipe();
        r7.name = "Lean Beef Stir Fry";
        r7.calories = 500;
        r7.protein = 40;
        r7.carbs = 35;
        r7.fats = 18;
        r7.ingredients = "Lean beef strips\nBell peppers\nSoy sauce\nGarlic";
        r7.instructions = "Stir fry beef until cooked. Add vegetables and soy sauce. Serve hot.";
        db.collection("recipes").add(r7);

        // ===== 8 =====
        Recipe r8 = new Recipe();
        r8.name = "Chicken Burrito Bowl";
        r8.calories = 510;
        r8.protein = 48;
        r8.carbs = 45;
        r8.fats = 12;
        r8.ingredients = "Chicken breast\nBrown rice\nBlack beans\nSalsa";
        r8.instructions = "Grill chicken. Cook rice. Assemble bowl with beans and salsa.";
        db.collection("recipes").add(r8);

        // ===== 9 =====
        Recipe r9 = new Recipe();
        r9.name = "Shrimp & Garlic Rice";
        r9.calories = 430;
        r9.protein = 36;
        r9.carbs = 40;
        r9.fats = 8;
        r9.ingredients = "Shrimp\nGarlic\nWhite rice\nOlive oil";
        r9.instructions = "Sauté shrimp with garlic. Serve over cooked rice.";
        db.collection("recipes").add(r9);

        // ===== 10 =====
        Recipe r10 = new Recipe();
        r10.name = "Cottage Cheese Pancakes";
        r10.calories = 360;
        r10.protein = 32;
        r10.carbs = 30;
        r10.fats = 8;
        r10.ingredients = "Cottage cheese\nEggs\nOats\nBaking powder";
        r10.instructions = "Blend ingredients. Cook pancakes on non-stick pan until golden.";
        db.collection("recipes").add(r10);

        // ===== 11 =====
        Recipe r11 = new Recipe();
        r11.name = "Chicken Caesar Protein Salad";
        r11.calories = 420;
        r11.protein = 40;
        r11.carbs = 20;
        r11.fats = 15;
        r11.ingredients = "Grilled chicken\nRomaine lettuce\nLight Caesar dressing\nParmesan";
        r11.instructions = "Slice grilled chicken. Toss with lettuce and dressing.";
        db.collection("recipes").add(r11);

        // ===== 12 =====
        Recipe r12 = new Recipe();
        r12.name = "Protein Oatmeal";
        r12.calories = 390;
        r12.protein = 30;
        r12.carbs = 45;
        r12.fats = 9;
        r12.ingredients = "Oats\nProtein powder\nMilk\nBanana";
        r12.instructions = "Cook oats in milk. Stir in protein powder and banana.";
        db.collection("recipes").add(r12);

        // ===== 13 =====
        Recipe r13 = new Recipe();
        r13.name = "Baked Cod & Rice";
        r13.calories = 410;
        r13.protein = 36;
        r13.carbs = 40;
        r13.fats = 6;
        r13.ingredients = "Cod fillet\nWhite rice\nLemon\nSpices";
        r13.instructions = "Bake cod until flaky. Serve with cooked rice.";
        db.collection("recipes").add(r13);

        // ===== 14 =====
        Recipe r14 = new Recipe();
        r14.name = "Chicken Lettuce Wraps";
        r14.calories = 370;
        r14.protein = 38;
        r14.carbs = 18;
        r14.fats = 10;
        r14.ingredients = "Chicken breast\nLettuce leaves\nSoy sauce\nGarlic";
        r14.instructions = "Cook chicken with soy sauce and garlic. Wrap in lettuce leaves.";
        db.collection("recipes").add(r14);

        // ===== 15 =====
        Recipe r15 = new Recipe();
        r15.name = "Protein Smoothie";
        r15.calories = 350;
        r15.protein = 30;
        r15.carbs = 40;
        r15.fats = 5;
        r15.ingredients = "Protein powder\nMilk\nBanana\nPeanut butter";
        r15.instructions = "Blend all ingredients until smooth.";
        db.collection("recipes").add(r15);

        // ===== 16 =====
        Recipe r16 = new Recipe();
        r16.name = "Chicken Teriyaki Rice Bowl";
        r16.calories = 520;
        r16.protein = 42;
        r16.carbs = 50;
        r16.fats = 10;
        r16.ingredients = "Chicken breast\nWhite rice\nTeriyaki sauce\nSesame seeds";
        r16.instructions = "Cook chicken until browned. Add teriyaki sauce. Serve over rice.";
        db.collection("recipes").add(r16);

        // ===== 17 =====
        Recipe r17 = new Recipe();
        r17.name = "Lean Beef Burger (Whole Wheat)";
        r17.calories = 460;
        r17.protein = 42;
        r17.carbs = 30;
        r17.fats = 15;
        r17.ingredients = "Lean beef patty\nWhole wheat bun\nLettuce\nTomato";
        r17.instructions = "Grill beef patty. Assemble burger with vegetables.";
        db.collection("recipes").add(r17);

        // ===== 18 =====
        Recipe r18 = new Recipe();
        r18.name = "Chicken Fried Rice (Light)";
        r18.calories = 480;
        r18.protein = 40;
        r18.carbs = 45;
        r18.fats = 12;
        r18.ingredients = "Chicken breast\nCooked rice\nEgg\nFrozen vegetables";
        r18.instructions = "Cook chicken. Add rice, egg, and vegetables. Stir fry.";
        db.collection("recipes").add(r18);

        // ===== 19 =====
        Recipe r19 = new Recipe();
        r19.name = "Protein French Toast";
        r19.calories = 390;
        r19.protein = 32;
        r19.carbs = 40;
        r19.fats = 7;
        r19.ingredients = "Egg whites\nWhole wheat bread\nMilk\nCinnamon";
        r19.instructions = "Dip bread in egg mixture. Pan-cook until golden.";
        db.collection("recipes").add(r19);

        // ===== 20 =====
        Recipe r20 = new Recipe();
        r20.name = "Turkey Chili";
        r20.calories = 450;
        r20.protein = 45;
        r20.carbs = 35;
        r20.fats = 10;
        r20.ingredients = "Ground turkey\nKidney beans\nTomato sauce\nChili spices";
        r20.instructions = "Simmer turkey with beans and sauce until thick.";
        db.collection("recipes").add(r20);

        // ===== 21 =====
        Recipe r21 = new Recipe();
        r21.name = "Chicken Pasta (High Protein)";
        r21.calories = 520;
        r21.protein = 45;
        r21.carbs = 50;
        r21.fats = 10;
        r21.ingredients = "Chicken breast\nProtein pasta\nTomato sauce";
        r21.instructions = "Cook pasta. Add cooked chicken and sauce.";
        db.collection("recipes").add(r21);

        // ===== 22 =====
        Recipe r22 = new Recipe();
        r22.name = "Egg & Turkey Breakfast Bowl";
        r22.calories = 360;
        r22.protein = 34;
        r22.carbs = 20;
        r22.fats = 12;
        r22.ingredients = "Eggs\nTurkey slices\nSpinach\nOlive oil";
        r22.instructions = "Scramble eggs with turkey and spinach.";
        db.collection("recipes").add(r22);

        // ===== 23 =====
        Recipe r23 = new Recipe();
        r23.name = "High Protein Ramen";
        r23.calories = 480;
        r23.protein = 38;
        r23.carbs = 50;
        r23.fats = 9;
        r23.ingredients = "Chicken breast\nRamen noodles\nEgg\nBroth";
        r23.instructions = "Cook noodles in broth. Add chicken and egg.";
        db.collection("recipes").add(r23);

        // ===== 24 =====
        Recipe r24 = new Recipe();
        r24.name = "Chicken & Lentil Stew";
        r24.calories = 430;
        r24.protein = 40;
        r24.carbs = 35;
        r24.fats = 8;
        r24.ingredients = "Chicken breast\nLentils\nOnion\nSpices";
        r24.instructions = "Simmer lentils. Add chicken and cook until tender.";
        db.collection("recipes").add(r24);

        // ===== 25 =====
        Recipe r25 = new Recipe();
        r25.name = "Protein Burrito Wrap";
        r25.calories = 460;
        r25.protein = 42;
        r25.carbs = 40;
        r25.fats = 10;
        r25.ingredients = "Chicken breast\nWhole wheat wrap\nBlack beans";
        r25.instructions = "Cook chicken. Assemble wrap with beans.";
        db.collection("recipes").add(r25);

        // ===== 26 =====
        Recipe r26 = new Recipe();
        r26.name = "Greek Chicken Plate";
        r26.calories = 500;
        r26.protein = 45;
        r26.carbs = 35;
        r26.fats = 14;
        r26.ingredients = "Chicken breast\nRice\nTzatziki\nCucumber";
        r26.instructions = "Grill chicken. Serve with rice and tzatziki.";
        db.collection("recipes").add(r26);

        // ===== 27 =====
        Recipe r27 = new Recipe();
        r27.name = "Protein Mac & Cheese";
        r27.calories = 520;
        r27.protein = 38;
        r27.carbs = 50;
        r27.fats = 12;
        r27.ingredients = "Protein pasta\nCheese\nMilk";
        r27.instructions = "Cook pasta. Stir in cheese sauce.";
        db.collection("recipes").add(r27);

        // ===== 28 =====
        Recipe r28 = new Recipe();
        r28.name = "Chicken Pho (Light)";
        r28.calories = 430;
        r28.protein = 36;
        r28.carbs = 45;
        r28.fats = 6;
        r28.ingredients = "Chicken breast\nRice noodles\nBroth\nHerbs";
        r28.instructions = "Simmer broth. Add noodles and chicken.";
        db.collection("recipes").add(r28);

        // ===== 29 =====
        Recipe r29 = new Recipe();
        r29.name = "Turkey Meatballs";
        r29.calories = 410;
        r29.protein = 42;
        r29.carbs = 20;
        r29.fats = 12;
        r29.ingredients = "Ground turkey\nEgg\nBreadcrumbs";
        r29.instructions = "Form meatballs. Bake at 180°C for 20 minutes.";
        db.collection("recipes").add(r29);

        // ===== 30 =====
        Recipe r30 = new Recipe();
        r30.name = "Protein Breakfast Wrap";
        r30.calories = 380;
        r30.protein = 32;
        r30.carbs = 30;
        r30.fats = 9;
        r30.ingredients = "Eggs\nTurkey slices\nWhole wheat wrap";
        r30.instructions = "Scramble eggs with turkey. Wrap and serve.";
        db.collection("recipes").add(r30);
        // ===== 31 =====
        Recipe r31 = new Recipe();
        r31.name = "Chicken Spinach Pasta";
        r31.calories = 510;
        r31.protein = 40;
        r31.carbs = 50;
        r31.fats = 10;
        r31.ingredients = "Chicken breast\nPasta\nSpinach\nGarlic";
        r31.instructions = "Cook pasta. Sauté chicken and spinach. Combine together.";
        db.collection("recipes").add(r31);

        // ===== 32 =====
        Recipe r32 = new Recipe();
        r32.name = "Protein Rice & Beans Bowl";
        r32.calories = 420;
        r32.protein = 30;
        r32.carbs = 45;
        r32.fats = 8;
        r32.ingredients = "Brown rice\nBlack beans\nSpices\nOlive oil";
        r32.instructions = "Cook rice and beans separately. Combine with spices.";
        db.collection("recipes").add(r32);

        // ===== 33 =====
        Recipe r33 = new Recipe();
        r33.name = "Chicken Vegetable Soup";
        r33.calories = 390;
        r33.protein = 35;
        r33.carbs = 30;
        r33.fats = 6;
        r33.ingredients = "Chicken breast\nCarrots\nCelery\nBroth";
        r33.instructions = "Simmer all ingredients until chicken is cooked.";
        db.collection("recipes").add(r33);

        // ===== 34 =====
        Recipe r34 = new Recipe();
        r34.name = "BBQ Chicken Plate";
        r34.calories = 480;
        r34.protein = 45;
        r34.carbs = 35;
        r34.fats = 12;
        r34.ingredients = "Chicken breast\nBBQ sauce\nRoasted potatoes";
        r34.instructions = "Bake chicken with BBQ sauce. Serve with potatoes.";
        db.collection("recipes").add(r34);

        // ===== 35 =====
        Recipe r35 = new Recipe();
        r35.name = "Protein Banana Pancakes";
        r35.calories = 390;
        r35.protein = 32;
        r35.carbs = 40;
        r35.fats = 8;
        r35.ingredients = "Banana\nEggs\nProtein powder\nOats";
        r35.instructions = "Blend ingredients and cook pancakes on non-stick pan.";
        db.collection("recipes").add(r35);

        // ===== 36 =====
        Recipe r36 = new Recipe();
        r36.name = "Chicken Teriyaki Noodles";
        r36.calories = 520;
        r36.protein = 42;
        r36.carbs = 50;
        r36.fats = 10;
        r36.ingredients = "Chicken breast\nEgg noodles\nTeriyaki sauce";
        r36.instructions = "Cook noodles. Stir fry chicken and add sauce.";
        db.collection("recipes").add(r36);

        // ===== 37 =====
        Recipe r37 = new Recipe();
        r37.name = "Protein Tuna Rice Bowl";
        r37.calories = 430;
        r37.protein = 38;
        r37.carbs = 40;
        r37.fats = 8;
        r37.ingredients = "Canned tuna\nWhite rice\nCorn\nLight mayo";
        r37.instructions = "Mix tuna with rice and corn. Add light mayo.";
        db.collection("recipes").add(r37);

        // ===== 38 =====
        Recipe r38 = new Recipe();
        r38.name = "High Protein Nachos";
        r38.calories = 500;
        r38.protein = 35;
        r38.carbs = 45;
        r38.fats = 15;
        r38.ingredients = "Chicken breast\nTortilla chips\nCheese\nSalsa";
        r38.instructions = "Bake chips with chicken and cheese until melted.";
        db.collection("recipes").add(r38);

        // ===== 39 =====
        Recipe r39 = new Recipe();
        r39.name = "Protein Breakfast Scramble";
        r39.calories = 360;
        r39.protein = 32;
        r39.carbs = 20;
        r39.fats = 12;
        r39.ingredients = "Eggs\nTurkey slices\nBell peppers";
        r39.instructions = "Scramble eggs with turkey and peppers.";
        db.collection("recipes").add(r39);

        // ===== 40 =====
        Recipe r40 = new Recipe();
        r40.name = "Chicken Soba Noodles";
        r40.calories = 470;
        r40.protein = 38;
        r40.carbs = 50;
        r40.fats = 8;
        r40.ingredients = "Chicken breast\nSoba noodles\nSoy sauce";
        r40.instructions = "Cook noodles. Toss with chicken and soy sauce.";
        db.collection("recipes").add(r40);

        // ===== 41 =====
        Recipe r41 = new Recipe();
        r41.name = "Protein Veggie Burrito Bowl";
        r41.calories = 420;
        r41.protein = 30;
        r41.carbs = 45;
        r41.fats = 9;
        r41.ingredients = "Tofu\nBrown rice\nBlack beans\nVegetables";
        r41.instructions = "Cook tofu and rice. Assemble bowl with beans and veggies.";
        db.collection("recipes").add(r41);

        // ===== 42 =====
        Recipe r42 = new Recipe();
        r42.name = "Honey Garlic Chicken";
        r42.calories = 490;
        r42.protein = 44;
        r42.carbs = 35;
        r42.fats = 12;
        r42.ingredients = "Chicken breast\nHoney\nGarlic\nSoy sauce";
        r42.instructions = "Cook chicken. Add honey garlic sauce and simmer.";
        db.collection("recipes").add(r42);

        // ===== 43 =====
        Recipe r43 = new Recipe();
        r43.name = "Protein Egg Fried Rice";
        r43.calories = 460;
        r43.protein = 34;
        r43.carbs = 45;
        r43.fats = 10;
        r43.ingredients = "Eggs\nRice\nGreen onions\nSoy sauce";
        r43.instructions = "Scramble eggs. Add rice and soy sauce.";
        db.collection("recipes").add(r43);

        // ===== 44 =====
        Recipe r44 = new Recipe();
        r44.name = "Chicken Avocado Salad";
        r44.calories = 430;
        r44.protein = 38;
        r44.carbs = 20;
        r44.fats = 18;
        r44.ingredients = "Chicken breast\nAvocado\nMixed greens\nOlive oil";
        r44.instructions = "Slice chicken and avocado. Toss with greens.";
        db.collection("recipes").add(r44);

        // ===== 45 =====
        Recipe r45 = new Recipe();
        r45.name = "Protein Peanut Butter Smoothie";
        r45.calories = 420;
        r45.protein = 30;
        r45.carbs = 35;
        r45.fats = 14;
        r45.ingredients = "Protein powder\nMilk\nPeanut butter\nBanana";
        r45.instructions = "Blend all ingredients until smooth.";
        db.collection("recipes").add(r45);

        // ===== 46 =====
        Recipe r46 = new Recipe();
        r46.name = "Chicken Curry (Light)";
        r46.calories = 480;
        r46.protein = 42;
        r46.carbs = 40;
        r46.fats = 10;
        r46.ingredients = "Chicken breast\nCurry paste\nRice\nCoconut milk (light)";
        r46.instructions = "Simmer chicken in curry sauce. Serve with rice.";
        db.collection("recipes").add(r46);

        // ===== 47 =====
        Recipe r47 = new Recipe();
        r47.name = "Protein Stuffed Peppers";
        r47.calories = 410;
        r47.protein = 36;
        r47.carbs = 30;
        r47.fats = 10;
        r47.ingredients = "Ground turkey\nBell peppers\nRice\nTomato sauce";
        r47.instructions = "Stuff peppers and bake at 180°C for 25 minutes.";
        db.collection("recipes").add(r47);

        // ===== 48 =====
        Recipe r48 = new Recipe();
        r48.name = "Chicken Fajita Bowl";
        r48.calories = 500;
        r48.protein = 44;
        r48.carbs = 45;
        r48.fats = 12;
        r48.ingredients = "Chicken breast\nBell peppers\nRice\nSpices";
        r48.instructions = "Cook chicken with peppers and spices. Serve with rice.";
        db.collection("recipes").add(r48);

        // ===== 49 =====
        Recipe r49 = new Recipe();
        r49.name = "Protein Lentil Curry";
        r49.calories = 430;
        r49.protein = 30;
        r49.carbs = 40;
        r49.fats = 9;
        r49.ingredients = "Lentils\nTomato sauce\nSpices\nRice";
        r49.instructions = "Simmer lentils in sauce. Serve with rice.";
        db.collection("recipes").add(r49);

        // ===== 50 =====
        Recipe r50 = new Recipe();
        r50.name = "Chicken Mushroom Rice";
        r50.calories = 460;
        r50.protein = 40;
        r50.carbs = 45;
        r50.fats = 9;
        r50.ingredients = "Chicken breast\nMushrooms\nRice\nGarlic";
        r50.instructions = "Cook chicken with mushrooms. Serve over rice.";
        db.collection("recipes").add(r50);
    }
}
