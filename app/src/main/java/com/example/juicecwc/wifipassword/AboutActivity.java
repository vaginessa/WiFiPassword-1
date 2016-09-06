package com.example.juicecwc.wifipassword;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by juicecwc on 2016/8/25.
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_introduction;
    private TextView tv_opensource;
    private TextView tv_feedback;
    private TextView tv_share;
    private TextView tv_rate;
    private TextView tv_donate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //沉浸式状态栏
        setStatusStyle();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindViews();
    }

    private void bindViews() {
        tv_introduction = (TextView)findViewById(R.id.introduction);
        tv_opensource = (TextView)findViewById(R.id.opensource);
        tv_feedback = (TextView)findViewById(R.id.feedback);
        tv_share = (TextView)findViewById(R.id.share);
        tv_rate = (TextView)findViewById(R.id.rate);
        tv_donate = (TextView)findViewById(R.id.donate);

        tv_introduction.setOnClickListener(this);
        tv_opensource.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_rate.setOnClickListener(this);
        tv_donate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.introduction:
                Intent intent1 = new Intent(AboutActivity.this, IntroductionActivity.class);
                startActivity(intent1);
                break;
            case R.id.opensource:
                Intent intent2 = new Intent(AboutActivity.this, OpensourceActivity.class);
                startActivity(intent2);
                break;
            case R.id.feedback:
                try {
                    Intent intent_mail = new Intent(Intent.ACTION_SENDTO);
                    intent_mail.setData(Uri.parse("mailto:" + getString(R.string.mailaddress)));
                    intent_mail.putExtra(Intent.EXTRA_SUBJECT, "WiFi密码查看器反馈");
                    intent_mail.putExtra(Intent.EXTRA_TEXT, "请填写邮件内容");
                    startActivity(intent_mail);
                } catch (Exception e) {
                    Toast.makeText(this, "未找到邮件应用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                // 需要指定意图的数据类型
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "跟你推荐一个可以查看手机Wifi密码的应用~ 下载地址：" + getString(R.string.downloadsite));
                shareIntent = Intent.createChooser(shareIntent, "分享");
                startActivity(shareIntent);
                break;
            case R.id.rate:
                try {
                    String mAddress = "market://details?id=" + getPackageName();
                    Intent marketIntent = new Intent("android.intent.action.VIEW");
                    marketIntent.setData(Uri.parse(mAddress));
                    startActivity(marketIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "无法打开应用市场", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.donate:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                copy();
                AlertDialog alertDialog = builder.setTitle("打赏开发者")
                        .setMessage("本应用完全免费，为了鼓励开发者，您可以通过支付宝进行捐赠。" +
                                "\n帐号 17888841917 已经复制，感谢您的支持!")
                        .setNeutralButton("让开发者吃土去吧", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("打开支付宝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                openAlipay();
                            }
                        }).create();
                alertDialog.show();
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

    //复制支付宝账号
    private void copy() {
        ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText(null, "17888841917"));
        if (manager.hasPrimaryClip()) {
            manager.getPrimaryClip().getItemAt(0).getText();
        }
    }

    //打开支付宝
    private void openAlipay() {
        try{
            Intent intent_alipay = getPackageManager()
                    .getLaunchIntentForPackage("com.eg.android.AlipayGphone");
            startActivity(intent_alipay);
        } catch (Exception e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.eg.android.AlipayGphone")));
            } catch (Exception e1) {
                Toast.makeText(this, "无法打开支付宝", Toast.LENGTH_SHORT).show();
            }
        }
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
