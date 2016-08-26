package com.example.juicecwc.wifipassword.entity;

import android.widget.Toast;

/**
 * Created by juicecwc on 2016/8/24.
 */
//WiFi实体类
public class WiFi implements Comparable<WiFi> {
    private String name;
    private String password;

    public WiFi() {}

    public WiFi(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public int compareTo(WiFi wiFi) {
        if (!this.getName().equals(wiFi.getName())) {
            int i = 0;
            while (i < this.getName().length() && i < wiFi.getName().length()) {
                if (trans2lower(this.getName().charAt(i)) != trans2lower(wiFi.getName().charAt(i)))
                    return trans2lower(this.getName().charAt(i)) - trans2lower(wiFi.getName().charAt(i));
                else if (this.getName().charAt(i) != wiFi.getName().charAt(i))
                    break;
                i ++;
                if (i == this.getName().length())
                    return -1;
                if (i == wiFi.getName().length())
                    return 1;
            }
            //默认是字母升序
            return this.getName().charAt(i) - wiFi.getName().charAt(i);
        }
        else
            return 0;
    }

    //将字符转为小写字母
    private char trans2lower(char ch) {
        if (ch >= 'A' && ch <='Z') {
            return (char)(ch - 'A' + 'a');
        }
        return ch;
    }
}
