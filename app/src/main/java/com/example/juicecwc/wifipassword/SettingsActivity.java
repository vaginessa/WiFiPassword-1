package com.example.juicecwc.wifipassword;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juicecwc.wifipassword.entity.WiFi;
import com.example.juicecwc.wifipassword.util.MySuggestionProvider;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

/**
 * Created by juicecwc on 2016/8/25.
 */
public class SettingsActivity extends BaseActivity {
    private RadioGroup radioGroup;
    private RadioButton btn_up;
    private RadioButton btn_down;
    private CheckBox btn_show;
    //private CheckBox btn_show_noname;
    private TextView tv_clear;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int btn = 0;
    private boolean check = false;
    //private boolean check_name = false;
    private boolean clear = false; //清空历史记录

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //沉浸式状态栏
        setStatusStyle();

        //设置返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = SettingsActivity.this;
        //mContext = getApplicationContext();
        sharedPreferences  = mContext.getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        radioGroup = (RadioGroup)findViewById(R.id.setting_sort);
        btn_show = (CheckBox) findViewById(R.id.setting_show);
        //btn_show_noname = (CheckBox)findViewById(R.id.setting_show_noname);
        tv_clear = (TextView) findViewById(R.id.setting_clear_tv);
        btn_up = (RadioButton)findViewById(R.id.setting_sort_up);
        btn_down = (RadioButton)findViewById(R.id.setting_sort_down);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.setting_sort_up)
                    btn = 1;
                else
                    btn = 2;

            }
        });

        btn_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                check = b;
            }
        });

        /*btn_show_noname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                check_name = b;
            }
        });*/

        //清空搜索记录
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear = true;
                Toast.makeText(mContext, R.string.doneclearhistory, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = sharedPreferences.edit();
        editor.putInt("sort", btn);
        editor.putBoolean("show", check);
        //editor.putBoolean("show_noname", check_name);
        editor.putBoolean("clear", clear);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int choose = sharedPreferences.getInt("sort", 0);
        if (choose == 1)
            btn_up.setChecked(true);
        else if (choose == 2)
            btn_down.setChecked(true);
        btn_show.setChecked(sharedPreferences.getBoolean("show", false));
        //btn_show_noname.setChecked(sharedPreferences.getBoolean("show_noname", false));
        clear = false;
    }

    //返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return true;
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
}
