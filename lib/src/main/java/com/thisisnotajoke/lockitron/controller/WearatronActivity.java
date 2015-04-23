package com.thisisnotajoke.lockitron.controller;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.widget.Toast;

import com.thisisnotajoke.lockitron.util.InjectionUtils;

import de.greenrobot.event.EventBus;

public class WearatronActivity extends AppCompatActivity {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /* Custom Fonts*/

    /**
     * If a custom font is being used, override to enable it in the action bar
     *
     * @return true if using a custom font
     */
    protected boolean usingCustomFont() {
        return false;
    }
}
