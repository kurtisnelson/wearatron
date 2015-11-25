package com.thisisnotajoke.wearatron.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.thisisnotajoke.lockitron.controller.SimpleWearatronActivity;
import com.thisisnotajoke.wearatron.GeofenceManager;
import com.thisisnotajoke.wearatron.NotificationDecorator;
import com.thisisnotajoke.wearatron.R;

import javax.inject.Inject;

public class LaunchActivity extends SimpleWearatronActivity {
    private static final int PERMISSIONS_REQUEST_LOCATION = 0;

    @Inject
    GeofenceManager mGeofenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestLocationPermission();
                NotificationDecorator.notify(this, NotificationDecorator.Type.PUSH, getString(R.string.app_name));
            } else {
                NotificationDecorator.notify(this, NotificationDecorator.Type.PUSH, getString(R.string.app_name));
            }
        } else {
            sendNotification();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                sendNotification();
            }
        }
    }

    private void sendNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGeofenceManager.setFenceLocation();
            mGeofenceManager.registerGeofences(this);
        }
        NotificationDecorator.notify(this, NotificationDecorator.Type.PUSH, getString(R.string.app_name));
        finish();
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}