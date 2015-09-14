package com.thisisnotajoke.lockitron;

import android.app.Application;

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
