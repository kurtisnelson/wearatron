package com.thisisnotajoke.lockitron.model;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import com.thisisnotajoke.lockitron.model.PreferenceManager;

public class PreferenceManagerBackupAgent extends BackupAgentHelper {
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PreferenceManager.PREF_NAME);
        addHelper(PreferenceManager.BACKUP_KEY, helper);
    }
}
