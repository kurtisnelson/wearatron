package com.kelsonprime.lockitron.glass;

import android.content.Intent;
import android.os.IBinder;

public class UnlockService extends CommandService {
    public int onStartCommand(Intent intent, int flags, int startId) {
        run(CommandTask.UNLOCK);
        return START_NOT_STICKY;
    }
}
