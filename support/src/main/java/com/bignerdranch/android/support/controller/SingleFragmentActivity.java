package com.bignerdranch.android.support.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.bignerdranch.android.support.R;

public abstract class SingleFragmentActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_single_fragment_fragment_container);

        if (fragment == null) {
            fragment = getFragment();
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.activity_single_fragment_fragment_container, fragment)
                        .commit();
            }
        }
    }

    protected abstract Fragment getFragment();
}