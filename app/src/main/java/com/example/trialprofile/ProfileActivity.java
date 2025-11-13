package com.example.trialprofile;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class ProfileActivity extends MainActivity{

    private LinearLayout btnFeedback;
    private ImageView btnEdit;
    private ImageView profileImage;
    private ProgressBar progressRing;
    private TextView tvUsername, tvTitle, tvCalories,tvPlans,tvStreak;

    //Stats cards
    private CardView caloriesCard, plansCard, streakCard;
    private ImageView iconCalories, iconPlans, iconStreak;

    // History Items
    private CardView historyItem1, historyItem2;
    private ImageView btnShare1, btnShare2;

    // FAB
    private FloatingActionButton fabAddWorkout;

    // Filter Button
    private ImageView btnFilter;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setProfileData();
        setClickListeners();
        startAnimations();
}

private void initializeViews() {
    // Header
    btnFeedback = findViewById(R.id.btnFeedback);
    btnEdit = findViewById(R.id.btnEdit);
    profileImage = findViewById(R.id.profileImage);
    progressRing = findViewById(R.id.progressRing);
    tvUsername = findViewById(R.id.tvUsername);
    tvTitle = findViewById(R.id.tvTitle);

    // Stats
    caloriesCard = findViewById(R.id.caloriesCard);
    plansCard = findViewById(R.id.plansCard);
    streakCard = findViewById(R.id.streakCard);

    tvCalories = findViewById(R.id.tvCalories);
    tvPlans = findViewById(R.id.tvPlans);
    tvStreak = findViewById(R.id.tvStreak);

    iconCalories = findViewById(R.id.iconCalories);
    iconPlans = findViewById(R.id.iconPlans);
    iconStreak = findViewById(R.id.iconStreak);

    // History
    historyItem1 = findViewById(R.id.historyItem1);
    historyItem2 = findViewById(R.id.historyItem2);
    btnShare1 = findViewById(R.id.btnShare1);
    btnShare2 = findViewById(R.id.btnShare2);
    btnFilter = findViewById(R.id.btnFilter);

    // FAB
    fabAddWorkout = findViewById(R.id.fabAddWorkout);

    }

    private void setProfileData() {
    tvUsername.setText("Nigger");
    tvTitle.setText("Title Leaderboard");
        tvCalories.setText("1300");
        tvPlans.setText("PPL-UP");
        tvStreak.setText("7 days");

        // Set progress ring to 75%
        progressRing.setProgress(75);
    }

    private void setClickListeners() {
        // Header Actions
        btnFeedback.setOnClickListener(v -> handleFeedback());
        btnEdit.setOnClickListener(v -> handleEdit());
        profileImage.setOnClickListener(v -> handleChangeProfilePicture());

        // Stats Cards with Scale Animation
        caloriesCard.setOnClickListener(v -> {
            animateCardClick(caloriesCard);
            showCaloriesDetail();
        });

        plansCard.setOnClickListener(v -> {
            animateCardClick(plansCard);
            showPlansDetail();
        });

        streakCard.setOnClickListener(v -> {
            animateCardClick(streakCard);
            animateStreakIcon();
            showStreakDetail();
        });

        // History Items
        historyItem1.setOnClickListener(v -> showWorkoutDetail("PUSH"));
        historyItem2.setOnClickListener(v -> showWorkoutDetail("PULL"));

        btnShare1.setOnClickListener(v -> shareWorkout("PUSH"));
        btnShare2.setOnClickListener(v -> shareWorkout("PULL"));

        btnFilter.setOnClickListener(v -> showFilterDialog());

//        // Bottom Navigation
//        btnWorkout.setOnClickListener(v -> navigateTo("Workout"));
//        btnHome.setOnClickListener(v -> navigateTo("Home"));
//        btnProfile.setOnClickListener(v ->
//                Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show()
//        );

        // FAB
        fabAddWorkout.setOnClickListener(v -> {
            animateFAB();
            addNewWorkout();
        });
    }

    private void startAnimations() {
        // Animate progress ring on load
        new Handler().postDelayed(() -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressRing, "progress", 0, 75);
            animator.setDuration(1500);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }, 300);

        // Animate streak icon (pulse effect)
        pulseAnimation(iconStreak);
    }

    private void animateCardClick(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f, 1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(scaleAnimation);
    }

    private void animateStreakIcon() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 1.3f, 1.0f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(300);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        iconStreak.startAnimation(scaleAnimation);
    }

    private void pulseAnimation(View view) {
        ScaleAnimation pulseAnimation = new ScaleAnimation(
                1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulseAnimation.setDuration(1000);
        pulseAnimation.setRepeatCount(Animation.INFINITE);
        pulseAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(pulseAnimation);
    }

    private void animateFAB() {
        fabAddWorkout.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() ->
                        fabAddWorkout.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start()
                ).start();
    }

    private void handleFeedback() {
        Toast.makeText(this, "Opening Feedback Form", Toast.LENGTH_SHORT).show();
        // TODO: Implement feedback dialog or activity
    }

    private void handleEdit() {
        Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to EditProfileActivity
    }

    private void handleChangeProfilePicture() {
        Toast.makeText(this, "Change Profile Picture", Toast.LENGTH_SHORT).show();
        // TODO: Open image picker
    }

    private void showCaloriesDetail() {
        Toast.makeText(this, "Calories: 1300 / 2000", Toast.LENGTH_SHORT).show();
        // TODO: Show detailed calories breakdown dialog
    }

    private void showPlansDetail() {
        Toast.makeText(this, "Current Plan: PPL-UP", Toast.LENGTH_SHORT).show();
        // TODO: Show workout plan details
    }

    private void showStreakDetail() {
        Toast.makeText(this, "Current Streak: 7 days | Best: 14 days", Toast.LENGTH_SHORT).show();
        // TODO: Show streak history and calendar
    }

    private void showWorkoutDetail(String workoutType) {
        Toast.makeText(this, workoutType + " Workout Details", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to WorkoutDetailActivity
    }

    private void shareWorkout(String workoutType) {
        Toast.makeText(this, "Sharing " + workoutType + " workout", Toast.LENGTH_SHORT).show();
        // TODO: Implement share functionality
        /*
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
            "Just completed a " + workoutType + " workout with TrainWise!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
        */
    }

    private void showFilterDialog() {
        Toast.makeText(this, "Filter History", Toast.LENGTH_SHORT).show();
        // TODO: Show filter options (Date, Type, Duration)
    }

    private void addNewWorkout() {
        Toast.makeText(this, "Add New Workout", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to AddWorkoutActivity
    }

    private void navigateTo(String destination) {
        Toast.makeText(this, "Navigate to " + destination, Toast.LENGTH_SHORT).show();
        // TODO: Implement navigation
    }
}



