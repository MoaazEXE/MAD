package com.example.bytelift.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app. Fragment;
import androidx.lifecycle. ViewModelProvider;

import com. example.bytelift.R;
import com.example.bytelift.databinding.FragmentHomeBinding;
import com.example. bytelift.utils. StreakManager;
import com.example.bytelift.ui.workout.activities.WorkoutActivity;  // ✅ Your main workout screen
import com.example.bytelift.ui.workout.models.Routine;  // ✅ Correct path
import com.example.bytelift.ui.workout.models.Exercise;  // ✅ Correct path
import com.example.bytelift.ui.workout.utils.DataManager;  // ✅ Correct path

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ✅ Initialize DataManager with context
        DataManager.init(requireContext());

        // Default text (from ViewModel template)
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // 🎯 Motivation quote
        String[] quotes = getResources().getStringArray(R.array.motivation_quotes);
        String randomQuote = quotes[new Random().nextInt(quotes.length)];
        TextView tvMotivation = root.findViewById(R.id.tvMotivation);
        tvMotivation.setText(randomQuote);

        // 🔥 Streak
        StreakManager.init(requireContext());
        int weekStreak = StreakManager.getCurrentStreak(requireContext());
        String weekProgress = StreakManager. getWeekProgress(requireContext());
        TextView streakView = root.findViewById(R.id.tvStreak);
        streakView.setText("🔥 Streak: " + weekStreak + " weeks\n📊 This week: " + weekProgress);

        // 💪 Today's workout "challenge"
        setupTodayWorkoutChallenge(root);

        return root;
    }

    private void setupTodayWorkoutChallenge(View root) {
        TextView tvChallenge = root.findViewById(R.id.tvChallenge);
        Button btnOpenWorkout = root.findViewById(R.id.btnOpenWorkout);

        if (tvChallenge == null || btnOpenWorkout == null) return;

        // ✅ Load routines using DataManager (no parameter needed)
        List<Routine> routines = DataManager.getRoutines();

        // No routines at all
        if (routines == null || routines.isEmpty()) {
            tvChallenge.setText("No workout routines yet.\nCreate one in the Workout tab!");
            btnOpenWorkout. setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), WorkoutActivity.class)));
            return;
        }

        // Get today's day name
        String todayName = getTodayName();

        // Find a routine that has today scheduled
        Routine todayRoutine = null;
        for (Routine routine : routines) {
            // ✅ Check if routine. days contains today
            if (routine.days != null && routine.days.containsKey(todayName)) {
                todayRoutine = routine;
                break;
            }
        }

        // No routine for today
        if (todayRoutine == null) {
            tvChallenge.setText("No workout scheduled for " + todayName + ".\nEnjoy your rest day!");
            btnOpenWorkout.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), WorkoutActivity.class)));
            return;
        }

        // ✅ Get today's exercises from the days Map
        List<Exercise> exercises = todayRoutine.days.get(todayName);

        if (exercises == null || exercises.isEmpty()) {
            tvChallenge.setText("No exercises scheduled for " + todayName + ".\nEnjoy your rest day!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Today's workout (").append(todayName).append("):\n");
            sb.append("Routine: ").append(todayRoutine.name).append("\n\n");

            int limit = Math.min(exercises.size(), 4);
            for (int i = 0; i < limit; i++) {
                Exercise ex = exercises.get(i);
                String name = (ex != null && ex.name != null) ? ex.name : "Exercise";
                String sets = (ex != null) ? String.valueOf(ex.sets) : "0";
                String reps = (ex != null) ? String.valueOf(ex.reps) : "0";

                sb.append("• ").append(name);
                sb.append(" - ").append(sets).append(" sets × ").append(reps).append(" reps\n");
            }

            if (exercises.size() > limit) {
                sb.append("\n... and ").append(exercises.size() - limit).append(" more exercises");
            }

            tvChallenge.setText(sb.toString());
        }

        // ✅ Button → open WorkoutActivity
        btnOpenWorkout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), WorkoutActivity.class)));
    }

    private String getTodayName() {
        Calendar cal = Calendar.getInstance();
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:  return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY:  return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY:  return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            case Calendar.SUNDAY:  return "Sunday";
        }
        return "Today";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}