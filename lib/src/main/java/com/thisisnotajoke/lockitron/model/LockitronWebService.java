package com.thisisnotajoke.lockitron.model;

import com.thisisnotajoke.lockitron.Lock;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface LockitronWebService {
    String ENDPOINT = "https://api.lockitron.com/v1/";

    @PUT(API.LOCK)
    void updateLock(@Path("lock_uuid") String lockUuid, @Body LockBody body, Callback<Lock> callback);

    @GET(API.LOCKS)
    void getMyLocks(Callback<List<Lock>> callback);

    public static class API {
        private static final String PATH_ROOT = "";
        public static final String LOCKS = PATH_ROOT + "/locks";
        public static final String LOCK = LOCKS + "/{lock_uuid}";
    }
}
