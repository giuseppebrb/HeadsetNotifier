package io.github.giuseppebrb.headsetnotifier.controller.receivers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.giuseppebrb.headsetnotifier.controller.NotificationSystem;

/**
 * This Receiver checks if an headset (both wired and bluetooth) has been connected, if so starts
 * a new @class ForegroundService.
 */
public class HeadsetStatusReceiver extends BroadcastReceiver {
    private boolean isHeadsetConnected;
    @Override
    public void onReceive(Context context, Intent intent) {
        // If it's a wired headset but this actually works only if app has been opened
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    headsetDisconnected(context);
                    isHeadsetConnected = false;
                    break;
                case 1:
                    headsetConnected(context);
                    isHeadsetConnected = true;
                    break;
                default:
                    break;
            }
        } else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) { // if it's a bluetooth headset
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, 0);
            switch (state) {
                case BluetoothA2dp.STATE_DISCONNECTED:
                    headsetDisconnected(context);
                    isHeadsetConnected = false;
                    break;
                case BluetoothA2dp.STATE_CONNECTED:
                    headsetConnected(context);
                    isHeadsetConnected = true;
                    break;
                default:
                    break;
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
            // If bluetooth turned off and a device was connected, stop service
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            if( state == BluetoothAdapter.STATE_OFF && isHeadsetConnected == true){
                headsetDisconnected(context);
            }
        }
    }

    /**
     * Operation to do when an headset (both bluetooth and wired) has been disconnected.
     * @param context in which operations operate
     */
    private void headsetDisconnected(Context context){
        NotificationSystem.stopServices(context);
    }

    /**
     * Operation to do when an headset (both bluetooth and wired) has been connected.
     * @param context in which operations operate
     */
    private void headsetConnected(Context context){
        NotificationSystem.startServices(context);
    }
}
