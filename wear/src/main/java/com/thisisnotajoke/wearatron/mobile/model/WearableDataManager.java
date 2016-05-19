package com.thisisnotajoke.mobile.model;

import android.content.Context;
import android.net.Uri;

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
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.lockitron.model.WearDataApi;
import com.thisisnotajoke.mobile.controller.WearableDispatchService;

import org.scribe.model.Token;

import java.util.List;

public class WearableDataManager implements DataManager {

    private final GoogleApiClient mGoogleApiClient;
    private final Gson mGson;
    private final Context mContext;

    public WearableDataManager(Context c, Gson gson) {
        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        mGson = gson;
        mContext = c;
    }

    public Lock getActiveLock() {
        PendingResult<DataItemBuffer> items = Wearable.DataApi.getDataItems(mGoogleApiClient, Uri.fromParts("wear", WearDataApi.LOCK_ITEM_PATH, null));
        DataItem item = items.await().get(0);
        DataMap map = DataMap.fromByteArray(item.getData());
        return mGson.fromJson(map.getString(WearDataApi.LOCK_ITEM_KEY), Lock.class);
    }

    @Override
    public void setActiveLock(Lock lock) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(WearDataApi.LOCK_ITEM_PATH);
        dataMap.getDataMap().putString(WearDataApi.LOCK_ITEM_KEY, mGson.toJson(lock));
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }

    @Override
    public void lockMyLock() {
        mContext.startService(WearableDispatchService.startActionLock(mContext));
    }

    @Override
    public void unlockMyLock() {
        mContext.startService(WearableDispatchService.startActionUnlock(mContext));
    }

    @Override
    public void loadLocks() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Lock> getMyLocks() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Token getToken() {
        throw new RuntimeException("Not implemented");
    }
}
