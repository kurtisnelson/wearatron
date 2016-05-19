package com.thisisnotajoke.lockitron;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.thisisnotajoke.wearatron.R;

import dagger.ObjectGraph;

public abstract class WearatronApplication extends Application {

    private static final String TAG = "WearatronApplication";
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
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setDefaults(R.xml.remote_config_defaults);
        remoteConfig.fetch()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            remoteConfig.activateFetched();
                            Log.d(TAG, "Remote config fetched");
                        } else {
                            FirebaseCrash.logcat(Log.ERROR, TAG, "Config fetch failed");
                            FirebaseCrash.report(task.getException());
                        }
                    }
                });
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
