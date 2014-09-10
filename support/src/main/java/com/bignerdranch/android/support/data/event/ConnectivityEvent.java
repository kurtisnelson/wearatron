package com.bignerdranch.android.support.data.event;

public class ConnectivityEvent extends BaseEvent {
    private final boolean mNoConnectivity;

    public ConnectivityEvent(boolean noConnectivity) {
        mNoConnectivity = noConnectivity;
    }

    public boolean isConnected() {
        return !mNoConnectivity;
    }
}
