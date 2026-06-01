package com.example.cineverse.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Username
    public void saveUsername(String username) {
        prefs.edit()
                .putString(Constants.PREF_USERNAME, username)
                .apply();
    }
    public String getUsername() {
        return prefs.getString(
                Constants.PREF_USERNAME, "Cinéphile"
        );
    }

    // Thème
    public void saveTheme(String theme) {
        prefs.edit()
                .putString(Constants.PREF_THEME, theme)
                .apply();
    }
    public String getTheme() {
        return prefs.getString(
                Constants.PREF_THEME, "Sombre"
        );
    }

    // Notifications
    public void saveNotifications(boolean enabled) {
        prefs.edit()
                .putBoolean("notifications", enabled)
                .apply();
    }
    public boolean getNotifications() {
        return prefs.getBoolean("notifications", true);
    }

    // Auto-save
    public void saveAutoSave(boolean enabled) {
        prefs.edit()
                .putBoolean("auto_save", enabled)
                .apply();
    }
    public boolean getAutoSave() {
        return prefs.getBoolean("auto_save", true);
    }
}