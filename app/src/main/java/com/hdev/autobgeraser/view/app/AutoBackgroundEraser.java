package com.hdev.autobgeraser.view.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.hdev.autobgeraser.BuildConfig;
import com.hdev.autobgeraser.utils.AppPreferences;

import java.util.HashMap;

public class AutoBackgroundEraser extends Application {
    private static String AUTO_BG_ERASER_UPDATE_TITLE = "auto_bg_eraser_update_title";
    private static String AUTO_BG_ERASER_UPDATE_DESCRIPTION = "auto_bg_eraser_update_description";
    private static String AUTO_BG_ERASER_UPDATE_VERSION = "auto_bg_eraser_update_latest_version";
    private static String AUTO_BG_ERASER_UPDATE_FORCE_UPDATE = "auto_bg_eraser_update_force_version";

    public static void checkAppUpdate(final OnAppUpdate onAppUpdate) {
        final int VERSION_CODE = BuildConfig.VERSION_CODE;
        //HashMap which contains the defuall value for all the parameter defined
        HashMap<String, Object> defaultMap = new HashMap<>();
        defaultMap.put(AUTO_BG_ERASER_UPDATE_TITLE, "Versi Terbaru Tersedia");
        defaultMap.put(AUTO_BG_ERASER_UPDATE_DESCRIPTION, "Kini Auto Backround Eraser tersedia versi terbaru, klik update untuk dapat terus menggunakan aplikasi Auto Background Eraser");
        defaultMap.put(AUTO_BG_ERASER_UPDATE_VERSION, VERSION_CODE);
        defaultMap.put(AUTO_BG_ERASER_UPDATE_FORCE_UPDATE, VERSION_CODE);

        //instance to access the Remote Config parameters
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setDefaults(defaultMap);
        Task<Void> fetchTask = remoteConfig.fetch();
        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    remoteConfig.activateFetched();
                    String title = remoteConfig.getString(AUTO_BG_ERASER_UPDATE_TITLE);
                    String description = remoteConfig.getString(AUTO_BG_ERASER_UPDATE_DESCRIPTION);
                    int latestVersion = (int) remoteConfig.getDouble(AUTO_BG_ERASER_UPDATE_VERSION);
                    int forceUpdateVersion = (int) remoteConfig.getDouble(AUTO_BG_ERASER_UPDATE_FORCE_UPDATE);

                    boolean isCancel = true;
                    if (latestVersion > VERSION_CODE) {
                        if (forceUpdateVersion > VERSION_CODE) {
                            isCancel = true;
                        }
                        onAppUpdate.onUpdate(title, description, isCancel);
                    }
                }
            }
        });
    }

    public static void sendEvent(Context context, String event) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        firebaseAnalytics.setMinimumSessionDuration(20000);
        firebaseAnalytics.setSessionTimeoutDuration(1000000);
        firebaseAnalytics.setUserProperty("AutoBackgroundEraser", event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        AppPreferences.getInstance(getApplicationContext());
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this);
    }

    public interface OnAppUpdate {
        void onUpdate(String updateTitle, String updateDescription, boolean isCancle);
    }
}
