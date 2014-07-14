package com.thisisnotajoke.wearatron;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class LockitronService extends IntentService {
    private static final String ACTION_LOCK = "com.thisisnotajoke.wearatron.action.LOCK";
    private static final String ACTION_UNLOCK = "com.thisisnotajoke.wearatron.action.UNLOCK";


    public static Intent startActionLock(Context context) {
        Intent intent = new Intent(context, LockitronService.class);
        intent.setAction(ACTION_LOCK);
        return intent;
    }

    public static Intent startActionUnlock(Context context) {
        Intent intent = new Intent(context, LockitronService.class);
        intent.setAction(ACTION_UNLOCK);
        return intent;
    }

    public LockitronService() {
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
}
