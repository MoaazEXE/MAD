package com.example.bytelift.ui. settings_page;

import android. app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx. core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.bytelift.MainActivity;
import com.example.bytelift.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WorkoutReminderWorker extends Worker {

    private static final String CHANNEL_ID = "workout_reminders";
    private static final String CHANNEL_NAME = "Workout Reminders";
    private static final int NOTIFICATION_ID = 1001;

    public WorkoutReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String reminderDays = getInputData().getString("reminder_days");
        String reminderTime = getInputData().getString("reminder_time");

        if (reminderDays == null || reminderTime == null) {
            Log.w("WorkoutReminderWorker", "Missing reminder data");
            return Result.failure();
        }

        // Check if today is one of the reminder days
        String today = getTodayDayOfWeek();
        if (!reminderDays.contains(today)) {
            Log.d("WorkoutReminderWorker", "Not a reminder day today:  " + today);
            return Result.success();
        }

        // Check if current time matches reminder time (within 30 min window)
        if (! isReminderTime(reminderTime)) {
            Log.d("WorkoutReminderWorker", "Not reminder time yet");
            return Result.success();
        }

        // Send notification
        sendWorkoutNotification();

        return Result.success();
    }

    private String getTodayDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY:  return "MON";
            case Calendar. TUESDAY: return "TUE";
            case Calendar.WEDNESDAY:  return "WED";
            case Calendar.THURSDAY: return "THU";
            case Calendar. FRIDAY: return "FRI";
            case Calendar.SATURDAY: return "SAT";
            case Calendar.SUNDAY: return "SUN";
            default: return "";
        }
    }

    private boolean isReminderTime(String targetTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            Date currentTime = sdf.parse(sdf.format(new Date()));
            Date target = sdf.parse(targetTime);

            if (currentTime == null || target == null) return false;

            // Check if within 30-minute window
            long diff = Math.abs(currentTime.getTime() - target.getTime());
            long diffMinutes = diff / (60 * 1000);

            return diffMinutes <= 30;
        } catch (Exception e) {
            Log.e("WorkoutReminderWorker", "Error parsing time", e);
            return false;
        }
    }

    private void sendWorkoutNotification() {
        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // You'll need to create this
                .setContentTitle("Time to Work Out!  💪")
                .setContentText("Don't skip leg day!  Your workout is waiting.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Time to hit the gym! Remember:  Train Smarter.  Lift Harder."));

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder. build());
            Log.d("WorkoutReminderWorker", "Workout notification sent");
        }
    }

    private void createNotificationChannel() {
        if (Build. VERSION.SDK_INT >= Build. VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager. IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for your workout schedule");

            NotificationManager notificationManager =
                    getApplicationContext().getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager. createNotificationChannel(channel);
            }
        }
    }
}