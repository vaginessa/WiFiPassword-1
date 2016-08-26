package com.example.juicecwc.wifipassword.util;

import com.example.juicecwc.wifipassword.entity.WiFi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by juicecwc on 2016/8/24.
 */
public class Parser {
    static Pattern WIFI = Pattern.compile("network\\=\\{\\s+ssid\\=\"(.+?)\"(\\s+psk\\=\"(.+?)\")?");

    public static List<WiFi> getWiFi(String wpaString) {
        Matcher matcher = null;
        try {
            //若编码出错，可能是这里的问题
            matcher = WIFI.matcher(new String(wpaString.getBytes("UTF-8"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<WiFi> list = new ArrayList<>();
        while (matcher.find()) {
            WiFi wifi = new WiFi();
            wifi.setName(matcher.group(1));
            wifi.setPassword(matcher.group(3));
            list.add(wifi);
        }
        return list;
    }
}
