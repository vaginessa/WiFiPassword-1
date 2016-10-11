package com.example.juicecwc.wifipassword.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by juicecwc on 2016/10/9.
 */

public class MyWiFiManager {
    private WifiManager mWifiManager;
    WifiInfo mWifiInfo;

    public MyWiFiManager(Context context) {
        mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //获取当前连接的WiFi信息
    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    //获取当前连接的WiFi名称
    public String getSSID() {
        return mWifiInfo.getSSID();
    }

    //打开WiFi
    public void openWiFi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public WifiManager getWifiManager() {
        return this.mWifiManager;
    }


}
