package com.example.bytelift. ui.settings_page;

public class SettingsItem {
    public enum Type {
        HEADER,
        VALUE,
        TOGGLE,
        ACTION,
        DESTRUCTIVE,
        INFO
    }

    private final Type type;
    private final String title;
    private String subtitle;
    private boolean toggleState;
    private final int iconRes;
    private final ClickListener listener;

    public interface ClickListener {
        void onClick();
    }

    public interface ToggleListener {
        void onToggle(boolean isChecked);
    }

    private ToggleListener toggleListener;

    // Constructor for HEADER
    public SettingsItem(Type type, String title) {
        this.type = type;
        this.title = title;
        this.subtitle = null;
        this.iconRes = 0;
        this.listener = null;
    }

    // Constructor for VALUE, ACTION, DESTRUCTIVE
    public SettingsItem(Type type, String title, String subtitle, int iconRes, ClickListener listener) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
        this. listener = listener;
    }

    // Constructor for TOGGLE
    public SettingsItem(Type type, String title, String subtitle, boolean toggleState,
                        ToggleListener toggleListener) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.toggleState = toggleState;
        this.iconRes = 0;
        this.listener = null;
        this.toggleListener = toggleListener;
    }

    // Constructor for INFO
    public SettingsItem(Type type, String title, String subtitle) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.iconRes = 0;
        this.listener = null;
    }

    // Getters
    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public boolean getToggleState() { return toggleState; }
    public int getIconRes() { return iconRes; }
    public ClickListener getListener() { return listener; }
    public ToggleListener getToggleListener() { return toggleListener; }

    // Setters
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setToggleState(boolean toggleState) { this.toggleState = toggleState; }
}