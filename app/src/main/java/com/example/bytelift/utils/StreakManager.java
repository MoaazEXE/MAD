package com.example.bytelift.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Calendar;

public class StreakManager {

    private static final String PREF_NAME = "streak_prefs";
    private static final String KEY_LAST_DAY = "last_day";
    private static final String KEY_STREAK = "streak";

    public static int updateStreak(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int lastDay = prefs.getInt(KEY_LAST_DAY, -1);
        int streak = prefs.getInt(KEY_STREAK, 0);

        int today = getTodayDayIndex();

        if (lastDay == -1) {
            // first time using app
            save(prefs, today, 1);
            return 1;
        }

        if (today == lastDay) {
            // already counted today
            return streak;
        }

        if (today == lastDay + 1) {
            // consecutive day → streak++
            streak++;
        } else {
            // missed a day → reset streak
            streak = 1;
        }

        save(prefs, today, streak);
        return streak;
    }

    private static void save(SharedPreferences prefs, int day, int streak) {
        prefs.edit()
                .putInt(KEY_LAST_DAY, day)
                .putInt(KEY_STREAK, streak)
                .apply();
    }

    private static int getTodayDayIndex() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_YEAR);
    }
}
