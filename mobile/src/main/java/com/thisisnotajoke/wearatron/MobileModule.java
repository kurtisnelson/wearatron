package com.thisisnotajoke.wearatron;

import android.content.Context;

import com.thisisnotajoke.lockitron.GeofenceManager;
import com.thisisnotajoke.lockitron.PreferenceManager;
import com.thisisnotajoke.lockitron.WearatronModule;
import com.thisisnotajoke.wearatron.controller.AuthActivity;
import com.thisisnotajoke.wearatron.controller.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MobileApplication.class,
                //lib
                GeofenceManager.class,
                PreferenceManager.class,
                //activity
                AuthActivity.class,
                MainActivity.class,
        },
        includes = {
                WearatronModule.class
        }
)

public final class MobileModule {
    protected MobileApplication mApplication;
    protected Context mContext;

    public MobileModule() {
    }

    public MobileModule(MobileApplication application) {
        mApplication = application;
        mContext = application.getApplicationContext();
    }

    //@Provides
    MobileApplication provideApplication() {
        return mApplication;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
