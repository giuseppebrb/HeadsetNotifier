package io.github.giuseppebrb.headsetnotifier.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import io.github.giuseppebrb.headsetnotifier.R;

/**
 * Handles the behavior when a new notification appears and a bluetooth or wired headset is
 * connected.
 */

public class CustomNotificationListenerService extends NotificationListenerService {

    private SharedPreferences sharedPreferences;
    public static boolean isServiceRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // When a new notification appears in the status bar, check if the application that appeared
        // has been filtered by the user, if so play the notification sound.
        sharedPreferences = getSharedPreferences(getApplicationContext().getString(R.string.app_name), Context.MODE_PRIVATE);
        Map<String, ?> allFilters = sharedPreferences.getAll();
        for (Map.Entry<String, ?> filter : allFilters.entrySet()){
            Gson gson = new Gson();
            String json = sharedPreferences.getString(filter.getKey(), null);
            ArrayList<String> values = gson.fromJson(json, ArrayList.class);
            String packageName = values.get(0); // represents the package name
            if (sbn.getPackageName().equalsIgnoreCase(packageName)){
                // Play the sound only if the notification system has not been disabled
                if(NotificationSystem.isNotificationSystemActive() == true){
                    // values.get(1) represent a resource (i.e. R.raw.solemn)
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Integer.valueOf(values.get(1)));
                    mp.start();
                }
                break;
            }
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }
}
