package com.thisisnotajoke.wearatron.mobile;

import android.content.Context;

import com.google.gson.Gson;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.lockitron.WearatronModule;
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.lockitron.model.LockStore;
import com.thisisnotajoke.lockitron.model.LockitronWebService;
import com.thisisnotajoke.wearatron.mobile.controller.AuthActivity;
import com.thisisnotajoke.wearatron.mobile.controller.LockListFragment;
import com.thisisnotajoke.wearatron.mobile.controller.MainActivity;
import com.thisisnotajoke.wearatron.mobile.controller.MobileListenerService;
import com.thisisnotajoke.wearatron.mobile.model.MobileDataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
                MobileApplication.class,
                //lib
                PreferenceManager.class,
                //activity
                AuthActivity.class,
                MainActivity.class,
                LockListFragment.class,
                //service
                MobileListenerService.class,
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

    @Provides
    DataManager provideDataManager(Context context, PreferenceManager preferenceManager, Gson gson, LockitronWebService webservice, LockStore lockStore) {
        return new MobileDataManager(context, preferenceManager, gson, webservice, lockStore);
    }

    @Provides
    @Singleton
    LockitronWebService provideWebService(RestAdapter restAdapter) {
        return restAdapter.create(LockitronWebService.class);
    }

    @Provides
    @Singleton
    LockStore provideLockStore() {
        return new LockStore();
    }
}
