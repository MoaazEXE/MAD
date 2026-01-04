package com.example.bytelift. ui.settings_page;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bytelift.data.settings.SettingsRepository;
import com.example.bytelift.data.settings.SettingsState;
import com.example.bytelift.models.FitnessProfile;
import com.example.bytelift.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com. google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java. io.IOException;
import java. text.SimpleDateFormat;
import java.util. Date;
import java.util.HashMap;
import java.util. Locale;
import java.util. Map;

public class SettingsViewModel extends AndroidViewModel {
    private final SettingsRepository settingsRepository;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<OperationResult> operationResult;

    public static class OperationResult {
        public enum Status { SUCCESS, ERROR, LOADING }

        private final Status status;
        private final String message;

        public OperationResult(Status status, String message) {
            this.status = status;
            this.message = message;
        }

        public Status getStatus() { return status; }
        public String getMessage() { return message; }
    }

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = SettingsRepository.getInstance(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        operationResult = new MutableLiveData<>();
    }

    public LiveData<SettingsState> getSettings() {
        return settingsRepository.getSettingsLiveData();
    }

    public LiveData<OperationResult> getOperationResult() {
        return operationResult;
    }

    // Theme
    public void setTheme(SettingsState.Theme theme) {
        settingsRepository.setTheme(theme);
        logAnalyticsEvent("settings_change", "theme", theme.name());
    }

    // Units
    public void setWeightUnit(SettingsState.WeightUnit unit) {
        settingsRepository.setWeightUnit(unit);
        logAnalyticsEvent("settings_change", "weight_unit", unit.name());
    }

    public void setHeightUnit(SettingsState.HeightUnit unit) {
        settingsRepository.setHeightUnit(unit);
        logAnalyticsEvent("settings_change", "height_unit", unit.name());
    }

    // Notifications
    public void setNotificationsEnabled(boolean enabled) {
        settingsRepository.setNotificationsEnabled(enabled);
        if (! enabled) {
            // Cancel all scheduled notifications
            cancelAllNotifications();
        }
        logAnalyticsEvent("settings_change", "notifications_enabled", String.valueOf(enabled));
    }

    public void setWorkoutReminders(boolean enabled) {
        settingsRepository.setWorkoutReminders(enabled);
        if (enabled && settingsRepository.getSettings().isNotificationsEnabled()) {
            scheduleWorkoutReminders();
        } else {
            cancelWorkoutReminders();
        }
        logAnalyticsEvent("settings_change", "workout_reminders", String.valueOf(enabled));
    }

    public void setReminderSchedule(String days, String time) {
        settingsRepository.setReminderSchedule(days, time);
        if (settingsRepository.getSettings().isNotificationsEnabled() &&
                settingsRepository.getSettings().isWorkoutReminders()) {
            scheduleWorkoutReminders();
        }
    }

    // Logout
    public void logout() {
        firebaseAuth.signOut();
        settingsRepository.clearAllSettings();
        logAnalyticsEvent("logout", null, null);
    }

    // Delete Account
    public void deleteAccount() {
        operationResult.setValue(new OperationResult(OperationResult. Status.LOADING, "Deleting account..."));
        logAnalyticsEvent("delete_account_attempt", null, null);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            operationResult.setValue(new OperationResult(OperationResult.Status. ERROR, "User not authenticated"));
            return;
        }

        String userId = user.getUid();

