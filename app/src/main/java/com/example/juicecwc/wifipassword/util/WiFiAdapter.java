package com.example.juicecwc.wifipassword.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juicecwc.wifipassword.R;
import com.example.juicecwc.wifipassword.entity.WiFi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juicecwc on 2016/8/24.
 */
public class WiFiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable, View.OnClickListener {
    private List<WiFi> list = null;
    private List<WiFi> list_filter = null;
    //private LayoutInflater inflater;
    private Context mContext;
    private Filter filter;
    //处理正在连接的WiFi的header
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private boolean mShowHeader = true;
    private WiFi headerWifi;

    private OnRecyclerViewItemClickListener mItemClickListener = null; //声明接口变量

    //定义接口
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int data); //data为传递的数据，这里传递的是位置position
    }

    public WiFiAdapter() {}

    public WiFiAdapter(Context context, List<WiFi> list1) {
        this.list = list1;
        this.list_filter = list1;
        Log.d("TAG", "both changed: list size: " + this.list.size() + " list_filter size: " + this.list_filter.size());
        this.mContext = context;
        //this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        int begin = mShowHeader ? 1 : 0;
        if (list_filter == null)
            return begin;
        else
            return list_filter.size() + begin;
    }

    @Override
    public int getItemViewType(int position) {
        if (!mShowHeader)
            return TYPE_ITEM;
        if (position == 0) //第一行显示header
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int begin = mShowHeader ? 1 : 0;
        position -= begin; //不加这句会溢出
        if (holder instanceof ItemViewHolder) {
            Resources resource = mContext.getResources();
            ColorStateList csl_1 = resource.getColorStateList(R.color.colorGray);
            ColorStateList csl_2 = resource.getColorStateList(R.color.textGray);
            WiFi mWiFi = list_filter.get(position);
            if (mWiFi == null)
                return;
            ((ItemViewHolder)holder).mName.setText(mWiFi.getName());
            if (mWiFi.getPassword() == null) {
                ((ItemViewHolder) holder).mPsd.setText(R.string.nopassword);
                ((ItemViewHolder) holder).mPsd.setTextColor(csl_1);
            }

            else {
                ((ItemViewHolder) holder).mPsd.setText(mWiFi.getPassword());
                ((ItemViewHolder) holder).mPsd.setTextColor(csl_2);
            }
            //设置Tag
            holder.itemView.setTag(holder.getAdapterPosition() - begin);
        } else if (holder instanceof HeaderViewHolder) {
            /*//默认得到的SSID带引号，需进行处理
            String headerName = new MyWiFiManager(mContext).getSSID();
            int length = headerName.length();
            headerName = headerName.substring(1, length - 1);*/

            WiFi mWiFi = headerWifi;
            Resources resource = mContext.getResources();
            ColorStateList csl_1 = resource.getColorStateList(R.color.colorGray);
            ColorStateList csl_2 = resource.getColorStateList(R.color.textGray);
            if (mWiFi == null)
                return;
            ((HeaderViewHolder)holder).mName.setText(mWiFi.getName());
            if (mWiFi.getPassword() == null) {
                ((HeaderViewHolder) holder).mPsd.setText(R.string.nopassword);
                ((HeaderViewHolder) holder).mPsd.setTextColor(csl_1);
            }

            else {
                ((HeaderViewHolder) holder).mPsd.setText(mWiFi.getPassword());
                ((HeaderViewHolder) holder).mPsd.setTextColor(csl_2);
            }
            //设置Tag
            holder.itemView.setTag(-1); //设置tag为-1作为标志
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item, parent, false);
            ItemViewHolder vHolder = new ItemViewHolder(view);
            view.setOnClickListener(this);
            return vHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item, parent, false);
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(view);
            view.setOnClickListener(this);
            return headerViewHolder;
        }
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null)
            mItemClickListener.onItemClick(view, (int)view.getTag());
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public boolean isShowHeader() {
        return this.mShowHeader;
    }

    public void setHeaderWifi(WiFi headerWifi) {
        this.headerWifi = headerWifi;
    }

    public void showHeader(boolean showHeader) {
        this.mShowHeader = showHeader;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {
        public TextView mName;
        public TextView mPsd;

        public ItemViewHolder(final View view) {
            super(view);
            mName = (TextView)view.findViewById(R.id.item_name);
            mPsd = (TextView)view.findViewById(R.id.item_password);
            view.setOnCreateContextMenuListener(this);
            Log.d("TAG", "menu created");
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, R.id.copy_name, Menu.NONE, R.string.copyname);
            contextMenu.add(Menu.NONE, R.id.copy_password, Menu.NONE, R.string.copypassword);
            contextMenu.add(Menu.NONE, R.id.copy_all, Menu.NONE, R.string.copyall);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {
        public TextView mName;
        public TextView mPsd;
        public ImageView mHeader;

        public HeaderViewHolder(final View view) {
            super(view);
            mName = (TextView)view.findViewById(R.id.item_name);
            mPsd = (TextView)view.findViewById(R.id.item_password);
            mHeader = (ImageView)view.findViewById(R.id.wifi);
            mHeader.setVisibility(View.VISIBLE);
            view.setOnCreateContextMenuListener(this);
            Log.d("TAG", "menu created");
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, R.id.copy_name, Menu.NONE, R.string.copyname);
            contextMenu.add(Menu.NONE, R.id.copy_password, Menu.NONE, R.string.copypassword);
            contextMenu.add(Menu.NONE, R.id.copy_all, Menu.NONE, R.string.copyall);
        }
    }

    //搜索
    @Override
    public Filter getFilter() {
        if(filter == null)
            filter = new SearchFilter();
        return filter;
    }

    public int getFilterListSize() {
        return list_filter.size();
    }

    public List<WiFi> getList_filter() {
        Log.d("TAG", "list_filter again: " + list_filter.size());
        return this.list_filter;
    }

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
            list_filter = (ArrayList<WiFi>)filterResults.values;
            Log.d("TAG", "list_filter: " + list_filter.size());
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            list_filter = (ArrayList<WiFi>)filterResults.values;
            Log.d("TAG", "search result num:" + list_filter.size());

            notifyDataSetChanged(); //不会改变list的值，显示的是list_filter的值
        }
    }

    //搜索之后复位
    public void reset() {
        list_filter = list;
        notifyDataSetChanged();
    }
}
