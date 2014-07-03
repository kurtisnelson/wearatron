package com.thisisnotajoke.lockitron;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.SharedPreferences;

import org.scribe.model.Token;

public class PreferenceManager {
    private static final String TOKEN_SECRET = "Secret";
    private static final String TOKEN_TOKEN = "Token";
    private static final String LOCK_UUID = "Lock.UUID";
    protected static final String BACKUP_KEY = "LockitronBackups";
    protected static final String PREF_NAME = "Lockitron";
    private final SharedPreferences prefs;
    private final Context mContext;

    public PreferenceManager(Context context){
        mContext = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void requestBackup() {
        BackupManager bm = new BackupManager(mContext);
        bm.dataChanged();
    }

    public void setToken(Token token){
        prefs
                .edit()
                .putString(TOKEN_SECRET, token.getSecret())
                .putString(TOKEN_TOKEN, token.getToken())
                .commit();
    }

    public Token getToken() {
        String secret = prefs.getString(TOKEN_SECRET, null);
        String token = prefs.getString(TOKEN_TOKEN, null);

        if(secret == null && token == null){
            return null;
        }
        return new Token(token, secret);
    }

    public void setLock(String uuid){
        prefs
                .edit()
                .putString(LOCK_UUID, uuid)
                .commit();
    }

    public String getLock() {
        return prefs.getString(LOCK_UUID, null);
    }
}

