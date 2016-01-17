package com.nuclominus.offlinetwitterclient.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nuclominus.offlinetwitterclient.Events.OnlineEvent;

import de.greenrobot.event.EventBus;

public class InternetReceiver extends BroadcastReceiver {

    private EventBus bus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        OnlineEvent event = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (activeNetInfo != null) {
            event = createEvent(true);
        } else if (mobNetInfo != null) {
            event = createEvent(true);
        } else {
            event = createEvent(false);
        }

        bus.post(event);
    }

    private OnlineEvent createEvent(boolean state) {
        return new OnlineEvent(state);
    }
}