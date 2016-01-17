package com.nuclominus.offlinetwitterclient.Utils;

import android.app.Application;

public class TweetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager.initializeInstance(getApplicationContext());
        ImageUtils.getInstance(getApplicationContext());
        ProjectUtils.getInstance(getApplicationContext());
        BaseFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        BaseFactory.releaseHelper();
        ImageUtils.release();
        ProjectUtils.release();
        PreferencesManager.release();
        super.onTerminate();
    }
}
