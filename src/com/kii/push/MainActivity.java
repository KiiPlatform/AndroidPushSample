package com.kii.push;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.kii.cloud.storage.Kii;

public class MainActivity extends FragmentActivity implements OnItemClickListener{
    private static final String TAG = "KiiPush";

    private Activity mActivity;
    private String[] mMenuItems = null;
    private AsyncTask<String, Void, ?> mGcmTask = null;
    private PrefWrapper prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PropertyManager propMan = PropertyManager.getInstance();
        propMan.load(this.getApplicationContext());
        prefs = PrefWrapper.getInstance(this);
        // Activity
        mActivity = (Activity) this;
        // Set Kii instance
        Kii.initialize(propMan.getAppId(), propMan.getAppKey(),
                propMan.getBaseUri());
        // Set broadcast receiver
        registerBroadcastReceiver();
        // Set menu
        mMenuItems = getResources().getStringArray(R.array.kiipush_menu);
        ListView lv = (ListView) findViewById(R.id.mainListView);
        lv.setOnItemClickListener(this);
        // Login UFE
        KiiPushAppTask task = new KiiPushAppTask(KiiPushAppTask.MENU_ID.LOGIN,
                "LOGIN", this);
        task.execute(prefs.getUsername(), prefs.getPassword());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // Get item String from position
        final String item = mMenuItems[position];

        if (item.equals(getString(R.string.install))) {
            GCMRegistrar.checkDevice(this.getApplicationContext());
            final String regId = GCMRegistrar.getRegistrationId(this
                    .getApplicationContext());
            if (TextUtils.isEmpty(regId)) {
                Log.i(TAG, "Not registered to GCM");
                PropertyManager pm = PropertyManager.getInstance(); 
                GCMRegistrar.register(this.getApplicationContext(), pm.getGCMSenderId());
            } else {
                Log.i(TAG, "Registered to GCM, installing to kii cloud");
                new KiiPushAppTask((int)id, item, this).execute(regId);
            }
        } else if (item.equals(getString(R.string.bucket_control))) {
            Intent intent = new Intent(this, BucketControlActivity.class);
            startActivity(intent);
        } else if (item.equals(getString(R.string.group_topic))) {
            Intent intent = new Intent(this, GroupListActivity.class);
            startActivity(intent);
        } else if (item.equals(getString(R.string.sendmessage_to_uscope_topic))) {
            ListDialogFragment.newInstance(R.layout.sendmessage_listdialog,
                    R.string.send_message, android.R.drawable.ic_menu_edit,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int pos, long id) {
                            if (pos == 0) {
                                new KiiPushAppTask(
                                        (int) KiiPushAppTask.MENU_ID.SENDMESSAGE_TO_USCOPE_TOPIC,
                                        item, MainActivity.this).execute();
                            } else if (pos == 1) {
                                // TODO: implement.
                                // Load message by MessageTemplateLoader and send it.
                            } else if (pos == 2) {
                                MessageTemplateLoader
                                        .launchEditor(getApplicationContext());
                            }
                            dismissDialogByTag(ListDialogFragment.TAG);
                        }
                    });
        } else {
            new KiiPushAppTask((int)id, item, this).execute();
        }
    }

    public void dismissDialogByTag(String TAG) {
        DialogFragment df = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG);
        df.dismiss();
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
        case R.id.options_file_bucket_push:
            // TODO: implement tab host.
            Intent i = new Intent(this, FileBucketPushActivity.class);
            startActivity(i);
            return true;
        case R.id.options_exit:
            mActivity = (Activity) this;
            if (!mActivity.isFinishing()) {
                finish();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        // Exist task cancel
        if (mGcmTask != null) {
            mGcmTask.cancel(true);
            // TODO: Kill ListActivityCore! its too bad.
            this.removeDialog(ListActivityCore.DIALOG_PROGRESS);
        }
        // Unregister GCM
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this.getApplicationContext());
        super.onDestroy();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_REGISTERED_GCM.equals(action)) {
                String regId = intent.getExtras().getString(
                        Constants.EXTRA_MESSAGE);
                Toast.makeText(getApplicationContext(),
                        "GCM registration done.\nGoing install to KiiCloud.",
                        Toast.LENGTH_LONG).show();
                new KiiPushAppTask(KiiPushAppTask.MENU_ID.INSTALL_PUSH, regId,
                        MainActivity.this);
            } else if (Constants.ACTION_UNREGISTERED_GCM.equals(action)) {
                Toast.makeText(getApplicationContext(),
                        "GCM unregistration done.", Toast.LENGTH_LONG).show();
            } else if (Constants.ACTION_GCM_ERROR.equals(action)) {
                Toast.makeText(getApplicationContext(),
                        "GCM registration error was happend.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_REGISTERED_GCM);
        filter.addAction(Constants.ACTION_UNREGISTERED_GCM);
        filter.addAction(Constants.ACTION_GCM_ERROR);
        registerReceiver(mHandleMessageReceiver, filter);
    }

}