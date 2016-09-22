package com.example.juicecwc.wifipassword;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by juicecwc on 2016/8/26.
 */
public class OpensourceActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layout_1;
    private LinearLayout layout_2;
    private LinearLayout layout_3;
    private LinearLayout layout_my;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        //沉浸式状态栏
        setStatusStyle();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout_1 = (LinearLayout)findViewById(R.id.reference_1);
        layout_2 = (LinearLayout)findViewById(R.id.reference_2);
        layout_3 = (LinearLayout)findViewById(R.id.reference_3);
        layout_my = (LinearLayout)findViewById(R.id.myproject);
        layout_1.setOnClickListener(this);
        layout_2.setOnClickListener(this);
        layout_3.setOnClickListener(this);
        layout_my.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reference_1:
                Uri uri = Uri.parse("" + getString(R.string.reference1url));
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setData(uri);
                startActivity(intent1);
                break;
            case R.id.reference_2:
                Uri uri2 = Uri.parse("" + getString(R.string.reference2url));
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_VIEW);
                intent2.setData(uri2);
                startActivity(intent2);
                break;
            case R.id.reference_3:
                Uri uri3 = Uri.parse("" + getString(R.string.reference3url));
                Intent intent3 = new Intent();
                intent3.setAction(Intent.ACTION_VIEW);
                intent3.setData(uri3);
                startActivity(intent3);
                break;
            case R.id.myproject:
                Uri uri_my = Uri.parse("" + getString(R.string.myprojecturl));
                Intent intent_my = new Intent();
                intent_my.setAction(Intent.ACTION_VIEW);
                intent_my.setData(uri_my);
                startActivity(intent_my);
                break;
        }
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
}
