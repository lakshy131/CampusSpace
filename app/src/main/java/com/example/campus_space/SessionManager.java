package com.example.campus_space;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "CampusSpaceSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_UID = "userUid";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save session after successful login or registration.
     */
    public void createSession(String uid, String name, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_UID, uid);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    /**
     * Check if user is logged in.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserUid() {
        return prefs.getString(KEY_USER_UID, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "");
    }

    /**
     * Update just the name (e.g., after profile edit).
     */
    public void updateName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    /**
     * Update just the role.
     */
    public void updateRole(String role) {
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    /**
     * Clear session on logout.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
