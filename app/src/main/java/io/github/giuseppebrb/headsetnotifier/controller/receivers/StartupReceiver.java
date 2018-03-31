package io.github.giuseppebrb.headsetnotifier.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.giuseppebrb.headsetnotifier.controller.services.HeadsetListenerService;

/**
 * This receiver checks when the device has been turned on and starts a new instance of @class HeadsetStatusService
 * to check if an headset has been connected.
 */

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Context applicationContext = context.getApplicationContext();
        if (HeadsetListenerService.isHeadsetListenerRunning() == false)
            applicationContext.startService(new Intent(applicationContext, HeadsetListenerService.class));
    }
}
