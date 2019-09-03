package com.nyiit.jailinquery.tools;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {
    public static final String TAG = NetworkUtil.class.getName();

    //获取IP地址
    public static String getLocalIpStr(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        LogUtil.d(TAG, "ip:" + wifiInfo.getIpAddress());
        return intToIpAddr(wifiInfo.getIpAddress());
    }

    private static String intToIpAddr(int ip) {
        return (ip & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

}
