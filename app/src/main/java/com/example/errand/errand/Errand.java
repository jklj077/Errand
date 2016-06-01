package com.example.errand.errand;

import android.app.Application;

/**
 * Created by jklj077 on 2016/6/1.
 */
public class Errand extends Application {
    public String key;

    @Override
    public void onCreate() {
        super.onCreate();
        key = null;
    }
}
