package com.ard.moritanian.androino;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Moritanian on 2017/10/27.
 */

public class Util {
    public static String getIpAdress(Context con){
        WifiManager wm = (WifiManager) con.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
