package com.thisisnotajoke.lockitron.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.thisisnotajoke.lockitron.util.InjectionUtils;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class WearatronFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (usesInjection()) {
            InjectionUtils.injectClass(getActivity(), this);
        }
        if (registerForEvents()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        if (registerForEvents()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void hideKeyboard() {
        Activity activity = getActivity();
        if (activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void toast(int resId) {
        ((WearatronActivity)getActivity()).toast(resId);
    }

    protected void toast(String message) {
        ((WearatronActivity)getActivity()).toast(message);
    }

    // Override this in subclasses in order to turn on injection
    protected boolean usesInjection() {
        return false;
    }

    // override to be registered for events
    // onEvent() will be required to avoid errors
    protected boolean registerForEvents() {
        return false;
    }
}
