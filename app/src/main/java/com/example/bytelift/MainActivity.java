package com.example.bytelift;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.bytelift.databinding.ActivityMainBinding;

// CHANGE THIS import to YOUR workout activity:
import com.example.bytelift.ui.workout.activities.WorkoutActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    // keep last selected fragment tab so workout doesn't "stay selected"
    private int lastSelectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) return;

        navController = navHostFragment.getNavController();

        // Setup normal fragment navigation first
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // Intercept bottom nav clicks
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_workout) {
                // open workout as Activity
                startActivity(new Intent(MainActivity.this, WorkoutActivity.class));

                // keep highlight on previous tab (e.g. Home)
                binding.bottomNav.setSelectedItemId(lastSelectedItemId);
                return false; // don't let navController handle it
            }

            // normal fragments
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) lastSelectedItemId = id;
            return handled;
        });

        // Optional: prevent reloading same fragment if user taps same tab
        binding.bottomNav.setOnItemReselectedListener(item -> {
            // do nothing
        });
    }
}
