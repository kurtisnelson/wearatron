package com.thisisnotajoke.lockitron.model;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.model.event.LockUpdatedEvent;

import org.scribe.model.Token;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DataManager {
    private static final String LOCK_ITEM_PATH = "/selected_lock";
    private static final String LOCK_ITEM_KEY = "LockJson";
    private static final String TAG = "DataManager";
    private final PreferenceManager mPreferenceManager;
    private final Gson mGson;
    private final GoogleApiClient mGoogleApiClient;
    private final LockStore mLockStore;
    private final LockitronWebService mWebService;

    public DataManager(Context c, PreferenceManager preferenceManager, Gson gson, LockitronWebService webService, LockStore lockStore) {
        mGson = gson;
        mPreferenceManager = preferenceManager;

        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        mWebService = webService;
        mLockStore = lockStore;
    }

    public void setActiveLock(Lock lock) {
        if (notOnWear()) {
            mPreferenceManager.setLock(lock);
            mPreferenceManager.requestBackup();
        }
        PutDataMapRequest dataMap = PutDataMapRequest.create(LOCK_ITEM_PATH);
        dataMap.getDataMap().putString(LOCK_ITEM_KEY, toJson(lock));
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }

    public Lock getActiveLock() {
        if (notOnWear())
            return mPreferenceManager.getLock();

        PendingResult<DataItemBuffer> items = Wearable.DataApi.getDataItems(mGoogleApiClient, Uri.fromParts("wear", LOCK_ITEM_PATH, null));
        DataItem item = items.await().get(0);
        DataMap map = DataMap.fromByteArray(item.getData());
        return mGson.fromJson(map.getString(LOCK_ITEM_KEY), Lock.class);
    }

    public List<Lock> getMyLocks() {
        return mLockStore.all();
    }

    public void loadLocks() {
        mWebService.getMyLocks(new Callback<List<Lock>>() {
            @Override
            public void success(List<Lock> locks, Response response) {
                for(Lock lock : locks) {
                    mLockStore.putLock(lock);
                }
                EventBus.getDefault().post(new LockUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Load Locks failed", error);
            }
        });
    }

    public Token getToken() {
        return mPreferenceManager.getToken();
    }

    public void setToken(Token token) {
        mPreferenceManager.setToken(token);
    }

    private String toJson(Object obj) {
        return mGson.toJson(obj);
    }

    private boolean notOnWear() {
        return Build.VERSION.SDK_INT != 20;
    }

    public void lockMyLock() {
        mWebService.lockLock(getActiveLock().getUUID(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Lock lock failed", error);
            }
        });
    }

    public void unlockMyLock() {
        mWebService.unlockLock(getActiveLock().getUUID(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Unlock lock failed", error);
            }
        });
    }
}
