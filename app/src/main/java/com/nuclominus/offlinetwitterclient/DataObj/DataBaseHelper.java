package com.nuclominus.offlinetwitterclient.DataObj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DataBaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME ="tweet.db";

    private static final int DATABASE_VERSION = 1;

    private TweetObjDAO tweetDao = null;

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, TweetObj.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            TableUtils.dropTable(connectionSource, TweetObj.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    public TweetObjDAO getTweetObjDAO() throws SQLException{
        if(tweetDao == null){
            tweetDao = new TweetObjDAO(getConnectionSource(), TweetObj.class);
        }
        return tweetDao;
    }

    public void clearTweetObjDAO() throws SQLException {
        TableUtils.clearTable(getConnectionSource(), TweetObj.class);
    }

    public void createTweetObjDAO(TweetObj tweet) throws SQLException {
        if(tweetDao == null){
            tweetDao = new TweetObjDAO(getConnectionSource(), TweetObj.class);
        }
        tweetDao.create(tweet);
    }

    @Override
    public void close(){
        super.close();
        tweetDao = null;
    }
}

