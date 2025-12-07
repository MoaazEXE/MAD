package com.example.bytelift.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bytelift.R;
import com.example.bytelift.databinding.FragmentHomeBinding;
import com.example.bytelift.utils.StreakManager;
import com.example.bytelift.ui.workout.RoutineStorage;
import com.example.bytelift.ui.workout.WorkoutRoutineActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Default text (from ViewModel template)
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // 🎯 Motivation quote
        String[] quotes = getResources().getStringArray(R.array.motivation_quotes);
        String randomQuote = quotes[new Random().nextInt(quotes.length)];
        TextView tvMotivation = root.findViewById(R.id.tvMotivation);
        tvMotivation.setText(randomQuote);

        // 🔥 Streak
        int streak = StreakManager.updateStreak(requireContext());
        TextView streakView = root.findViewById(R.id.tvStreak);
        streakView.setText("🔥 Streak: " + streak + " days");

        // 💪 Today’s workout “challenge”
        setupTodayWorkoutChallenge(root);

        return root;
    }

    private void setupTodayWorkoutChallenge(View root) {
        TextView tvChallenge = root.findViewById(R.id.tvChallenge);
        Button btnOpenWorkout = root.findViewById(R.id.btnOpenWorkout);

        if (tvChallenge == null || btnOpenWorkout == null) return;

        // Load saved routines
        List<WorkoutRoutineActivity.WorkoutRoutine> routines =
                RoutineStorage.loadRoutines(requireContext());

        // No routines at all
        if (routines == null || routines.isEmpty()) {
            tvChallenge.setText("No workout routines yet.\nCreate one in the Workout tab!");
            btnOpenWorkout.setOnClickListener(v ->
                    startActivity(new Intent(v.getContext(), WorkoutRoutineActivity.class)));
            return;
        }

        // For now: just use the first routine
        WorkoutRoutineActivity.WorkoutRoutine routine = routines.get(0);
        Map<String, List<WorkoutRoutineActivity.SelectedExercise>> dayMap = routine.dayToExercises;

        if (dayMap == null || dayMap.isEmpty()) {
            tvChallenge.setText("No workout days configured.\nEdit your routine in the Workout tab.");
            btnOpenWorkout.setOnClickListener(v ->
                    startActivity(new Intent(v.getContext(), WorkoutRoutineActivity.class)));
            return;
        }

        String todayName = getTodayName();
        List<WorkoutRoutineActivity.SelectedExercise> todayList = dayMap.get(todayName);

        if (todayList == null || todayList.isEmpty()) {
            tvChallenge.setText("No workout scheduled for " + todayName + ".\nEnjoy your rest day!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Today's workout (").append(todayName).append("):\n");

            int limit = Math.min(todayList.size(), 4);
            for (int i = 0; i < limit; i++) {
                WorkoutRoutineActivity.SelectedExercise ex = todayList.get(i);
                String name = (ex != null && ex.name != null) ? ex.name : "Exercise";
                sb.append("• ").append(name).append("\n");
            }

            if (todayList.size() > limit) {
                sb.append("...and ").append(todayList.size() - limit).append(" more");
            }

            tvChallenge.setText(sb.toString());
        }

        // Button → open full Workout screen
        btnOpenWorkout.setOnClickListener(v ->
                startActivity(new Intent(v.getContext(), WorkoutRoutineActivity.class)));
    }

    private String getTodayName() {
        Calendar cal = Calendar.getInstance();
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            case Calendar.SUNDAY: return "Sunday";
        }
        return "Today";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
