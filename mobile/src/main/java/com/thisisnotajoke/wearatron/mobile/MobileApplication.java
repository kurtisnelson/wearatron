package com.thisisnotajoke.wearatron.mobile;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thisisnotajoke.lockitron.WearatronApplication;

public class MobileApplication extends WearatronApplication {
    protected MobileModule getModule() {
        return new MobileModule(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);
    }
}
