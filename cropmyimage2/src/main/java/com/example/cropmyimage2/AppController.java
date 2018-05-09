package com.example.cropmyimage2;

import android.app.Application;

/**
 * compile 'com.android.support:multidex:1.0.3'
 * defaultConfig {
 * multiDexEnabled true
 * }
 **/


public class AppController extends Application {
    private static AppController sInstance;
    public static Constant constant;
    public static final String TAG = AppController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        constant = new Constant(this);
    }

    public static synchronized AppController getInstance() {
        return sInstance;
    }}