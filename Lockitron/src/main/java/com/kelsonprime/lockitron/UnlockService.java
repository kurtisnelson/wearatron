package com.kelsonprime.lockitron;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class UnlockService extends Service {
    public int onStartCommand(Intent intent, int flags, int startId) {
        new CommandTask(this.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "unlock");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
