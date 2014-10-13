package com.thisisnotajoke.lockitron.model;

import com.thisisnotajoke.lockitron.Lock;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface LockitronWebService {
    public static final String ENDPOINT = "https://api.lockitron.com/v1/";

    @POST(API.UNLOCK_LOCK)
    public void unlockLock(@Path("lock_uuid") String lockUuid, Callback<Void> callback);

    @POST(API.LOCK_LOCK)
    public void lockLock(@Path("lock_uuid") String lockUuid, Callback<Void> callback);

    @GET(API.LOCKS)
    public void getMyLocks(Callback<List<Lock>> callback);

    public static class API {
        private static final String PATH_ROOT = "";
        public static final String LOCKS = PATH_ROOT + "/locks";
        public static final String LOCK = LOCKS + "/{lock_uuid}";
        public static final String UNLOCK_LOCK = LOCK + "/unlock";
        public static final String LOCK_LOCK = LOCK + "/lock";
    }
}
