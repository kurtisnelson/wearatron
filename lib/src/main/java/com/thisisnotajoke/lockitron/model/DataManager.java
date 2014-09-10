package com.thisisnotajoke.lockitron.model;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

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
import com.thisisnotajoke.lockitron.PreferenceManager;

import org.scribe.model.Token;

public class DataManager {
    private static final String LOCK_ITEM_PATH = "/selected_lock";
    private static final String LOCK_ITEM_KEY = "LockJson";
    private final PreferenceManager mPreferenceManager;
    private final Gson mGson;
    private final GoogleApiClient mGoogleApiClient;

    public DataManager(Context c, PreferenceManager preferenceManager, Gson gson) {
        mGson = gson;
        mPreferenceManager = preferenceManager;

        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void setLock(Lock lock) {
        if (notOnWear()) {
            mPreferenceManager.setLock(lock);
            mPreferenceManager.requestBackup();
        }
        PutDataMapRequest dataMap = PutDataMapRequest.create(LOCK_ITEM_PATH);
        dataMap.getDataMap().putString(LOCK_ITEM_KEY, toJson(lock));
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }

    public Lock getLock() {
        if (notOnWear())
            return mPreferenceManager.getLock();

        PendingResult<DataItemBuffer> items = Wearable.DataApi.getDataItems(mGoogleApiClient, Uri.fromParts("wear", LOCK_ITEM_PATH, null));
        DataItem item = items.await().get(0);
        DataMap map = DataMap.fromByteArray(item.getData());
        return mGson.fromJson(map.getString(LOCK_ITEM_KEY), Lock.class);
    }

    public Token getToken() {
        return mPreferenceManager.getToken();
    }

    private String toJson(Object obj) {
        return mGson.toJson(obj);
    }

    private boolean notOnWear() {
        return Build.VERSION.SDK_INT != 20;
    }
}
