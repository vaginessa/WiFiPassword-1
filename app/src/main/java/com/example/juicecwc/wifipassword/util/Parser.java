package com.example.juicecwc.wifipassword.util;

import android.util.Log;

import com.example.juicecwc.wifipassword.R;
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

    //成功修复中文WiFi名称无法读取问题，bug出在正则表达式上，
    //之前的正则表达式是识别引号内的WiFi名称，但是中文WiFi名称乱码无引号
    //static Pattern WIFI = Pattern.compile("network\\=\\{\\s+ssid\\=\"(.+?)\"(\\s+psk\\=\"(.+?)\")?");
    static Pattern WIFI = Pattern.compile("network=\\{\\s+ssid=(.+?)\\t(psk=\"(.+?)\")?");
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
            String temp_name = matcher.group(1);
            if (temp_name.charAt(0) == '"') {
                int len = temp_name.length();
                temp_name = temp_name.substring(1, len - 1);
                wifi.setName(temp_name);
            }
            else {
                //wifi.setName("Sorry, Cannot get WiFi name =_=");  //R.string.noname
                wifi.setName(convertUTF8ToString(temp_name));
            }
                wifi.setPassword(matcher.group(3));
            list.add(wifi);
        }
        return list;
    }

    //将16进制的UTF-8编码转为对应的汉字，解决中文WiFi名乱码的问题
    private static String convertUTF8ToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }

        try {
            s = s.toUpperCase();

            int total = s.length() / 2;
            int pos = 0;

            byte[] buffer = new byte[total];
            for (int i = 0; i < total; i++) {

                int start = i * 2;

                buffer[i] = (byte) Integer.parseInt(
                        s.substring(start, start + 2), 16);
                pos++;
            }

            return new String(buffer, 0, pos, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
}
