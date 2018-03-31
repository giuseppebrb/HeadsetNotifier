package io.github.giuseppebrb.headsetnotifier.controller;

import android.content.Context;
import android.content.Intent;

import io.github.giuseppebrb.headsetnotifier.Constants;
import io.github.giuseppebrb.headsetnotifier.controller.services.ForegroundService;

/**
 * Defines the operations for the notification sound system and the custom notification of the app itself.
 */

public class NotificationSystem {

    private static Intent foregroundServiceIntent; // Intent for the foreground service
    private static Intent notificationListenerService; // Intent for that service that checks if a new notification is posted

    private static boolean isNotificationSystemActive = false;
    private static boolean isHeadsetConnected = false;

    /**
     * Start the services necessary for the work of the app
     */
    public static void startServices(Context context){
        foregroundServiceIntent = new Intent(context.getApplicationContext(), ForegroundService.class);
        notificationListenerService = new Intent(context.getApplicationContext(), CustomNotificationListenerService.class);

        foregroundServiceIntent.setAction(Constants.START_FOREGROUND);
        context.startService(foregroundServiceIntent);
        context.startService(notificationListenerService);
        isNotificationSystemActive = true;
        isHeadsetConnected = true;
    }

    /**
     * Stop the services started from the app
     */
    public static void stopServices(Context context){
        if(isHeadsetConnected == false)
            return;
        foregroundServiceIntent.setAction(Constants.STOP_FOREGROUND);
        if (ForegroundService.isServiceRunning)
            context.stopService(foregroundServiceIntent);
        if (CustomNotificationListenerService.isServiceRunning)
            context.stopService(notificationListenerService);
        isNotificationSystemActive = false;
    }

    /**
     * Used to know if the notification sound system is running.
     * @return true if it's running, false if has been stopped
     */
    public static boolean isNotificationSystemActive(){
        return isNotificationSystemActive;
    }
}
