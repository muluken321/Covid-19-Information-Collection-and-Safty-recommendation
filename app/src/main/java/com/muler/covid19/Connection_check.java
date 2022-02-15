package com.muler.covid19;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection_check {
    private Context ctx;

    public Connection_check(Context context) {
        this.ctx = context;
    }
    ///////////////////////////////////////////////////////////////////////////
    public boolean isConnectedToInternet() {
        ConnectivityManager connmanager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connmanager != null) {
            NetworkInfo[] info = connmanager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }}
            return false;
        }




/////////////////////////////////////////////////////////////
}