package com.nuclominus.offlinetwitterclient.Activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.nuclominus.offlinetwitterclient.Adapter.TweetAdapter;
import com.nuclominus.offlinetwitterclient.Utils.AnimatingUtils;
import com.nuclominus.offlinetwitterclient.Utils.BaseFactory;
import com.nuclominus.offlinetwitterclient.DataObj.TweetObj;
import com.nuclominus.offlinetwitterclient.Events.OnlineEvent;
import com.nuclominus.offlinetwitterclient.R;
import com.nuclominus.offlinetwitterclient.Utils.UtilsProj;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
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

    RelativeLayout menuLayout, hideLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_post);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        initUtils();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(tweetAdapter);

        tweets = new LinkedList<>();
        getTweets(TweetObj.getTweetObjLastID());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!AnimatingUtils.animProgress)
                if (menuLayout.getMeasuredHeight() <= 10) {
                    AnimatingUtils.expand(menuLayout, hideLayer);
                } else {
                    AnimatingUtils.collapse(menuLayout, hideLayer);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }

    private void initUtils() {
        UtilsProj.getInstance(this);
        eventBus.register(this);
        network_state = UtilsProj.getNetworkState();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_burger);

        menuLayout = ((RelativeLayout) findViewById(R.id.layoutMenu));
        hideLayer = ((RelativeLayout) findViewById(R.id.hideLayer));
    }

    public void onEvent(OnlineEvent event) {
        network_state = event.getState();
        getTweets(TweetObj.getTweetObjLastID());
    }

    public void getTweets(final Long since_id) {

        if (network_state) {
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            twitterApiClient.getStatusesService().homeTimeline(null, since_id, null, null, null, null, null, new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    if (listResult.data.size() > 0) {
                        TweetObj.parseTweets(listResult.data);
                    }
                    initTweets();
                }

                @Override
                public void failure(TwitterException e) {
                    Snackbar.make(swipeLayout, "Twitter: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    swipeLayout.setRefreshing(false);
                }
            });
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
        network_state = UtilsProj.getNetworkState();
        swipeLayout.setRefreshing(true);
        if (tweets != null && tweets.size() > 0)
            getTweets(tweets.get(0).getId());
    }


}