        // Delete Firestore documents
        firestore. collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid1 -> {
                    firestore.collection("fitnessProfiles").document(userId).delete()
                            .addOnSuccessListener(aVoid2 -> {
                                // Delete Firebase Auth user
                                user.delete()
                                        .addOnSuccessListener(aVoid3 -> {
                                            settingsRepository.clearAllSettings();
                                            operationResult.setValue(new OperationResult(
                                                    OperationResult. Status.SUCCESS,
                                                    "Account deleted successfully"
                                            ));
                                            logAnalyticsEvent("delete_account_success", null, null);
                                        })
                                        .addOnFailureListener(e -> {
                                            operationResult.setValue(new OperationResult(
                                                    OperationResult.Status.ERROR,
                                                    "Failed to delete account: " + e.getMessage()
                                            ));
                                            logAnalyticsEvent("delete_account_failure", "stage", "auth_delete");
                                        });
                            })
                            .addOnFailureListener(e -> {
                                operationResult.setValue(new OperationResult(
                                        OperationResult.Status.ERROR,
                                        "Failed to delete fitness profile: " + e.getMessage()
                                ));
                                logAnalyticsEvent("delete_account_failure", "stage", "fitness_delete");
                            });
                })
                .addOnFailureListener(e -> {
                    operationResult.setValue(new OperationResult(
                            OperationResult.Status.ERROR,
                            "Failed to delete user profile: " + e.getMessage()
                    ));
                    logAnalyticsEvent("delete_account_failure", "stage", "user_delete");
                });
    }

    // Export Data
    public void exportData() {
        operationResult.setValue(new OperationResult(OperationResult.Status.LOADING, "Preparing your data..."));

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            operationResult.setValue(new OperationResult(OperationResult.Status.ERROR, "User not authenticated"));
            return;
        }

        String userId = user.getUid();
        Map<String, Object> exportData = new HashMap<>();

        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        UserProfile userProfile = userDoc.toObject(UserProfile. class);
                        exportData. put("userProfile", userProfile);
                    }

                    firestore.collection("fitnessProfiles").document(userId).get()
                            .addOnSuccessListener(fitnessDoc -> {
                                if (fitnessDoc.exists()) {
                                    FitnessProfile fitnessProfile = fitnessDoc.toObject(FitnessProfile.class);
                                    exportData.put("fitnessProfile", fitnessProfile);
                                }

                                exportData.put("exportDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                                exportData.put("appVersion", getAppVersion());

                                // Convert to JSON
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String json = gson. toJson(exportData);

                                // Save to file
                                String fileName = "bytelift_export_" +
                                        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".json";
                                File file = new File(getApplication().getCacheDir(), fileName);

                                try (FileWriter writer = new FileWriter(file)) {
                                    writer.write(json);
                                    operationResult.setValue(new OperationResult(
                                            OperationResult.Status.SUCCESS,
                                            file.getAbsolutePath()
                                    ));
                                    logAnalyticsEvent("export_data", null, null);
                                } catch (IOException e) {
                                    operationResult.setValue(new OperationResult(
                                            OperationResult.Status.ERROR,
                                            "Failed to save export file: " + e.getMessage()
                                    ));
                                }
                            })
                            .addOnFailureListener(e -> {
                                operationResult.setValue(new OperationResult(
                                        OperationResult.Status.ERROR,
                                        "Failed to fetch fitness profile: " + e.getMessage()
                                ));
                            });
                })
                .addOnFailureListener(e -> {
                    operationResult.setValue(new OperationResult(
                            OperationResult.Status. ERROR,
                            "Failed to fetch user profile: " + e.getMessage()
                    ));
                });
    }

    // Clear Cache
    public void clearCache() {
        try {
            File cacheDir = getApplication().getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
            operationResult.setValue(new OperationResult(
                    OperationResult.Status.SUCCESS,
                    "Cache cleared successfully"
            ));
            logAnalyticsEvent("clear_cache", null, null);
        } catch (Exception e) {
            operationResult.setValue(new OperationResult(
                    OperationResult.Status.ERROR,
                    "Failed to clear cache: " + e.getMessage()
            ));
        }
    }

    public class DebouncedOnClickListener implements View.OnClickListener {
        private long lastClickTime = 0;
        private static final long DEBOUNCE_INTERVAL = 300;
        private final View. OnClickListener delegate;

        public DebouncedOnClickListener(View. OnClickListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > DEBOUNCE_INTERVAL) {
                lastClickTime = currentTime;
                delegate.onClick(v);
            }
        }
    }

    private void logAnalyticsEvent(String eventName, String paramKey, String paramValue) {
        // Log to console for debugging
        Log.d("SettingsAnalytics",
                "Event: " + eventName +
                        (paramKey != null ?  ", " + paramKey + "=" + paramValue : ""));

        // TODO:  Uncomment below and add Firebase Analytics dependency to use analytics
        // implementation("com.google.firebase:firebase-analytics") in build.gradle

    /*
    Bundle bundle = new Bundle();
    if (paramKey != null && paramValue != null) {
        bundle.putString(paramKey, paramValue);
    }
    com.google.firebase.analytics.FirebaseAnalytics
        .getInstance(getApplication())
        .logEvent(eventName, bundle);
    */
    }


    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir. list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir. delete();
        }
        return false;
    }

    // App Version
    public String getAppVersion() {
        try {
            PackageInfo pInfo = getApplication().getPackageManager()
                    .getPackageInfo(getApplication().getPackageName(), 0);
            return "v" + pInfo.versionName + " (Build " + pInfo.versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    // Notification scheduling (placeholder for future WorkManager integration)
    private void scheduleWorkoutReminders() {
        // TODO: Implement WorkManager periodic task
        // WorkManager will read reminderDays and reminderTime from SettingsRepository
        // and schedule notifications accordingly
    }

    private void cancelWorkoutReminders() {
        // TODO: Cancel WorkManager tasks
    }

    private void cancelAllNotifications() {
        cancelWorkoutReminders();
    }

    // Analytics (privacy-respecting, no PII)

    public void logFeedback() {
        logAnalyticsEvent("send_feedback", null, null);
    }

    public void logBugReport(boolean includeDebugInfo) {
        logAnalyticsEvent("report_bug", "include_debug_info", String.valueOf(includeDebugInfo));
    }
}