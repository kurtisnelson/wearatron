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
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class LockitronActivity extends InsetActivity implements CircleFragment.Callback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "LockitronActivity";
    private GridViewPager mDoorPager;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void onReadyForContent() {
        setContentView(R.layout.activity_lockitron);
        mDoorPager = (GridViewPager) findViewById(R.id.activity_lockitron_pager);
        mDoorPager.setBackgroundColor(getResources().getColor(R.color.blue));
        mDoorPager.setAdapter(new DoorGridViewPagerAdapter(getFragmentManager()));
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

    @Override
    public void onClick(boolean lock) {
        new MessageTask(this, mGoogleApiClient).execute(lock);
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
        Log.d(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }
}


class MessageTask extends AsyncTask<Boolean, Void, Boolean> {
    private static final String LOCK_PATH = "/action/lock";
    private static final String UNLOCK_PATH = "/action/unlock";
    private static final String TAG = "MessageTask";
    private final GoogleApiClient mClient;
    private final Context mContext;

    public MessageTask(Context c, GoogleApiClient client){
        mContext = c;
        mClient = client;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "preexecute");
        if(mClient == null || !mClient.isConnected()){
            Log.w(TAG, "Could not start task");
            Intent intent = new Intent(mContext, ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Please make sure the companion app is setup and running");
            mContext.startActivity(intent);
            cancel(true);
        }
    }

    @Override
    protected Boolean doInBackground(Boolean... command) {
        Log.d(TAG, "Starting task");
        String msg;
        if(command[0]){
            msg = LOCK_PATH;
        }else {
            msg = UNLOCK_PATH;
        }
        Log.d(TAG, "Firing message");
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mClient, getNode().getId(), msg, null).await();
        if (result.getStatus().isSuccess()) {
            Log.d(TAG, "Sent message");
            return true;
        } else {
            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
            return false;
        }
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

    private Node getNode() {
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mClient).await();
        return nodes.getNodes().get(0);
    }
}

