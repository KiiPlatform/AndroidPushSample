package com.kii.push;

import cn.jpush.android.api.JPushInterface;

import com.kii.cloud.storage.Kii;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainTabActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab_fragment);
        PropertyManager pm = PropertyManager.getInstance();
        pm.load(getApplicationContext());
        PrefWrapper prefs = PrefWrapper.getInstance(this);

        Kii.initialize(pm.getAppId(), pm.getAppKey(), pm.getBaseUri());
        // JPush initialize
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        // Login UFE
        KiiPushAppTask task = new KiiPushAppTask(KiiPushAppTask.MENU_ID.LOGIN,
                "LOGIN", this);
        task.execute(prefs.getUsername(), prefs.getPassword());
        initTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    
    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }
    private void initTabs() {

        FragmentTabHost host = (FragmentTabHost) findViewById(android.R.id.tabhost);
        host.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        host.addTab(host.newTabSpec("GCM").setIndicator("GCM"),
                GCMFragment.class, null);

        host.addTab(host.newTabSpec("JPush").setIndicator("JPush"),
                JPushFragment.class, null);

        host.addTab(host.newTabSpec("Bucket").setIndicator("Bucket"),
                BucketPushFragment.class, null);

        host.addTab(host.newTabSpec("Topic").setIndicator("Topic"),
                TopicPushFragment.class, null);
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
