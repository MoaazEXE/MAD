package com.example.bytelift.ui.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bytelift.R;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LH> {

    public static class LeaderboardEntry {
        public String userId;
        public String name;
        public long streak;

        public LeaderboardEntry() {
        }

        public LeaderboardEntry(String userId, String name, long streak) {
            this.userId = userId;
            this.name = name;
            this.streak = streak;
        }
    }

    private final List<LeaderboardEntry> items = new ArrayList<>();

    public void setItems(List<LeaderboardEntry> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard_row, parent, false);
        return new LH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LH holder, int position) {
        LeaderboardEntry e = items.get(position);
        holder.tvRank.setText("#" + (position + 1));
        holder.tvName.setText(e.name != null && !e.name.isEmpty() ? e.name : "Anonymous");
        holder.tvStreak.setText(e.streak + " days");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LH extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvName;
        TextView tvStreak;

        LH(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvStreak = itemView.findViewById(R.id.tvStreak);
        }
    }
}
