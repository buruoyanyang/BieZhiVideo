package com.example.biezhi.biezhivideo.HelpClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by biezhi on 2015/12/18.
 */

public class NetInfo {
    public String checkNetWork(Context context)
    {
        String netConnect = "";
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED && wifi == NetworkInfo.State.DISCONNECTED)
        {
            netConnect = "mobile";
        }
        else if (wifi == NetworkInfo.State.CONNECTED && mobile == NetworkInfo.State.DISCONNECTED)
        {
            netConnect = "wifi";
        }
        else
        {
            netConnect = "";
        }
        return netConnect;
    }
}
