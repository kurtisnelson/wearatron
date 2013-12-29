package com.kelsonprime.lockitron;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.android.volley.RequestQueue;

public class UnlockService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf(startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
