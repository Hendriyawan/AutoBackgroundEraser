package com.hdev.autobgeraser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void savePath(Context context, String path) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString("path_downloaded", path);
        editor.apply();
    }

    public static void saveTempFile(Context context, String fileName) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString("temp_file", fileName);
        editor.apply();
    }

    public static String getPath(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("path_downloaded", "");
    }

    public static String getTempFile(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("temp_file", "");
    }
}
