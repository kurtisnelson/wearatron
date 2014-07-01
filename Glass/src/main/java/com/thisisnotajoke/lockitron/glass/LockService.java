package com.thisisnotajoke.lockitron.glass;

import android.content.Intent;

public class LockService extends CommandService {
    public int onStartCommand(Intent intent, int flags, int startId) {
        run(CommandTask.LOCK);
        return START_NOT_STICKY;
    }
}
