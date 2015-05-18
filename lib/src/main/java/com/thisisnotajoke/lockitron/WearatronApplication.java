package com.thisisnotajoke.lockitron;

import android.app.Application;

import com.kelsonprime.lockitron.BuildConfig;
import com.rollbar.android.Rollbar;

import dagger.ObjectGraph;

public abstract class WearatronApplication extends Application {

    private static WearatronApplication instance;

    protected ObjectGraph mObjectGraph;


    public final void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public <T> T get(Class<T> klass) {
        return mObjectGraph.get(klass);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Rollbar.init(this, "93ec4c93e21746d38837141ccbb475a0", "production");
        }

        setupDagger();

        instance = this;
    }


    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(getModule());
    }

    protected abstract Object getModule();

    protected void setupDagger() {
        mObjectGraph = createObjectGraph();
        if (usesInjection()) {
            inject(this);
        }
    }


    protected boolean usesInjection() {
        return true;
    }
}
