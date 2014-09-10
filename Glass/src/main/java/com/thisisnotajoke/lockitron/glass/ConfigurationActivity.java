package com.thisisnotajoke.lockitron.glass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.PreferenceManager;
import com.thisisnotajoke.lockitron.controller.LockListFragment;
import com.thisisnotajoke.lockitron.controller.WearatronActivity;

import org.scribe.model.Token;

import javax.inject.Inject;

public class ConfigurationActivity extends WearatronActivity implements LockListFragment.Callbacks {
    private static final String TAG = "ConfigurationActivity";
    private Token mToken;
    private Lock mLock;

    @Inject
    PreferenceManager mPreferenceManager;

    protected Fragment createFragment() {
        return LockListFragment.newInstance(mToken.getToken(), mLock);
    }

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    protected void setToken(String token) {
        this.mToken = new Token(token, token);
        savePreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        mToken = mPreferenceManager.getToken();
        mLock = mPreferenceManager.getLock();
        if(mToken == null || mToken.isEmpty())
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
        mPreferenceManager.setToken(mToken);
        mPreferenceManager.setLock(mLock);
    }

    @Override
    public void onLockSelected(Lock lock) {
        mLock = lock;
        savePreferences();
        finish();
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}
