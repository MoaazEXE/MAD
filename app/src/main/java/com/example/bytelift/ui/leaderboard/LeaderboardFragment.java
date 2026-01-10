package com.example.bytelift.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bytelift.databinding.FragmentLeaderboardBinding;
import com.example.bytelift.utils.StreakManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private LeaderboardAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference leaderboardRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        leaderboardRef = db.collection("leaderboard");

        adapter = new LeaderboardAdapter();
        binding.recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerLeaderboard.setAdapter(adapter);

        binding.tvTitleLeaderboard.setText("Leaderboard");

        // 1) update this user's score in Firestore
        updateMyScore();

        // 2) then load leaderboard
        loadLeaderboard();

        return root;
    }

    private void updateMyScore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        String uid = user.getUid();
        String name = user.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            name = user.getEmail();
        }
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);

        data.put("updatedAt", System.currentTimeMillis());

        leaderboardRef.document(uid)
                .set(data)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update score", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadLeaderboard() {
        binding.progressLeaderboard.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        leaderboardRef
                .orderBy("streak", Query.Direction.DESCENDING)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(snap -> {
                    binding.progressLeaderboard.setVisibility(View.GONE);

                    List<LeaderboardAdapter.LeaderboardEntry> list = new ArrayList<>();
                    snap.getDocuments().forEach(doc -> {
                        String uid = doc.getId();
                        String name = doc.getString("name");
                        Long streakLong = doc.getLong("streak");
                        long streak = streakLong != null ? streakLong : 0L;
                        list.add(new LeaderboardAdapter.LeaderboardEntry(uid, name, streak));
                    });

                    if (list.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmpty.setVisibility(View.GONE);
                    }

                    adapter.setItems(list);
                })
                .addOnFailureListener(e -> {
                    binding.progressLeaderboard.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
