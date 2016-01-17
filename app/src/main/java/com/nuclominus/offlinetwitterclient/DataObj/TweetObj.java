package com.nuclominus.offlinetwitterclient.DataObj;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nuclominus.offlinetwitterclient.Utils.BaseFactory;
import com.nuclominus.offlinetwitterclient.Utils.ProjectUtils;
import com.twitter.sdk.android.core.models.Tweet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "tweetobj")
public class TweetObj {

    @DatabaseField(dataType = DataType.DATE)
    private Date createdAt;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = "id")
    private long id;

    @DatabaseField()
    private String screen_name;

    @DatabaseField()
    private String text;

    @DatabaseField()
    private String user;

    @DatabaseField()
    private String img;

    @DatabaseField()
    private boolean isSent;

    private String diffTime;

    public TweetObj() {

    }

    public TweetObj(String createdAt, long id, String text, String user, String screen_name, String img, boolean isSent) {
        this.createdAt = ProjectUtils.convertDate(createdAt);
        this.id = id;
        this.text = text;
        this.user = user;
        this.screen_name = screen_name;
        this.img = img;
        this.isSent = isSent;
        updateDiff();
    }

    public TweetObj(Date createdAt, String text, String user, String screen_name) {
        this.createdAt = createdAt;
        this.text = text;
        this.user = user;
        this.screen_name = screen_name;
        this.isSent = false;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getDiffTime() {
        return diffTime;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getImg() {
        return img;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public void updateDiff() {
        this.diffTime = ProjectUtils.getDiffDate(getCreatedAt());
    }

    static public void parseTweets(List<Tweet> items) {
        ArrayList<TweetObj> tweets = new ArrayList<>();
        for (Tweet obj : items) {
            TweetObj tweet = new TweetObj(obj.createdAt, obj.id, obj.text, obj.user.name,
                    obj.user.screenName, obj.user.profileImageUrl, true);
            tweets.add(tweet);
        }
        saveTweets(tweets);
    }

    static public void saveTweet(TweetObj tweet) {
        try {
            if (BaseFactory.getHelper().getTweetObjDAO().checkExist(tweet.getId())) {
                BaseFactory.getHelper().createTweetObjDAO(tweet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static public void saveTweets(ArrayList<TweetObj> tweets) {
        for (TweetObj tweet : tweets) {
            saveTweet(tweet);
        }
    }

    static public Long getTweetObjLastID() {
        Long id = null;
        try {
            id = BaseFactory.getHelper().getTweetObjDAO().getLastID();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


}
