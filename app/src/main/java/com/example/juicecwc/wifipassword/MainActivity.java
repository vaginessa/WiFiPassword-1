package com.example.juicecwc.wifipassword;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juicecwc.wifipassword.entity.WiFi;
import com.example.juicecwc.wifipassword.util.MySuggestionProvider;
import com.example.juicecwc.wifipassword.util.MyWiFiManager;
import com.example.juicecwc.wifipassword.util.Parser;
import com.example.juicecwc.wifipassword.util.Root;
import com.example.juicecwc.wifipassword.util.WiFiAdapter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String filePath = "cat /data/misc/wifi/wpa_supplicant.conf";
    private RecyclerView mRecyclerView;
    private TextView textView;
    private List<WiFi> noPskList;
    private List<WiFi> pskList;
    private List<WiFi> showList;
    private List<WiFi> dataList;
    private WiFi wifi_click;
    private WiFiAdapter mWiFiAdapter;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout refreshLayout; //下拉刷新
    private boolean clear; //清空搜索记录
    private SearchView searchView;
    private String backupParentPath;
    private String backupPath;
    private String out = null; //读出的数据
    private LinearLayoutManager mLayoutManager;
    private MyWiFiManager mMyWiFiManager;
    private WiFi headerWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("TAG", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //沉浸式状态栏
        setStatusStyle();

        mContext = MainActivity.this;

        //备份路径
        backupParentPath = mContext.getExternalFilesDir("Backup").getPath();
        backupPath = backupParentPath + "/wpa_supplicant.conf";

        //下拉刷新
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, //设置动画颜色
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        sharedPreferences = mContext.getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        //mWiFiAdapter = new WiFiAdapter();

        //RecyclerView相关
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        //设置点击动画，似乎并没有效果
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(mRecyclerView);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        textView = (TextView)findViewById(R.id.text);
        //tv_psd = (TextView)findViewById(tv_psd);

        dataList = new ArrayList<>();
        noPskList = new ArrayList<>();
        pskList = new ArrayList<>();
        showList = new ArrayList<>();
        //noNameList = new ArrayList<>();

        mMyWiFiManager = new MyWiFiManager(mContext);
        doWork();//获取WiFi密码列表
        wifiState();//处理WiFi状态
        flush();//刷新
    }

    private void doWork() {
        boolean flag_root = Root.isRoot();
        Log.d("TAG", "check");
        if (flag_root) {
            File tempFile = new File(backupPath);
            Log.d("TAG", "isroot");

            //未备份的情况——第一次使用本软件,进行备份
            if (!tempFile.exists()) {
                out = Backup();
            } else { //已有备份了，直接从备份中读取
                out = ReadBackup();
            }

            //没有WiFi密码信息
            if (out.isEmpty()) {
                //Log.d("TAG", "no wifi");
                textView.setVisibility(View.VISIBLE);
                textView.setText(R.string.nowifi);
            } else {
                //Log.d("TAG", "has wifi");
                //Log.d("TAG", "out:" + out.toString());
                dataList = Parser.getWiFi(out);
                //if (dataList.isEmpty())
                    //Log.d("TAG", "datalist is null");
                //Log.d("TAG", "datalist:" + dataList.toString());
                String headerName = mMyWiFiManager.getSSID();
                int length = headerName.length();
                headerName = headerName.substring(1, length - 1);
                for (WiFi wifi : dataList) {
                    if (wifi.getName().equals(headerName))
                        headerWifi = wifi;
                    if (wifi.getPassword() == null) {
                        noPskList.add(wifi);
                    } /*else if (wifi.getName() == "Sorry, Cannot get WiFi name =_=") {
                    *//*} else if (wifi.getName() == "名称乱码") {*//*
                        noNameList.add(wifi);
                    }*/ else {
                        pskList.add(wifi);
                        Log.d("TAG", wifi.getName());
                    }
                }

                showList = pskList;
                showList.addAll(noPskList);
                mWiFiAdapter = new WiFiAdapter(this, showList);
                mWiFiAdapter.setHeaderWifi(headerWifi);
                mRecyclerView.setAdapter(mWiFiAdapter);

                mWiFiAdapter.setOnItemClickListener(new WiFiAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int data) {
                        mRecyclerView.showContextMenuForChild(view);
                        if (data == -1)
                            wifi_click = headerWifi;
                        else
                            wifi_click = showList.get(data);
                    }
                });

                //listView.setAdapter(mWiFiAdapter);

                //flush();
            }
        }
        else {
            Log.d("TAG", "no root");
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.noroot);
        }

    }

    private void wifiState() {
        Log.d("TAG", mMyWiFiManager.getSSID() + mMyWiFiManager.checkState());
        mWiFiAdapter.showHeader(false);
        //未打开WiFi
        if (mMyWiFiManager.checkState() == 1) {
            Snackbar.make(mRecyclerView, R.string.wificlosed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.openwifi, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mMyWiFiManager.openWiFi();
                        }
                    }).show();
        } else if (mMyWiFiManager.getSSID() != null) {
            mWiFiAdapter.showHeader(true);
        }
    }

    //刷新列表
    public void flush() {

        clear = sharedPreferences.getBoolean("clear", false);
        if (clear == true) {
            clearHistory();
        }

        int sort = sharedPreferences.getInt("sort", 0);
        pskList.clear();
        for (WiFi wiFi : dataList) {
            if (noPskList.contains(wiFi))
                continue;
            pskList.add(wiFi);
        }
        if (sort == 1) {
            Collections.sort(pskList);
            Collections.sort(noPskList);
            //Log.d("TAG", "sort up");
        } else if (sort == 2) {
            Collections.sort(pskList);
            Collections.sort(noPskList);
            Collections.reverse(pskList);
            Collections.reverse(noPskList);
            //Log.d("TAG", "sort down");
        }
        showList = pskList;

        /*boolean flag_show_noname = sharedPreferences.getBoolean("show_noname", false);
        if (flag_show_noname == true)
            showList.addAll(noNameList);*/

        boolean flag_show = sharedPreferences.getBoolean("show", false);
        if (flag_show == true) {
            showList.addAll(noPskList);
            //Log.d("TAG", "show all");
        }

        //mWiFiAdapter.setList(showList);
        //Log.d("TAG", pskList.toString());
        mWiFiAdapter.reset();
        mWiFiAdapter.notifyDataSetChanged(); //只会改变WiFiAdapter里的list的值不会改变list_filter的值
        Log.d("TAG", "flush");
        wifiState();
    }

    //备份
    private String Backup() {
        out = null;
        Process process = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        StringBuffer sBuffer = new StringBuffer();

        File tempDir = new File(backupParentPath);
        if (!tempDir.exists())
            tempDir.mkdir();
        //读取该文件
        try {
            //用来写入SD卡备份文件
            FileOutputStream fileOutputStream = new FileOutputStream(backupPath);

            process = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(process.getOutputStream());
            inputStream = new DataInputStream(process.getInputStream());
            outputStream.writeBytes(filePath + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                fileOutputStream.write((temp + "\n").getBytes());
                sBuffer.append(temp);
            }
            out = sBuffer.toString();
            reader.close();
            fileOutputStream.close();
            inputStream.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    //读取备份数据
    private String ReadBackup() {
        out = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(backupPath);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuffer sBuffer = new StringBuffer();
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                sBuffer.append(temp);
            }
            out = sBuffer.toString();
            reader.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.item_click_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }*/

    //点击后弹出菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ClipboardManager clipboardManager = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = null;
        String temp_password = wifi_click.getPassword();
        String temp_name = wifi_click.getName();

        if (temp_name.isEmpty())
            Log.d("TAG", "empty");
        /*if (temp_password == null)
            Log.d("TAG", "null");*/
        switch (item.getItemId()) {
            case R.id.copy_password:
                if (temp_password == null)
                    Toast.makeText(this, "密码为空，无法复制", Toast.LENGTH_SHORT).show();
                else {
                    clipData = ClipData.newPlainText("password", temp_password);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(this, "密码复制成功", Toast.LENGTH_SHORT).show();
                }
                //Log.d("TAG", temp_password);
                break;
            case R.id.copy_name:
                /*if (temp_name == "Sorry, Cannot get WiFi name =_=")
                *//*if (temp_name == "名称乱码")*//*
                    Toast.makeText(this, "WiFi名称为乱码，无法复制", Toast.LENGTH_SHORT).show();*/
                //else {
                    clipData = ClipData.newPlainText("name", wifi_click.getName());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(this, "名称复制成功", Toast.LENGTH_SHORT).show();
                //}

                break;
            case R.id.copy_all:
                if (temp_password == null)
                    Toast.makeText(this, "密码为空，无法复制", Toast.LENGTH_SHORT).show();
                /*else if (temp_name == "Sorry, Cannot get WiFi name =_=")
                *//*else if (temp_name == "名称乱码")*//*
                    Toast.makeText(this, "WiFi名称为乱码，无法复制", Toast.LENGTH_SHORT).show();*/
                else {
                    clipData = ClipData.newPlainText("all", "名称：" + wifi_click.getName() + "\n"
                            + "密码：" + temp_password);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(this, "名称和密码复制成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //采用searchView作为搜索UI
        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView)MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //这段代码在点回车键之后才执行，不点回车键不会执行
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(mContext, "查找成功，下拉刷新返回", Toast.LENGTH_SHORT).show();
                //隐藏输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                }
                showList = mWiFiAdapter.getList_filter();
                Log.d("TAG", "showlist size: " + showList.size());
                Log.d("TAG", "getCount_out: " + mWiFiAdapter.getItemCount());
                searchView.clearFocus();
                return true;
            }

            //先执行这段代码，在一点搜索图标的时候就执行。每次输入搜索内容也会执行，但是还是没用啊啊啊啊，并不会及时更新showList
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "Changed");

                doSearch(newText);
                mRecyclerView.scrollToPosition(0);
                showList = mWiFiAdapter.getList_filter();
                Log.d("TAG", "showlist size 2: " + showList.size());
                Log.d("TAG", "getCount_out 2: " + mWiFiAdapter.getItemCount());
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(mContext, AboutActivity.class);
                startActivity(intent1);
                break;
            /*case R.id.search: //搜索
                //onSearchRequested();

                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("TAG", "onResume");
        flush();
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        wifiState();
        Log.d("TAG", "onBackPressed");
    }*/

    //沉浸式状态栏
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private void setStatusStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager;
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tintManager.setStatusBarTintResource(R.color.colorPrimaryDark); //状态栏颜色
        else
            tintManager.setStatusBarTintResource(R.color.colorPrimary); //状态栏颜色
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() { //一个定时器
            @Override
            public void run() {
                flush();
                out = Backup();
                refreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    //执行搜索
    private void doSearch(String query) {

        mWiFiAdapter.showHeader(false);//打开搜索之后关闭header
        Log.d("TAG", "doSearch");
            /*Toast.makeText(this,"the query key is " + query,Toast.LENGTH_LONG).show(); //弹出用户输入的关键字，模拟搜索处理*/
        if (query.isEmpty()) {
            //mRecyclerView.clearTextFilter();
            mWiFiAdapter.reset();
        }
        else {
            mWiFiAdapter.getFilter().filter(query);
            Log.d("TAG", "filter(query): " + showList.size());
            /*showList = mWiFiAdapter.getList_filter();
            Log.d("TAG", "showlist size: " + showList.size());
            Log.d("TAG", "getCount_out: " + mWiFiAdapter.getItemCount());*/
        }

        /*//保存搜索记录
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);*/
    }
    //清空搜索记录
    public void clearHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();


        clear = false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("clear", clear);
        editor.commit();
    }
}
