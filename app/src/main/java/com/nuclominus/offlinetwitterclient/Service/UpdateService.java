package com.nuclominus.offlinetwitterclient.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.nuclominus.offlinetwitterclient.Events.UpdateEvent;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class UpdateService extends Service {
    public static final long NOTIFY_INTERVAL = 60 * 1000; // 60 seconds

    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    private EventBus bus = EventBus.getDefault();
    private UpdateEvent event = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    event = new UpdateEvent();
                    bus.post(event);
                }

            });
        }
    }
}
