package com.example.bytelift.ui.settings_page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os. Looper;
import android. view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat. app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app. Fragment;
import androidx.lifecycle. ViewModelProvider;
import androidx. recyclerview.widget.LinearLayoutManager;

import com.example.bytelift.R;
import com.example.bytelift.WelcomeActivity;
import com.example.bytelift.data.settings.SettingsState;
import com.example.bytelift.databinding.FragmentSettingsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google. android.material.snackbar. Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private SettingsAdapter adapter;
    private List<SettingsItem> settingsItems;
    private Handler debounceHandler;
    private boolean isProcessing = false;

    // Row position tracking for dynamic updates
    private static final int POS_NOTIFICATIONS_TOGGLE = 5;
    private static final int POS_WORKOUT_REMINDERS = 6;
    private static final int POS_REMINDER_SCHEDULE = 7;
    private static final int POS_THEME = 9;
    private static final int POS_WEIGHT_UNIT = 10;
    private static final int POS_HEIGHT_UNIT = 11;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding. inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        debounceHandler = new Handler(Looper.getMainLooper());
        viewModel = new ViewModelProvider(this).get(SettingsViewModel. class);

        setupRecyclerView();
        observeSettings();
        observeOperationResults();
    }

    private void setupRecyclerView() {
        settingsItems = new ArrayList<>();
        adapter = new SettingsAdapter(settingsItems);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(null);
    }

    private void observeSettings() {
        viewModel.getSettings().observe(getViewLifecycleOwner(), settings -> {
            if (settings != null) {
                buildSettingsList(settings);
            }
        });
    }

    private void observeOperationResults() {
        viewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    showLoading(true);
                    showSnackbar(result.getMessage(), Snackbar.LENGTH_SHORT);
                    break;

                case SUCCESS:
                    showLoading(false);
                    handleSuccessResult(result.getMessage());
                    break;

                case ERROR:
                    showLoading(false);
                    showErrorDialog(result.getMessage());
                    break;
            }
        });
    }

    // ============================================
    // BUILD SETTINGS LIST
    // ============================================

    private void buildSettingsList(SettingsState settings) {
        settingsItems.clear();

        // ACCOUNT SECTION
        settingsItems.add(createHeaderItem("ACCOUNT"));
        settingsItems.add(createValueItem("Edit Account", "Update your profile information", this::handleEditAccount));
        settingsItems.add(createActionItem("Logout", this::handleLogout));
        settingsItems.add(createDestructiveItem("Delete Account", this::handleDeleteAccount));

        // NOTIFICATIONS & REMINDERS SECTION
        settingsItems.add(createHeaderItem("NOTIFICATIONS & REMINDERS"));
        settingsItems.add(createToggleItem("Enable Notifications", "Allow ByteLift to send you notifications",
                settings.isNotificationsEnabled(), this::handleNotificationsToggle));
        settingsItems.add(createToggleItem("Workout Reminders",
                settings.isNotificationsEnabled() ? "Get reminded about your workout schedule" : "Enable notifications first",
                settings.isWorkoutReminders() && settings.isNotificationsEnabled(),
                enabled -> handleWorkoutRemindersToggle(enabled, settings.isNotificationsEnabled())));
        settingsItems.add(createValueItem("Reminder Schedule", settings.getFormattedReminderSchedule(), this::handleReminderSchedule));

        // APP PREFERENCES SECTION
        settingsItems. add(createHeaderItem("APP PREFERENCES"));
        settingsItems. add(createValueItem("Theme", settings.getTheme().getDisplayName(), () -> handleThemeChange(settings.getTheme())));
        settingsItems.add(createValueItem("Weight Unit", settings. getWeightUnit().getDisplayName(), () -> handleWeightUnitChange(settings.getWeightUnit())));
        settingsItems. add(createValueItem("Height Unit", settings.getHeightUnit().getDisplayName(), () -> handleHeightUnitChange(settings.getHeightUnit())));

        // DATA, PRIVACY & CONTROL SECTION
        settingsItems. add(createHeaderItem("DATA, PRIVACY & CONTROL"));
        settingsItems.add(createValueItem("Export My Data", "Download your data as JSON", this::handleExportData));
        settingsItems.add(createValueItem("Privacy Policy", "View our privacy policy", this::handlePrivacyPolicy));
        settingsItems.add(createValueItem("Terms of Service", "View terms and conditions", this::handleTermsOfService));
        settingsItems.add(createActionItem("Clear Local Cache", this::handleClearCache));

        // SUPPORT SECTION
        settingsItems. add(createHeaderItem("SUPPORT"));
        settingsItems.add(createValueItem("Help & FAQ", "Get answers to common questions", this::handleHelp));
        settingsItems. add(createValueItem("Send Feedback", "Share your thoughts with us", this::handleFeedback));
        settingsItems.add(createValueItem("Report a Bug", "Help us improve ByteLift", this::handleBugReport));

        // ABOUT SECTION
        settingsItems.add(createHeaderItem("ABOUT"));
        settingsItems. add(new SettingsItem(SettingsItem.Type.INFO, "ByteLift " + viewModel.getAppVersion(), null));

        adapter.notifyDataSetChanged();
    }

    // ============================================
    // ITEM CREATION HELPERS
    // ============================================

    private SettingsItem createHeaderItem(String title) {
        return new SettingsItem(SettingsItem.Type.HEADER, title);
    }

    private SettingsItem createValueItem(String title, String subtitle, SettingsItem. ClickListener listener) {
        return new SettingsItem(SettingsItem.Type.VALUE, title, subtitle, 0, listener);
    }

    private SettingsItem createActionItem(String title, SettingsItem.ClickListener listener) {
        return new SettingsItem(SettingsItem.Type.ACTION, title, null, 0, listener);
    }

    private SettingsItem createDestructiveItem(String title, SettingsItem.ClickListener listener) {
        return new SettingsItem(SettingsItem.Type.DESTRUCTIVE, title, null, 0, listener);
    }

    private SettingsItem createToggleItem(String title, String subtitle, boolean state, SettingsItem.ToggleListener listener) {
        return new SettingsItem(SettingsItem.Type.TOGGLE, title, subtitle, state, listener);
    }

    // ============================================
    // ACCOUNT HANDLERS
    // ============================================

    private void handleEditAccount() {
        if (preventRapidClicks()) return;
        Intent intent = new Intent(requireActivity(), EditAccountActivity.class);
        startActivity(intent);
    }

    private void handleLogout() {
        if (preventRapidClicks()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log out of ByteLift? ")
                .setMessage("You can always log back in")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    viewModel.logout();
                    navigateToWelcome("Logged out successfully");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleDeleteAccount() {
        if (preventRapidClicks()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete your account?")
                .setMessage("This action is permanent.  All your data will be deleted and cannot be recovered.\n\nThis includes:\n• Your profile\n• Fitness data\n• Workout history\n• All app settings")
                .setPositiveButton("Delete", (dialog, which) -> showDeleteConfirmation())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are you absolutely sure?")
                .setMessage("This cannot be undone!")
                .setPositiveButton("Yes, Delete Forever", (dialog, which) -> {
                    viewModel.deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ============================================
    // NOTIFICATION HANDLERS
    // ============================================

    private void handleNotificationsToggle(boolean enabled) {
        debounceHandler.removeCallbacksAndMessages(null);
        debounceHandler.postDelayed(() -> {
            viewModel.setNotificationsEnabled(enabled);
            if (! enabled) {
                updateItemSubtitle(POS_WORKOUT_REMINDERS, "Enable notifications first");
            } else {
                updateItemSubtitle(POS_WORKOUT_REMINDERS, "Get reminded about your workout schedule");
            }
        }, 300);
    }

    private void handleWorkoutRemindersToggle(boolean enabled, boolean notificationsEnabled) {
        if (! notificationsEnabled) {
            showSnackbar("Please enable notifications first", Snackbar.LENGTH_SHORT);
            updateItemToggleState(POS_WORKOUT_REMINDERS, false);
            return;
        }

        debounceHandler.removeCallbacksAndMessages(null);
        debounceHandler.postDelayed(() -> {
            viewModel.setWorkoutReminders(enabled);
        }, 300);
    }

    private void handleReminderSchedule() {
        if (preventRapidClicks()) return;
        Intent intent = new Intent(requireActivity(), ReminderScheduleActivity. class);
        startActivity(intent);
    }

    // ============================================
    // APP PREFERENCES HANDLERS
    // ============================================

    private void handleThemeChange(SettingsState. Theme currentTheme) {
        if (preventRapidClicks()) return;

        String[] options = {"System", "Light", "Dark"};
        int currentIndex = currentTheme.ordinal();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(options, currentIndex, (dialog, which) -> {
                    SettingsState.Theme selectedTheme = SettingsState. Theme.values()[which];

                    // Save the theme
                    viewModel.setTheme(selectedTheme);

                    // Update the display
                    updateItemSubtitle(POS_THEME, selectedTheme.getDisplayName());

                    dialog.dismiss();

                    // Just recreate current activity (stays on settings page)
                    requireActivity().recreate();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void handleWeightUnitChange(SettingsState.WeightUnit currentUnit) {
        if (preventRapidClicks()) return;

        String[] options = {"Kilograms (kg)", "Pounds (lb)"};
        int currentIndex = currentUnit == SettingsState.WeightUnit.KG ? 0 :  1;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Weight Unit")
                .setSingleChoiceItems(options, currentIndex, (dialog, which) -> {
                    SettingsState.WeightUnit selectedUnit = which == 0 ? SettingsState.WeightUnit.KG :  SettingsState.WeightUnit.LB;
                    viewModel.setWeightUnit(selectedUnit);
                    updateItemSubtitle(POS_WEIGHT_UNIT, selectedUnit.getDisplayName());
                    dialog. dismiss();
                    showSnackbar("Weight unit updated", Snackbar.LENGTH_SHORT);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleHeightUnitChange(SettingsState.HeightUnit currentUnit) {
        if (preventRapidClicks()) return;

        String[] options = {"Centimeters (cm)", "Feet & Inches (ft)"};
        int currentIndex = currentUnit == SettingsState.HeightUnit.CM ? 0 : 1;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Height Unit")
                .setSingleChoiceItems(options, currentIndex, (dialog, which) -> {
                    SettingsState.HeightUnit selectedUnit = which == 0 ? SettingsState.HeightUnit. CM : SettingsState.HeightUnit.FT_IN;
                    viewModel. setHeightUnit(selectedUnit);
                    updateItemSubtitle(POS_HEIGHT_UNIT, selectedUnit.getDisplayName());
                    dialog.dismiss();
                    showSnackbar("Height unit updated", Snackbar. LENGTH_SHORT);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ============================================
    // DATA & PRIVACY HANDLERS
    // ============================================

    private void handleExportData() {
        if (preventRapidClicks()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Export Your Data")
                .setMessage("This will create a JSON file containing your profile, fitness data, and workout history.")
                .setPositiveButton("Export", (dialog, which) -> viewModel.exportData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handlePrivacyPolicy() {
        if (preventRapidClicks()) return;
        openUrl("https://bytelift.app/privacy");
    }

    private void handleTermsOfService() {
        if (preventRapidClicks()) return;
        openUrl("https://bytelift.app/terms");
    }

    private void handleClearCache() {
        if (preventRapidClicks()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear App Cache? ")
                .setMessage("This will clear temporary data but won't affect your account or saved workouts.")
                .setPositiveButton("Clear", (dialog, which) -> viewModel.clearCache())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ============================================
    // SUPPORT HANDLERS
    // ============================================

    private void handleHelp() {
        if (preventRapidClicks()) return;
        showSnackbar("Help & FAQ coming soon", Snackbar.LENGTH_SHORT);
    }

    private void handleFeedback() {
        if (preventRapidClicks()) return;
        viewModel.logFeedback();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@bytelift. app"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "ByteLift Feedback");
        intent.putExtra(Intent.EXTRA_TEXT,
                "App Version: " + viewModel.getAppVersion() + "\n\n" +
                        "What do you like about ByteLift?\n\n\n" +
                        "What could be improved?\n\n\n" +
                        "Any other feedback:\n\n");

        try {
            startActivity(Intent.createChooser(intent, "Send Feedback"));
        } catch (Exception e) {
            showSnackbar("No email app found", Snackbar.LENGTH_LONG);
        }
    }

    private void handleBugReport() {
        if (preventRapidClicks()) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Report a Bug")
                .setMessage("Include device information to help us debug? ")
                .setPositiveButton("Yes, Include Info", (dialog, which) -> {
                    viewModel.logBugReport(true);
                    sendBugReport(true);
                })
                .setNegativeButton("No, Just Bug", (dialog, which) -> {
                    viewModel.logBugReport(false);
                    sendBugReport(false);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void sendBugReport(boolean includeDebugInfo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@bytelift.app"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "ByteLift Bug Report");

        StringBuilder body = new StringBuilder();
        body.append("App Version: ").append(viewModel.getAppVersion()).append("\n\n");

        if (includeDebugInfo) {
            body.append("Device Information:\n");
            body.append("• Model: ").append(android.os.Build.MODEL).append("\n");
            body.append("• Manufacturer: ").append(android.os.Build.MANUFACTURER).append("\n");
            body.append("• Android Version: ").append(android.os.Build.VERSION.RELEASE).append("\n");
            body.append("• SDK:  ").append(android.os.Build.VERSION.SDK_INT).append("\n\n");
        }

        body.append("Bug Description:\n[Describe what happened]\n\n\n");
        body.append("Steps to Reproduce:\n1. \n2. \n3. \n\n");
        body.append("Expected Behavior:\n[What should have happened]\n\n");

        intent.putExtra(Intent. EXTRA_TEXT, body.toString());

        try {
            startActivity(Intent.createChooser(intent, "Report Bug"));
        } catch (Exception e) {
            showSnackbar("No email app found", Snackbar.LENGTH_LONG);
        }
    }

    // ============================================
    // SUCCESS RESULT HANDLER
    // ============================================

    private void handleSuccessResult(String message) {
        if (message.contains("deleted successfully")) {
            navigateToWelcome("Account deleted successfully");
        } else if (message.contains("bytelift_export")) {
            shareExportFile(message);
        } else if (message.contains("Cache cleared")) {
            showSnackbar("Cache cleared successfully", Snackbar.LENGTH_SHORT);
        } else {
            showSnackbar(message, Snackbar.LENGTH_SHORT);
        }
    }

    private void shareExportFile(String filePath) {
        try {
            File file = new File(filePath);
            Uri uri = FileProvider. getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/json");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent. putExtra(Intent.EXTRA_SUBJECT, "ByteLift Data Export");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Export ByteLift Data"));
        } catch (Exception e) {
            showErrorDialog("Failed to share export file: " + e.getMessage());
        }
    }

    // ============================================
    // UI HELPERS
    // ============================================

    private void updateItemSubtitle(int position, String newSubtitle) {
        if (position >= 0 && position < settingsItems.size()) {
            settingsItems.get(position).setSubtitle(newSubtitle);
            adapter.notifyItemChanged(position);
        }
    }

    private void updateItemToggleState(int position, boolean state) {
        if (position >= 0 && position < settingsItems.size()) {
            settingsItems.get(position).setToggleState(state);
            adapter.notifyItemChanged(position);
        }
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding. progressBar.setVisibility(show ? View.VISIBLE : View. GONE);
        }
    }

    private void showSnackbar(String message, int duration) {
        if (getView() != null) {
            Snackbar.make(getView(), message, duration).show();
        }
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable. ic_dialog_alert)
                .show();
    }

    private void navigateToWelcome(String message) {
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent. FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar("Unable to open link", Snackbar.LENGTH_SHORT);
        }
    }

    private boolean preventRapidClicks() {
        if (isProcessing) return true;
        isProcessing = true;
        debounceHandler.postDelayed(() -> isProcessing = false, 500);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (debounceHandler != null) {
            debounceHandler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }
}