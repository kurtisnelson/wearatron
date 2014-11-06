package com.thisisnotajoke.wearatron.controller;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import com.thisisnotajoke.wearatron.LockMessageTask;

public class WearableDispatchService extends IntentService {
    private static final String ACTION_LOCK = "com.thisisnotajoke.wearatron.action.LOCK";
    private static final String ACTION_UNLOCK = "com.thisisnotajoke.wearatron.action.UNLOCK";


    public static Intent startActionLock(Context context) {
        Intent intent = new Intent(context, WearableDispatchService.class);
        intent.setAction(ACTION_LOCK);
        return intent;
    }

    public static Intent startActionUnlock(Context context) {
        Intent intent = new Intent(context, WearableDispatchService.class);
        intent.setAction(ACTION_UNLOCK);
        return intent;
    }

    public WearableDispatchService() {
        super("LockitronService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCK.equals(action)) {
                new LockMessageTask(this).execute(true);
            } else if (ACTION_UNLOCK.equals(action)) {
                new LockMessageTask(this).execute(false);
            }
        }
    }

    public static PendingIntent getUnlockPendingIntent(Context context) {
        return PendingIntent.getService(context, 1, WearableDispatchService.startActionUnlock(context), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PendingIntent getLockPendingIntent(Context context) {
        return PendingIntent.getService(context, 2, WearableDispatchService.startActionLock(context), PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
