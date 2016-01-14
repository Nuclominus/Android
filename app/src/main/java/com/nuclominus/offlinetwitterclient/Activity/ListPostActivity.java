package com.nuclominus.offlinetwitterclient.Activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.nuclominus.offlinetwitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;


public class ListPostActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private TweetTimelineListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_post);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        initAdapter();
        ((ListView) findViewById(android.R.id.list)).setAdapter(adapter);

    }

    private void initAdapter() {
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(Twitter.getSessionManager().getActiveSession().getUserName())
                .build();
        adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
    }

    @Override
    public void onRefresh() {

        swipeLayout.setRefreshing(true);

        adapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                swipeLayout.setRefreshing(false);

            }

            @Override
            public void failure(TwitterException exception) {
                swipeLayout.setRefreshing(false);
            }
        });
    }
}
