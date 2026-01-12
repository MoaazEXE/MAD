package com.example.bytelift.ui.settings_page;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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

    /**
     * WorkManager will call this method when the job runs.
     * Your logic:
     * 1) Read reminder_days and reminder_time from InputData
     * 2) If today is not in reminder_days -> do nothing
     * 3) If time is not close to reminder_time -> do nothing
     * 4) Otherwise send a notification
     */
    @NonNull
    @Override
    public Result doWork() {
        String reminderDays = getInputData().getString("reminder_days");
        String reminderTime = getInputData().getString("reminder_time");

        // If scheduler didn't provide required data, fail so you can debug.
        if (reminderDays == null || reminderTime == null) {
            Log.w("WorkoutReminderWorker", "Missing reminder data");
            return Result.failure();
        }

        // 1) Check if today is one of the reminder days (MON,TUE,...)
        String today = getTodayDayOfWeek();
        if (!reminderDays.contains(today)) {
            Log.d("WorkoutReminderWorker", "Not a reminder day today: " + today);
            return Result.success();
        }

        // 2) Check if current time is near the reminder time (within 30 minutes)
        if (!isReminderTime(reminderTime)) {
            Log.d("WorkoutReminderWorker", "Not reminder time yet");
            return Result.success();
        }

        // 3) Send the notification
        sendWorkoutNotification();
        return Result.success();
    }

    /**
     * Returns "MON", "TUE", ... depending on device date.
     * Used to compare against reminderDays string (ex: "MON,WED,FRI").
     */
    private String getTodayDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY:    return "MON";
            case Calendar.TUESDAY:   return "TUE";
            case Calendar.WEDNESDAY: return "WED";
            case Calendar.THURSDAY:  return "THU";
            case Calendar.FRIDAY:    return "FRI";
            case Calendar.SATURDAY:  return "SAT";
            case Calendar.SUNDAY:    return "SUN";
            default: return "";
        }
    }

    /**
     * Checks if now is within a 30-minute window of the targetTime (HH:mm).
     * Example targetTime: "07:00"
     */
    private boolean isReminderTime(String targetTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);

            // Normalize "now" to HH:mm (so seconds don't affect diff)
            Date currentTime = sdf.parse(sdf.format(new Date()));
            Date target = sdf.parse(targetTime);

            if (currentTime == null || target == null) return false;

            long diffMs = Math.abs(currentTime.getTime() - target.getTime());
            long diffMinutes = diffMs / (60 * 1000);

            return diffMinutes <= 30;
        } catch (Exception e) {
            Log.e("WorkoutReminderWorker", "Error parsing time", e);
            return false;
        }
    }

    /**
     * Builds and posts the notification. Clicking it opens MainActivity.
     */
    private void sendWorkoutNotification() {
        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        // IMPORTANT: You must have this drawable in res/drawable.
                        // If not, build will crash / not compile.
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Time to Work Out! 💪")
                        .setContentText("Don't skip leg day! Your workout is waiting.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Time to hit the gym! Remember: Train Smarter. Lift Harder."));

        NotificationManager nm =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (nm != null) {
            nm.notify(NOTIFICATION_ID, builder.build());
            Log.d("WorkoutReminderWorker", "Workout notification sent");
        }
    }

    /**
     * Android 8+ requires a notification channel, otherwise notifications won't show.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Reminders for your workout schedule");

            NotificationManager nm = getApplicationContext().getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
}
