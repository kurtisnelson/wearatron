package com.thisisnotajoke.lockitron.glass;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.bignerdranch.android.support.util.InjectionUtils;
import com.thisisnotajoke.lockitron.model.DataManager;

import javax.inject.Inject;


public class CommandService extends Service {
    public static final String PREFS_NAME = "server";
    public static final String PREFS_UUID = "uuid";
    public static final String PREFS_TOKEN = "token";
    private static final String TAG = "CommandService";
    private String lockUUID;
    private String token;

    @Inject
    protected DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        InjectionUtils.injectClass(this);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lockUUID = settings.getString(PREFS_UUID, null);
        token = settings.getString(PREFS_TOKEN, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean hasCredentials() {
        if(lockUUID == null || lockUUID.isEmpty() || token == null || token.isEmpty()){
            Intent i = new Intent(this, ConfigurationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lock", lockUUID);
        editor.putString("token", token);
        editor.commit();
    }
}
