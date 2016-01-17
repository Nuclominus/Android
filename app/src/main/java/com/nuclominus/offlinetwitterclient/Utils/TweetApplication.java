package com.nuclominus.offlinetwitterclient.Utils;

import android.app.Application;

public class TweetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseFactory.setHelper(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        BaseFactory.releaseHelper();
        super.onTerminate();
    }
}
