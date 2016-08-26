package com.example.juicecwc.wifipassword;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.juicecwc.wifipassword.entity.WiFi;

import java.util.List;

/**
 * Created by juicecwc on 2016/8/25.
 */
public class SettingsActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton btn_up;
    private RadioButton btn_down;
    private CheckBox btn_show;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int btn = 0;
    private boolean check = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //设置返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = SettingsActivity.this;
        //mContext = getApplicationContext();
        sharedPreferences  = mContext.getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        radioGroup = (RadioGroup)findViewById(R.id.setting_sort);
        btn_show = (CheckBox) findViewById(R.id.setting_show);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = sharedPreferences.edit();
        editor.putInt("sort", btn);
        editor.putBoolean("show", check);
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
}
