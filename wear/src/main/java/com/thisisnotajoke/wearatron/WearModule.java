package com.thisisnotajoke.wearatron;

import android.content.Context;

import com.google.gson.Gson;
import com.thisisnotajoke.lockitron.WearatronModule;
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.wearatron.controller.LaunchActivity;
import com.thisisnotajoke.wearatron.controller.WearableDispatchService;
import com.thisisnotajoke.wearatron.model.WearableDataManager;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                WearApplication.class,
                //lib
                PreferenceManager.class,
                //activity
                LaunchActivity.class,
                //service
                WearableDispatchService.class
        },
        includes = {
                WearatronModule.class
        }
)

public final class WearModule {
    protected WearApplication mApplication;
    protected Context mContext;

    public WearModule() {
    }

    public WearModule(WearApplication application) {
        mApplication = application;
        mContext = application.getApplicationContext();
    }

    //@Provides
    WearApplication provideApplication() {
        return mApplication;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

//    @Provides
    DataManager provideDataManager(Context context, Gson gson) {
        return new WearableDataManager(context, gson);
    }

    @Provides
    GeofenceManager provideGeofenceManager(Context c, PreferenceManager preferenceManager) {
        return new GeofenceManager(c, preferenceManager);
    }
}
