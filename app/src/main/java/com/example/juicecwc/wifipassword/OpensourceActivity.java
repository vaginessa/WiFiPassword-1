package com.example.juicecwc.wifipassword;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by juicecwc on 2016/8/26.
 */
public class OpensourceActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_1;
    private LinearLayout layout_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout_1 = (LinearLayout)findViewById(R.id.reference_1);
        layout_2 = (LinearLayout)findViewById(R.id.reference_2);
        layout_1.setOnClickListener(this);
        layout_2.setOnClickListener(this);
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
}
