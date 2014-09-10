package com.bignerdranch.android.support.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.bignerdranch.android.support.data.event.ConnectivityEvent;
import com.bignerdranch.android.support.data.event.EventHelper;

public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        EventHelper.postEvent(new ConnectivityEvent(noConnectivity));
    }
}
