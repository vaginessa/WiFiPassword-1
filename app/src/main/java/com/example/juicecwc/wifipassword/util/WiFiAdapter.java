package com.example.juicecwc.wifipassword.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.juicecwc.wifipassword.R;
import com.example.juicecwc.wifipassword.entity.WiFi;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by juicecwc on 2016/8/24.
 */
public class WiFiAdapter extends BaseAdapter {
    private List<WiFi> list = null;
    private LayoutInflater inflater;
    private Context mContext;

    public WiFiAdapter() {}

    public WiFiAdapter(Context context, List<WiFi> list1) {
        this.list = list1;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.wifiName = (TextView)view.findViewById(R.id.item_name);
            viewHolder.wifiPassword = (TextView)view.findViewById(R.id.item_password);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        //名字乱码的情况
        if (list.get(i).getName() == "") {
            viewHolder.wifiName.setText(R.string.noname);
        } else {
            viewHolder.wifiName.setText(list.get(i).getName());
        }
        String temp_password = list.get(i).getPassword();
        //没有密码的情况
        if (temp_password == null) {
            viewHolder.wifiPassword.setText(R.string.nopassword);
            viewHolder.wifiPassword.setTextColor(Color.GRAY);
        } else {
            viewHolder.wifiPassword.setText(temp_password);
            viewHolder.wifiPassword.setTextColor(Color.BLACK);
        }

        return view;
    }

    static class ViewHolder {
        private TextView wifiName;
        private TextView wifiPassword;
    }
}
