package com.example.juicecwc.wifipassword.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juicecwc.wifipassword.R;
import com.example.juicecwc.wifipassword.entity.WiFi;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by juicecwc on 2016/8/24.
 */
public class WiFiAdapter extends BaseAdapter implements Filterable {
    private List<WiFi> list = null;
    private List<WiFi> list_filter = null;
    private LayoutInflater inflater;
    private Context mContext;
    private Filter filter;

    public WiFiAdapter() {}

    public WiFiAdapter(Context context, List<WiFi> list1) {
        this.list = list1;
        this.list_filter = list1;
        Log.d("TAG", "both changed: list size: " + this.list.size() + " list_filter size: " + this.list_filter.size());
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    /*public void setList(ArrayList<WiFi> list2) {
        this.list = list2;
    }*/

    @Override
    public int getCount() {
        Log.d("TAG", "getCount: " + list_filter.size());
        return list_filter.size();
    }

    @Override
    public Object getItem(int i) {
        Log.d("TAG", "getItem");
        return list_filter.get(i);
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
        /*//名字乱码的情况
        if (list_filter.get(i).getName() == "") {
            viewHolder.wifiName.setText(R.string.noname);
        } else {*/
            viewHolder.wifiName.setText(list_filter.get(i).getName());
        //}
        String temp_password = list_filter.get(i).getPassword();
        //没有密码的情况
        if (temp_password == null) {
            viewHolder.wifiPassword.setText(R.string.nopassword);
            viewHolder.wifiPassword.setTextColor(Color.GRAY);
        } else {
            viewHolder.wifiPassword.setText(temp_password);
            viewHolder.wifiPassword.setTextColor(Color.BLACK);
        }

        Log.d("TAG", "getView: list size: " + this.list.size() + " list_filter size: " + this.list_filter.size());
        return view;
    }

    static class ViewHolder {
        private TextView wifiName;
        private TextView wifiPassword;
    }

    //搜索
    @Override
    public Filter getFilter() {
        if(filter == null)
            filter = new SearchFilter();
        return filter;
    }

    /*public int getFilterListSize() {
        return list_filter.size();
    }*/

    //搜索工具类
    public class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            charSequence = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            if (charSequence != null && charSequence.length() != 0) {
                ArrayList<WiFi> matchData = new ArrayList<>();
                for (int i = 0; i < list.size(); i ++) {
                    if (list.get(i).getName().toLowerCase().contains(charSequence))
                        matchData.add(list.get(i));
                }
                filterResults.count = matchData.size();
                filterResults.values = matchData;
            } else {
                filterResults.count = list.size();
                filterResults.values = list;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            list_filter = (ArrayList<WiFi>)filterResults.values;
            Log.d("TAG", "search");

            notifyDataSetChanged(); //不会改变list的值，显示的是list_filter的值
        }
    }

    //搜索之后复位
    public void reset() {
        list_filter = list;
        notifyDataSetChanged();
    }
}
