package com.thisisnotajoke.wearatron;

import com.crashlytics.android.Crashlytics;
import com.thisisnotajoke.lockitron.WearatronApplication;

import io.fabric.sdk.android.Fabric;

public class MobileApplication extends WearatronApplication {
    protected MobileModule getModule() {
        return new MobileModule(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!com.kelsonprime.lockitron.BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
