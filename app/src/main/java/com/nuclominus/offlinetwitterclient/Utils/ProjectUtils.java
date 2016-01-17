package com.nuclominus.offlinetwitterclient.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nuclominus.offlinetwitterclient.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ProjectUtils {

    private static final String DATE_PATTERN = "EE MMM dd HH:mm:ss z yyyy";
    private static ProjectUtils instance = null;
    private static Context context;

    public static ProjectUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new ProjectUtils();
            context = ctx;
        }
        return instance;
    }

    private ProjectUtils() {
    }

    public static Date convertDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        try {
            Date post = dateFormat.parse(date);
            return post;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDiffDate(Date date) {

        Date post = date;
        Date current = new Date();

        long diff = current.getTime() - post.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        if (diffHours != 0) {
            return diffHours + context.getString(R.string.hour);
        } else if (diffMinutes != 0) {
            return diffMinutes + context.getString(R.string.minute);
        } else if (diffSeconds != 0) {
            return diffSeconds + context.getString(R.string.second);
        }

        return null;
    }

    public static Date getCurrentDate() {
        Date current = new Date();
        return current;
    }

    public static boolean getNetworkState() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }


    public static void release() {
        context = null;
        instance = null;
    }
}
