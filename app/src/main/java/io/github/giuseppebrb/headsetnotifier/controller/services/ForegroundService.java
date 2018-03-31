package io.github.giuseppebrb.headsetnotifier.controller.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import io.github.giuseppebrb.headsetnotifier.Constants;
import io.github.giuseppebrb.headsetnotifier.MainActivity;
import io.github.giuseppebrb.headsetnotifier.R;

/**
 * Service that displays or hide notification when application is running actively.
 */

public class ForegroundService extends Service {
    public static boolean isServiceRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equalsIgnoreCase(Constants.START_FOREGROUND)){
            isServiceRunning = true;
            startForeground(Constants.NOTIFICATION_ID, foregroundNotification(this));
        } else if (intent.getAction().equalsIgnoreCase(Constants.STOP_FOREGROUND)){
            isServiceRunning = false;
            stopForeground(true);
        }
        return START_STICKY;
    }

    /**
     * Create notification to display that the application is running actively.
     * @param context where notification will be displayed
     * @return notification to display
     */
    private Notification foregroundNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.foreground_service_ticker))
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        return notification;
    }
}
