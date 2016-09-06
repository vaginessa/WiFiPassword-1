package com.example.juicecwc.wifipassword;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juicecwc.wifipassword.entity.WiFi;
import com.example.juicecwc.wifipassword.util.MySuggestionProvider;
import com.example.juicecwc.wifipassword.util.Parser;
import com.example.juicecwc.wifipassword.util.Root;
import com.example.juicecwc.wifipassword.util.WiFiAdapter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String filePath = "cat /data/misc/wifi/wpa_supplicant.conf";
    private ListView listView;
    private TextView textView;
    private List<WiFi> noPskList;
    private List<WiFi> pskList;
    private List<WiFi> showList;
    private List<WiFi> dataList;
    private List<WiFi> noNameList;
    private WiFi wifi_click;
    private WiFiAdapter mWiFiAdapter;
    private Context mContext = MainActivity.this;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout refreshLayout; //下拉刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("TAG", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //沉浸式状态栏
        setStatusStyle();

        mContext = MainActivity.this;

        //下拉刷新
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        /*refreshLayout.post(new Runnable() { //一进入界面就刷新
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        new Handler().postDelayed(new Runnable() { //一个定时器
            @Override
            public void run() {
                flush();
                refreshLayout.setRefreshing(false);
            }
        }, 2000);*/
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, //设置动画颜色
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        sharedPreferences = mContext.getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        mWiFiAdapter = new WiFiAdapter();
        listView = (ListView)findViewById(R.id.list);
        textView = (TextView)findViewById(R.id.text);

        //搜索
        handleIntent(getIntent());
        listView.setTextFilterEnabled(true);

        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //单击呼出菜单
                listView.showContextMenuForChild(view);
                wifi_click = showList.get(i);
            }
        });

        dataList = new ArrayList<>();
        noPskList = new ArrayList<>();
        pskList = new ArrayList<>();
        showList = new ArrayList<>();
        noNameList = new ArrayList<>();
        doWork();

    }

    private void doWork() {
        boolean flag_root = Root.isRoot();
        Log.d("TAG", "check");
        if (flag_root) {
            Log.d("TAG", "isroot");
            Process process = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;
            StringBuffer sBuffer = new StringBuffer();
            String out = null;

            //读取该文件
            try {
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
                    sBuffer.append(temp);
                }
                out = sBuffer.toString();
                reader.close();
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
                for (WiFi wifi : dataList) {
                    if (wifi.getPassword() == null) {
                        noPskList.add(wifi);
                    } else if (wifi.getName() == "Sorry, Cannot get WiFi name =_=") {
                    /*} else if (wifi.getName() == "名称乱码") {*/
                        noNameList.add(wifi);
                    } else {
                        pskList.add(wifi);
                    }
                }

                showList = pskList;
                mWiFiAdapter = new WiFiAdapter(this, showList);
                listView.setAdapter(mWiFiAdapter);

                //flush();
            }
        }
        else {
            Log.d("TAG", "no root");
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.noroot);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.item_click_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

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
                if (temp_name == "Sorry, Cannot get WiFi name =_=")
                /*if (temp_name == "名称乱码")*/
                    Toast.makeText(this, "WiFi名称为乱码，无法复制", Toast.LENGTH_SHORT).show();
                else {
                    clipData = ClipData.newPlainText("name", wifi_click.getName());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(this, "名称复制成功", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.copy_all:
                if (temp_password == null)
                    Toast.makeText(this, "密码为空，无法复制", Toast.LENGTH_SHORT).show();
                else if (temp_name == "Sorry, Cannot get WiFi name =_=")
                /*else if (temp_name == "名称乱码")*/
                    Toast.makeText(this, "WiFi名称为乱码，无法复制", Toast.LENGTH_SHORT).show();
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

        /*// 关联检索配置和SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();*/
        /*searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));*/

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
                onSearchRequested();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void flush() {
        int sort = sharedPreferences.getInt("sort", 0);
        pskList.clear();
        for (WiFi wiFi : dataList) {
            if (noPskList.contains(wiFi) || noNameList.contains(wiFi))
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

        boolean flag_show_noname = sharedPreferences.getBoolean("show_noname", false);
        if (flag_show_noname == true)
            showList.addAll(noNameList);

        boolean flag_show = sharedPreferences.getBoolean("show", false);
        if (flag_show == true) {
            showList.addAll(noPskList);
            //Log.d("TAG", "show all");
        }

        //Log.d("TAG", pskList.toString());
        mWiFiAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("TAG", "onResume");
        flush();
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager;
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary); //状态栏颜色
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() { //一个定时器
            @Override
            public void run() {
                flush();
                refreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    //搜索
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    //搜索
    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY) ; //获取用户输入的关键字
            /*Toast.makeText(this,"the query key is " + query,Toast.LENGTH_LONG).show(); //弹出用户输入的关键字，模拟搜索处理*/
            //listView.setFilterText(query);

            //保存搜索记录
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }

    }


}
