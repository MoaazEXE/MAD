package com.example.bytelift.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bytelift.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load user profile data
        loadUserProfile();

        // Set up click listeners
        setupClickListeners();

        return root;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch user profile from Firestore
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // Get data from Firestore
                            String username = document.getString("username");
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String email = document.getString("email");

                            // Display username
                            if (username != null) {
                                binding.tvUsername.setText(username);
                            }

                            // Display full name in title (or you can create a separate TextView)
                            if (firstName != null && lastName != null) {
                                String fullName = firstName + " " + lastName;
                                Log.d(TAG, "Loaded profile: " + fullName + " (@" + username + ")");
                            }

                            // TODO: Later we'll fetch these from other collections
                            // For now, keep dummy data for:
                            // - Calories (will come from calorieTracker collection)
                            // - Workout Plan (will come from workoutStats collection)
                            // - Streak (will come from workoutStats collection)

                            setDummyData();

                        } else {
                            Log.d(TAG, "No profile document found");
                            Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                            setDummyData();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading profile", e);
                        Toast.makeText(getContext(), "Failed to load profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        setDummyData();
                    });
        } else {
            Log.d(TAG, "No user logged in");
            setDummyData();
        }
    }

    private void setDummyData() {
        // Keep existing dummy data for features not yet implemented
        // These will be replaced when Diet, Workout, etc. are connected

        // Calories - will be from Diet page later
        binding.tvCalories.setText("1300");

        // Workout Plan - will be from Workout page later
        binding.tvPlans.setText("PPL-UP");

        // Streak - will be from Home/Dashboard later
        binding.tvStreak.setText("7 days");

        // Title/Rank - will be from Leaderboard later
        binding.tvTitle.setText("TITLE LEADERBOARD");
    }

    private void setupClickListeners() {
        // Feedback button
        binding.btnFeedback.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Feedback feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Edit/Filter button (top right)
        binding.btnEdit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit profile coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Calories card click
        binding.caloriesCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigate to Diet page", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to DietFragment
        });

        // Workout Plans card click
        binding.plansCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigate to Workout page", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to WorkoutFragment
        });

        // Streak card click
        binding.streakCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigate to Home/Dashboard", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to HomeFragment
        });

        // History filter button
        binding.btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Share buttons for history items
        binding.btnShare1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Share workout coming soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement share functionality
        });

        binding.btnShare2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Share workout coming soon!", Toast.LENGTH_SHORT).show();
            // TODO: Implement share functionality
        });

        // FAB - Add workout
        binding.fabAddWorkout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add workout coming soon!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add workout screen
        });

        // History items click
        binding.historyItem1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View workout details coming soon!", Toast.LENGTH_SHORT).show();
        });

        binding.historyItem2.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View workout details coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}