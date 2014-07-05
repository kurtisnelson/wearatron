package com.thisisnotajoke.lockitron.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class LockitronActivity extends InsetActivity implements CircleFragment.Callback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "LockitronActivity";
    private GridViewPager mDoorPager;

    @Override
    public void onReadyForContent() {
        setContentView(R.layout.activity_lockitron);
        mDoorPager = (GridViewPager) findViewById(R.id.activity_lockitron_pager);
        mDoorPager.setBackgroundColor(getResources().getColor(R.color.blue));
        mDoorPager.setAdapter(new DoorGridViewPagerAdapter(getFragmentManager()));
    }

    @Override
    public void onClick(boolean lock) {
        new MessageTask(this).execute(lock);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult);
        Intent intent = new Intent(LockitronActivity.this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Please make sure the companion app is setup");
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to play services");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    class DoorGridViewPagerAdapter extends FragmentGridPagerAdapter {

        public DoorGridViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 2;
        }

        @Override
        public Fragment getFragment(int row, int column) {
            CircleFragment f = CircleFragment.newInstance(column % 2 == 0);
            f.setCallback(LockitronActivity.this);
            return f;
        }

        @Override
        protected long getFragmentId(int row, int column) {
            return row * column;
        }
    }
}


class MessageTask extends AsyncTask<Boolean, Void, Boolean> implements GoogleApiClient.OnConnectionFailedListener {
    private static final String ACTION_PATH = "/action";
    private static final String TAG = "MessageTask";
    private GoogleApiClient mClient;
    private final Context mContext;

    public MessageTask(Context c){
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

