package com.thisisnotajoke.wearatron;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationDecorator {
    public static enum Type {
        HINT,
        PUSH
    };

    private static final int PRIMARY_ID = 0;

    public static void notify(Context context, Type type) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder notification = base(context);

        switch (type) {
            case HINT:
                notification.setPriority(Notification.PRIORITY_LOW);
                break;
            case PUSH:
                notification.setPriority(Notification.PRIORITY_MAX);
                break;
        }
        notificationManager.notify(PRIMARY_ID, notification.build());
    }

    public static void cancel(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(PRIMARY_ID);
    }

    private static NotificationCompat.Builder base(Context context) {
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender();

        wearableExtender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.background));

        PendingIntent unlockPendingIntent = PendingIntent.getService(context, 1, LockitronService.startActionUnlock(context), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent lockPendingIntent = PendingIntent.getService(context, 2, LockitronService.startActionLock(context), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .addAction(R.drawable.button_unlocked_normal, context.getString(R.string.unlock), unlockPendingIntent)
                .addAction(R.drawable.button_locked_normal, context.getString(R.string.lock), lockPendingIntent)
                .extend(wearableExtender);
        return builder;
    }

}
