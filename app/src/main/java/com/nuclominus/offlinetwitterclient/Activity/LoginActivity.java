package com.nuclominus.offlinetwitterclient.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nuclominus.offlinetwitterclient.BuildConfig;
import com.nuclominus.offlinetwitterclient.R;
import com.nuclominus.offlinetwitterclient.Utils.ImageUtils;
import com.nuclominus.offlinetwitterclient.Utils.PreferencesManager;
import com.nuclominus.offlinetwitterclient.Utils.ProjectUtils;
import com.nuclominus.offlinetwitterclient.Utils.Serializer;
import com.nuclominus.offlinetwitterclient.Utils.TwitterHelper;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.persistence.PreferenceStore;
import io.fabric.sdk.android.services.persistence.PreferenceStoreImpl;
import io.fabric.sdk.android.services.persistence.PreferenceStoreStrategy;

public class LoginActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private PreferenceStore preferenceStore;
    private PreferenceStoreStrategy<TwitterSession> preferenceStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        preferenceStore = new PreferenceStoreImpl(this.getApplicationContext(), "save_session");
        preferenceStrategy = new PreferenceStoreStrategy<>(preferenceStore, new Serializer(), "save_session");

        Fabric.with(this, new Twitter(authConfig));

        if (checkSession()) {
            startActivity(new Intent(LoginActivity.this, ListPostActivity.class));
            finish();
        } else {
            initUI();
        }

    }

    private boolean checkSession() {
        final TwitterSession restoredSession = preferenceStrategy.restore();
        if (restoredSession == null) {
            return false;
        } else {
            return true;
        }
    }

    private void initUI() {
        setContentView(R.layout.activity_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                preferenceStrategy.save(result.data);
                getprofile();
                startActivity(new Intent(LoginActivity.this, ListPostActivity.class));
                finish();
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private void getprofile() {
        TwitterHelper.getUser(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                PreferencesManager.getInstance().setValue(PreferencesManager.PROFILE_NAME, result.data.name);
                PreferencesManager.getInstance().setValue(PreferencesManager.PROFILE_TAG, result.data.screenName);

                ImageUtils.DownloadAsyncTask asynk = new ImageUtils.DownloadAsyncTask();
                asynk.execute(result.data.profileImageUrl);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
