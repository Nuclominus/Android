package com.nuclominus.offlinetwitterclient.Utils;


import android.support.design.widget.Snackbar;
import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

public class TwitterHelper {

    static TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();

    static public void gethomeTimeline(Callback<List<Tweet>> callback, final Long since_id) {
        twitterApiClient.getStatusesService().homeTimeline(null, since_id, null, null, null, null, null, callback);
    }

    static public void sendPost(Callback<Tweet> callback, final String message) {
        twitterApiClient.getStatusesService().update(message, null, null, null, null, null, null, null, null, callback);
    }

    static public void getUser(Callback<User> callback) {
        twitterApiClient.getAccountService().verifyCredentials(null, null, callback);
    }

    static public void showSnack(View view, String message) {
        Snackbar.make(view, "Twitter: " + message, Snackbar.LENGTH_LONG).show();
    }

}
