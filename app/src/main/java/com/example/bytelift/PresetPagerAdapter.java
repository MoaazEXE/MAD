package com.example.bytelift;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PresetPagerAdapter extends FragmentStateAdapter {
    public PresetPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String category;
        switch (position) {
            case 0: category = "beginner"; break;
            case 1: category = "intermediate"; break;
            default: category = "expert"; break;
        }
        return PresetCategoryFragment.newInstance(category);
    }

    @Override
    public int getItemCount() { return 3; }
}