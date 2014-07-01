package com.thisisnotajoke.lockitron.glass;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DoorWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, LockService.class);
            PendingIntent lockIntent = PendingIntent.getService(context, 0, intent, 0);
            Intent intent2 = new Intent(context, UnlockService.class);
            PendingIntent unlockIntent = PendingIntent.getService(context, 0, intent2, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.door_appwidget);
            views.setOnClickPendingIntent(R.id.widget_lock_button, lockIntent);
            views.setOnClickPendingIntent(R.id.widget_unlock_button, lockIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
