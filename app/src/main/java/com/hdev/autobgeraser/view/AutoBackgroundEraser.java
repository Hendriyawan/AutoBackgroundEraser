package com.hdev.autobgeraser.view;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.google.firebase.FirebaseApp;

public class AutoBackgroundEraser extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        AndroidNetworking.initialize(getApplicationContext());
    }
}
