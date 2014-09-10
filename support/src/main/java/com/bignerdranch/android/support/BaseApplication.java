package com.bignerdranch.android.support;

import android.app.Application;

import dagger.ObjectGraph;

public abstract class BaseApplication extends Application {

    private final String TAG = AppConstants.APP_TAG + getClass().getSimpleName();

    private static BaseApplication instance;

    protected ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDagger();

        instance = this;
    }

    public final void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public <T> T get(Class<T> klass) {
        return mObjectGraph.get(klass);
    }

    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(getModule());
    }

    protected void setupDagger() {
        mObjectGraph = createObjectGraph();
        if (usesInjection()) {
            inject(this);
        }
    }

    protected abstract Object getModule();

    protected boolean usesInjection() {
        return true;
    }
}
