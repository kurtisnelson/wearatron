package com.thisisnotajoke.lockitron;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)

public final class WearatronModule {
    @Provides
    public PreferenceManager providesPreferenceManager(Context c) {
        return new PreferenceManager(c);
    }
}
