package io.github.giuseppebrb.headsetnotifier.controller.services;

import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.github.giuseppebrb.headsetnotifier.controller.receivers.HeadsetStatusReceiver;

/**
 * Always running service to check when a bluetooth or wired headset/headphone
 * is connected and if bluetooth has been turned on or off.
 * To be not confused with HeadsetStatusReceiver that is its linked receiver.
 */

public class HeadsetListenerService extends Service {
    private static boolean isHeadsetListenerRunning;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilters = new IntentFilter();
        intentFilters.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilters.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(new HeadsetStatusReceiver(), intentFilters);
        isHeadsetListenerRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isHeadsetListenerRunning = false;
        startService(new Intent(this, HeadsetListenerService.class));
    }

    /**
     * Represents the state of the HeadsetListener service.
     * @return true if service is running, false otherwise.
     */
    public static boolean isHeadsetListenerRunning(){
        return isHeadsetListenerRunning;
    }
}
