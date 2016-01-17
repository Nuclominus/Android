package com.nuclominus.offlinetwitterclient.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuclominus.offlinetwitterclient.DataObj.TweetObj;
import com.nuclominus.offlinetwitterclient.R;
import com.nuclominus.offlinetwitterclient.Utils.PreferencesManager;
import com.nuclominus.offlinetwitterclient.Utils.TwitterHelper;
import com.nuclominus.offlinetwitterclient.Utils.ProjectUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

public class CreateTweetActivity extends AppCompatActivity {

    RelativeLayout createTweetBtn;
    EditText eTextMessage;
    TextView tVChrCount;
    public static final int maxChrCount = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {

        setContentView(R.layout.activity_create_tweet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_close);
        toolbar.setTitle("");

        createTweetBtn = (RelativeLayout) findViewById(R.id.rlOptButtonCreate);
        createTweetBtn.setTransitionName(getString(R.string.activity_button_transition));

        createTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMessage(eTextMessage.getText().toString());
            }
        });

        eTextMessage = (EditText) findViewById(R.id.eTMessage);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(eTextMessage, InputMethodManager.SHOW_IMPLICIT);

        tVChrCount = (TextView) findViewById(R.id.tVChrCount);
        tVChrCount.setText(maxChrCount + "");

        eTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int diff = (maxChrCount - s.length());
                tVChrCount.setText(diff + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_activity_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void postMessage(final String message) {
        if (message.length() <= maxChrCount) {
            TwitterHelper.sendPost(new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    CreateTweetActivity.this.onBackPressed();
                }

                @Override
                public void failure(TwitterException e) {
                    TweetObj tweetObj = new TweetObj(ProjectUtils.getCurrentDate(), message,
                            PreferencesManager.getInstance().getValue(PreferencesManager.PROFILE_NAME), PreferencesManager.getInstance().getValue(PreferencesManager.PROFILE_NAME));
                    tweetObj.saveTweet(tweetObj);
                    CreateTweetActivity.this.onBackPressed();
                    TwitterHelper.showSnack(createTweetBtn, e.getLocalizedMessage());
                }
            }, message);
        } else {
            TwitterHelper.showSnack(createTweetBtn, getString(R.string.out_max_lenght));
        }
    }

}
