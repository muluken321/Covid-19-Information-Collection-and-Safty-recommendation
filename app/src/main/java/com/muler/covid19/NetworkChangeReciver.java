package com.muler.covid19;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReciver extends BroadcastReceiver {
    ConnectionChangeCallback ccc;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activ = cm.getActiveNetworkInfo();
        boolean isConnected =activ!= null && activ.isConnectedOrConnecting();
        if(ccc != null){
            ccc.onConnectionChange(isConnected);
        }
    }
    public void setConnectionChangecallback(ConnectionChangeCallback connectionchangecallback){
        this.ccc = connectionchangecallback;
    }

    interface ConnectionChangeCallback {
        void onConnectionChange(boolean isConnected);

    }
}
