package com.moviewatchlist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private SharedPreferences sharedPreferences;

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name"; // Tambah kunci untuk nama pengguna

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(String userId, String email, String token, String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_TOKEN, token);
        editor.putString(KEY_USER_NAME, userName); // Simpan nama pengguna
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
    }

    public String getUserToken() {
        return sharedPreferences.getString(Constants.KEY_USER_TOKEN, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null); // Ambil nama pengguna
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}