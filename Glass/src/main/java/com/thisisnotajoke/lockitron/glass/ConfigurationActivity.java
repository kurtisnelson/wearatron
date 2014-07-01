package com.thisisnotajoke.lockitron.glass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thisisnotajoke.lockitron.Lock;

public class ConfigurationActivity extends FragmentActivity implements LockListFragment.Callbacks {
    private static final String TAG = "ConfigurationActivity";
    private String token, uuid;

    protected Fragment createFragment() {
        return LockListFragment.newInstance(token);
    }

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    protected void setToken(String token) {
        this.token = token;
        savePreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        SharedPreferences settings = getSharedPreferences(CommandService.PREFS_NAME, MODE_PRIVATE);
        token = settings.getString(CommandService.PREFS_TOKEN, null);
        uuid = settings.getString(CommandService.PREFS_UUID, null);
        if(token == null || token.isEmpty())
            scan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = createFragment();
        manager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    protected void scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            setToken(scanResult.getContents());
        }
    }

    private void savePreferences(){
        SharedPreferences settings = getSharedPreferences(CommandService.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CommandService.PREFS_TOKEN, token);
        editor.putString(CommandService.PREFS_UUID, uuid);
        editor.commit();
    }

    @Override
    public void onLockSelected(Lock lock) {
        uuid = lock.getUUID();
        savePreferences();
        finish();
    }
}
