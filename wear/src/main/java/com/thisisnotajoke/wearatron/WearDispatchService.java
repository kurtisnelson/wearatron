package com.thisisnotajoke.wearatron;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearDispatchService extends WearableListenerService {
    private static final String HINT_PATH = "/hint";
    private static final String TAG = "WearDispatchService";
    private static final int HINT_ID = 0;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message: " + messageEvent.getPath());
        if(messageEvent.getPath().equals(HINT_PATH)){
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if(messageEvent.getData()[0] == 0x1) {
                Intent unlockIntent = new Intent(this, LockitronActivity.class);
                unlockIntent.putExtra(LockitronActivity.DO_EXTRA, false);
                PendingIntent unlockPendingIntent = PendingIntent.getActivity(this, 1, unlockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Intent lockIntent = new Intent(this, LockitronActivity.class);
                lockIntent.putExtra(LockitronActivity.DO_EXTRA, true);
                PendingIntent lockPendingIntent = PendingIntent.getActivity(this, 2, lockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.WearableExtender wearableExtender =
                        new NotificationCompat.WearableExtender();

                Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .addAction(R.drawable.action_unlock, getString(R.string.unlock), unlockPendingIntent)
                        .addAction(R.drawable.action_lock, getString(R.string.lock), lockPendingIntent)
                        .setPriority(Notification.PRIORITY_LOW)
                        .extend(wearableExtender)
                        .build();

                notificationManager.notify(HINT_ID, notification);
            }else {
                notificationManager.cancel(HINT_ID);
            }
        }
    }
}
