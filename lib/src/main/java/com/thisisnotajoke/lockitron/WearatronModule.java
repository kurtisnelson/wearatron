package com.thisisnotajoke.lockitron;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelsonprime.lockitron.BuildConfig;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.lockitron.model.event.UnauthorizedException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

@Module(
        complete = false,
        library = true
)

public final class WearatronModule {
    private static final String WEB_SERVICE_URL = "https://api.lockitron.com/v2";

    @Provides
    public PreferenceManager providesPreferenceManager(Context c, Gson gson) {
        return new PreferenceManager(c, gson);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint, Gson gson, final PreferenceManager preferenceManager) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.BASIC)
                .setLog(new AndroidLog("Retrofit"))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("User-Agent", "android-retrofit");
                        request.addHeader("Accept", "application/json");
                        String token = preferenceManager.getToken().getToken();
                        if (token != null) {
                            request.addQueryParam("access_token", token);
                        }
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Response r = cause.getResponse();
                        if (r != null && r.getStatus() == 401) {
                            preferenceManager.setToken(null);
                            return new UnauthorizedException(cause);
                        }
                        return cause;
                    }
                })
                .build();
    }
    
        @Provides
        @Singleton
        Endpoint provideEndpoint() {
            return Endpoints.newFixedEndpoint(WEB_SERVICE_URL);
        }
}
