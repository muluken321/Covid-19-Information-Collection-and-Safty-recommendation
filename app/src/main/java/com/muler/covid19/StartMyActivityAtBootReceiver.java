package com.muler.covid19;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class StartMyActivityAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Intent intentt = new Intent(context,MyService.class);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, GSignIn.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
            context.startService(new Intent(context, BackGroundMotion.class));
            context.startService(new Intent(context, BluetoothDestance.class));
            Intent myIntent = new Intent(context, SpedDistanceService.class);
            context.startService(myIntent);
        }

    }
}
