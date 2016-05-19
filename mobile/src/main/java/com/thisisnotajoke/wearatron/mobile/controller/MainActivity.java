package com.thisisnotajoke.wearatron.mobile.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.controller.WearatronActivity;
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.wearatron.mobile.R;

import javax.inject.Inject;

public class MainActivity extends WearatronActivity implements LockListFragment.Callbacks {
    private String mToken;

    private Lock mLock;

    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = mDataManager.getToken().getToken();
        mLock = mDataManager.getActiveLock();
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = createFragment();
        manager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private Fragment createFragment() {
        return LockListFragment.newInstance(mToken, mLock);
    }

    private int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onLockSelected(final Lock lock) {
        mLock = lock;
        mDataManager.setActiveLock(lock);
        Toast.makeText(this, R.string.lock_selected, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}
