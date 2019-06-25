package com.hdev.autobgeraser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {

    private static AppPreferences appPreferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private AppPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppPreferences getInstance(Context context) {
        if (appPreferences == null) {
            appPreferences = new AppPreferences(context.getApplicationContext());
        }
        return appPreferences;
    }

    public static String getPath(){
        return sharedPreferences.getString("path_downloaded", "");
    }

    public static void savePath(String path) {
        editor = sharedPreferences.edit();
        editor.putString("path_downloaded", path);
        editor.apply();
    }

    public static boolean getFirstLaunch() {
        return sharedPreferences.getBoolean("first_launch", true);
    }

    public static void setFirstLaunch(boolean isFisrt) {
        editor = sharedPreferences.edit();
        editor.putBoolean("first_launch", isFisrt);
        editor.apply();
    }

    public static String getApiKey() {
        return sharedPreferences.getString("api_key", "");
    }

    public static void setApiKey(String apiKey) {
        editor = sharedPreferences.edit();
        editor.putString("api_key", apiKey);
        editor.apply();
    }
}
