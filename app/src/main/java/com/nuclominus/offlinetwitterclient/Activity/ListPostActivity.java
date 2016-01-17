package com.nuclominus.offlinetwitterclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.nuclominus.offlinetwitterclient.Adapter.TweetAdapter;
import com.nuclominus.offlinetwitterclient.DataObj.TweetObj;
import com.nuclominus.offlinetwitterclient.Events.OnlineEvent;
import com.nuclominus.offlinetwitterclient.Events.UpdateEvent;
import com.nuclominus.offlinetwitterclient.R;
import com.nuclominus.offlinetwitterclient.Service.UpdateService;
import com.nuclominus.offlinetwitterclient.Utils.AnimateUtils;
import com.nuclominus.offlinetwitterclient.Utils.BaseFactory;
import com.nuclominus.offlinetwitterclient.Utils.ProjectUtils;
import com.nuclominus.offlinetwitterclient.Utils.TwitterHelper;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ListPostActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private TweetAdapter tweetAdapter;
    private LinkedList<TweetObj> tweets;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private EventBus eventBus = EventBus.getDefault();
    private boolean network_state;

    private boolean pauseState = false;

    RelativeLayout menuLayout, hideLayer, createTweetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initUtils();
    }

    private void initUI() {
        setContentView(R.layout.activity_list_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_burger);

        menuLayout = ((RelativeLayout) findViewById(R.id.layoutMenu));
        hideLayer = ((RelativeLayout) findViewById(R.id.hideLayer));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(tweetAdapter);

        tweets = new LinkedList<>();

        createTweetBtn = (RelativeLayout) findViewById(R.id.rlOptButtonCreate);
        createTweetBtn.setTransitionName(getString(R.string.activity_button_transition));
        createTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListPostActivity.this, CreateTweetActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ListPostActivity.this, createTweetBtn, getString(R.string.activity_button_transition));
                startActivity(intent, options.toBundle());
            }
        });

        startService(new Intent(this, UpdateService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pauseState) {
            AnimateUtils.collapse(menuLayout, hideLayer);
            getTweets(TweetObj.getTweetObjLastID());
            pauseState = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseState = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!AnimateUtils.animProgress)
                if (menuLayout.getMeasuredHeight() <= 10) {
                    AnimateUtils.expand(menuLayout, hideLayer);
                } else {
                    AnimateUtils.collapse(menuLayout, hideLayer);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UpdateService.class));
        super.onDestroy();
    }

    private void initUtils() {
        eventBus.register(this);
        network_state = ProjectUtils.getNetworkState();
    }

    private void update() {
        swipeLayout.setRefreshing(true);
        checkNotSend();
        getTweets(TweetObj.getTweetObjLastID());
    }

    public void onEvent(OnlineEvent event) {
        network_state = event.getState();
        if (network_state) {
            update();
        }
    }

    public void onEvent(UpdateEvent event) {
        if (network_state) {
            update();
        }
    }

    public void getTweets(final Long since_id) {
        if (network_state) {
            TwitterHelper.gethomeTimeline(new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> listResult) {
                    if (listResult.data.size() > 0) {
                        TweetObj.parseTweets(listResult.data);
                    }
                    swipeLayout.setRefreshing(false);
                    initTweets();
                }

                @Override
                public void failure(TwitterException e) {
                    TwitterHelper.showSnack(swipeLayout, e.getLocalizedMessage());
                    swipeLayout.setRefreshing(false);
                }
            }, since_id);
        } else {
            initTweets();
        }
    }

    private void initTweets() {
        try {
            tweets.clear();
            tweets.addAll(BaseFactory.getHelper().getTweetObjDAO().getAllTweets());
            initAdapter();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkNotSend() {
        try {
            List<TweetObj> tweetObjs = BaseFactory.getHelper().getTweetObjDAO().getAllNotSend();
            if (tweetObjs != null && tweetObjs.size() > 0) {
                final ArrayList<Tweet> sendTweets = new ArrayList<>();

                for (final TweetObj tweet : tweetObjs) {
                    TwitterHelper.sendPost(new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            try {
                                BaseFactory.getHelper().getTweetObjDAO().removeItem(tweet.getId());
                                sendTweets.add(result.data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failure(TwitterException e) {
                            TwitterHelper.showSnack(swipeLayout, e.getLocalizedMessage());
                        }
                    }, tweet.getText());
                }
                TweetObj.parseTweets(sendTweets);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initAdapter() {
        if (tweets != null) {
            if (tweetAdapter != null) {
                tweetAdapter.notifyDataSetChanged();
            } else {
                tweetAdapter = new TweetAdapter(tweets, ListPostActivity.this);
                recyclerView.setAdapter(tweetAdapter);
            }
        } else {
            tweetAdapter = new TweetAdapter(new ArrayList<TweetObj>(), ListPostActivity.this);
        }
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        network_state = ProjectUtils.getNetworkState();
        checkNotSend();
        swipeLayout.setRefreshing(true);
        if (tweets != null && tweets.size() > 0)
            getTweets(tweets.get(0).getId());
    }

}
