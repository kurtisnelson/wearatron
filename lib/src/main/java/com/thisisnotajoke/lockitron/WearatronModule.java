package com.thisisnotajoke.lockitron;

import android.content.Context;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)

public final class WearatronModule {
    @Provides
    public PreferenceManager providesPreferenceManager(Context c, Gson gson) {
        return new PreferenceManager(c, gson);
    }
}
