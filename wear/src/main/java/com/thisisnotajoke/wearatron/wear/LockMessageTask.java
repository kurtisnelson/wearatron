package com.thisisnotajoke.wearatron.wear;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.thisisnotajoke.lockitron.model.WearDataApi;

import java.util.Set;

public class LockMessageTask extends AsyncTask<Boolean, Void, Boolean> implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MessageTask";
    private static final String API_CAPABILITY_NAME = "lockitron_web_api";
    private final Context mContext;

    public LockMessageTask(Context c) {
        mContext = c;
    }

    @Override
    protected Boolean doInBackground(Boolean... command) {
        Log.d(TAG, "connecting...");
        GoogleApiClient mClient = new GoogleApiClient.Builder(mContext)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mClient.blockingConnect();
        Log.d(TAG, "Starting task");
        Set<Node> nodes = Wearable.CapabilityApi.getCapability(
                mClient, API_CAPABILITY_NAME,
                CapabilityApi.FILTER_REACHABLE).await().getCapability().getNodes();
        String nodeId = pickBestNodeId(nodes);
        if (nodeId == null) {
            Log.e(TAG, "No capable node found");
            return false;
        }
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mClient, nodeId, WearDataApi.ACTION_PATH, command[0] ? WearDataApi.ACTION_LOCK_PAYLOAD : WearDataApi.ACTION_UNLOCK_PAYLOAD).await();
        mClient.disconnect();

        if (!result.getStatus().isSuccess()) {
            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
            return false;
        } else {
            Log.d(TAG, "Sent message " + result.getStatus());
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        Intent intent = new Intent(mContext, ConfirmationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (success) {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        } else {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);

        }
        mContext.startActivity(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Could not connect");
        Intent intent = new Intent(mContext, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, connectionResult.toString());
        mContext.startActivity(intent);
        cancel(true);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }
}