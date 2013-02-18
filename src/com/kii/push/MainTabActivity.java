package com.kii.push;

import com.kii.cloud.storage.Kii;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab);
        PropertyManager pm = PropertyManager.getInstance();
        pm.load(getApplicationContext());
        PrefWrapper prefs = PrefWrapper.getInstance(this);

        Kii.initialize(pm.getAppId(), pm.getAppKey(), pm.getBaseUri());
        // Login UFE
        KiiPushAppTask task = new KiiPushAppTask(KiiPushAppTask.MENU_ID.LOGIN,
                "LOGIN", this);
        task.execute(prefs.getUsername(), prefs.getPassword());
        initTabs();
    }

    private void initTabs() {

        TabHost host = getTabHost();
        TabHost.TabSpec spec;
        Intent i = new Intent().setClass(getApplicationContext(),
                GCMActivity.class);
        spec = host.newTabSpec("GCM").setIndicator("GCM").setContent(i);
        host.addTab(spec);

        Intent i2 = new Intent().setClass(getApplicationContext(),
                BucketPushActivity.class);
        spec = host.newTabSpec("Bucket").setIndicator("Bucket").setContent(i2);
        host.addTab(spec);

        Intent i3 = new Intent().setClass(getApplicationContext(),
                FileBucketPushActivity.class);
        spec = host.newTabSpec("FileBucket").setIndicator("FileBucket")
                .setContent(i3);
        host.addTab(spec);

        Intent i4 = new Intent().setClass(getApplicationContext(),
                TopicPushActivity.class);
        spec = host.newTabSpec("Topic").setIndicator("Topic")
                .setContent(i4);
        host.addTab(spec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.options_settings:
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        case R.id.options_exit:
            if (!isFinishing()) {
                finish();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
