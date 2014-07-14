package com.thisisnotajoke.wearatron;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class LockMessageTask extends AsyncTask<Boolean, Void, Boolean> implements GoogleApiClient.OnConnectionFailedListener {
    private static final String ACTION_PATH = "/action";
    private static final String TAG = "MessageTask";
    private GoogleApiClient mClient;
    private final Context mContext;

    public LockMessageTask(Context c){
        mContext = c;
    }

    @Override
    protected Boolean doInBackground(Boolean... command) {
        Log.d(TAG, "connecting...");
        mClient = new GoogleApiClient.Builder(mContext)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mClient.blockingConnect();
        Log.d(TAG, "Starting task");
        List<Node> nodes = Wearable.NodeApi.getConnectedNodes(mClient).await().getNodes();
        Log.d(TAG, "got nodes");
        for(Node node : nodes) {
            byte[] payload;
            if (command[0]) {
                payload = new byte[]{0x1};
            } else {
                payload = new byte[]{0x0};
            }

            Log.d(TAG, "Firing message to " + node);
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mClient, node.getId(), ACTION_PATH, payload).await();
            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                mClient.disconnect();
                return false;
            }
            Log.d(TAG, "Sent message " + result.getStatus());
        }
        mClient.disconnect();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        Intent intent = new Intent(mContext, ConfirmationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(success){
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        }else{
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
}