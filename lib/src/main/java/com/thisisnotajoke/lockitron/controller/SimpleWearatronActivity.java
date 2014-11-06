package com.thisisnotajoke.lockitron.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.thisisnotajoke.lockitron.util.InjectionUtils;

import de.greenrobot.event.EventBus;

public class SimpleWearatronActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (usesInjection()) {
            InjectionUtils.injectClass(this);

            if (registerForEvents()) {
                EventBus.getDefault().register(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usesInjection() && registerForEvents()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void toast(int resId) {
        toast(getString(resId));
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /* Injection */

    // Override this in subclasses in order to turn on injection
    protected boolean usesInjection() {
        return false;
    }

     /* Events */

    // override to be registered for events
    // onEvent() will be required to avoid errors
    protected boolean registerForEvents() {
        return false;
    }
}
