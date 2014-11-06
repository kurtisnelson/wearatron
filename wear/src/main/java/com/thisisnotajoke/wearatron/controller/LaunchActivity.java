package com.thisisnotajoke.wearatron.controller;

import android.os.AsyncTask;
import android.os.Bundle;

import com.thisisnotajoke.lockitron.controller.SimpleWearatronActivity;
import com.thisisnotajoke.wearatron.NotificationDecorator;
import com.thisisnotajoke.wearatron.R;

public class LaunchActivity extends SimpleWearatronActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String name = getString(R.string.lockitron);
                NotificationDecorator.notify(getApplicationContext(), NotificationDecorator.Type.PUSH, name);
                return null;
            }
        }.doInBackground();
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}