package com.thisisnotajoke.lockitron.glass;

import android.content.Intent;

import com.thisisnotajoke.lockitron.model.DataManager;

public class LockService extends CommandService {
    protected DataManager mDataManager;

    public int onStartCommand(Intent intent, int flags, int startId) {
        mDataManager.lockMyLock();
        return START_NOT_STICKY;
    }
}
