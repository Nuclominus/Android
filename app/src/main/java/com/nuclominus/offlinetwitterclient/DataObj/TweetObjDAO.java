package com.nuclominus.offlinetwitterclient.DataObj;


import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class TweetObjDAO extends BaseDaoImpl<TweetObj, Integer> {

    protected TweetObjDAO(ConnectionSource connectionSource,
                          Class<TweetObj> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<TweetObj> getAllTweets() throws SQLException {
        return this.queryBuilder().orderBy("createdAt",false).query();
    }

    public Long getLastID() throws SQLException {
        long numRows = this.countOf();
        if (numRows>0) {
            return this.queryBuilder().orderBy("createdAt", false).queryForFirst().getId();
        }
        return null;
    }

    public boolean checkExist(Long id) throws SQLException {
        Long having = this.queryBuilder().where().eq("id", id).countOf();
        if (having != null) {
            return true;
        }
        return false;
    }
}