package com.bignerdranch.android.support.data.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * AsyncTaskLoader that caches some data.
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {

    protected D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        mData = null;
    }

}