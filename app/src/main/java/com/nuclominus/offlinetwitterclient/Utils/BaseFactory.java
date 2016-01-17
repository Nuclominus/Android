package com.nuclominus.offlinetwitterclient.Utils;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nuclominus.offlinetwitterclient.DataObj.DataBaseHelper;

public class BaseFactory {
    private static DataBaseHelper databaseHelper;

    public static DataBaseHelper getHelper() {
        return databaseHelper;
    }

    public static void setHelper(Context context) {
        databaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
