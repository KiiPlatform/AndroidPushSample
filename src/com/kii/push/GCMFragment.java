package com.kii.push;
import com.google.android.gcm.GCMRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class GCMFragment extends Fragment implements
        OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gcm_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.gcmListView);
        lv.setOnItemClickListener(this);
        return v;
    }


    private static final String TAG = "GCMActivity";
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        registerBroadcastReceiver();
    }

    
    @Override
    public void onDestroy() {
        this.getActivity().unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this.getActivity().getApplicationContext());
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            GCMRegistrar.checkDevice(this.getActivity().getApplicationContext());
            final String regId = GCMRegistrar.getRegistrationId(this
                    .getActivity().getApplicationContext());
            Log.i(TAG, "regId: " + regId);
            if (TextUtils.isEmpty(regId)) {
                GCMRegistrar.register(this.getActivity().getApplicationContext(),
                        Constants.GCM_SENDER_ID);
            } else {
                new KiiPushAppTask(KiiPushAppTask.MENU_ID.INSTALL_PUSH,
                        getString(R.string.install_push), this.getActivity()).execute(regId);
            }
        } else if (pos == 1) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.UNREGISTER_GCM,
                    getString(R.string.unregister_gcm), this.getActivity())
                    .execute();
        }
    }

    
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_REGISTERED_GCM.equals(action)) {
                String regId = intent.getExtras().getString(
                        Constants.EXTRA_MESSAGE);
                Toast.makeText(getActivity().getApplicationContext(),
                        "GCM registration done.\nGoing install to KiiCloud.",
                        Toast.LENGTH_LONG).show();
                new KiiPushAppTask(KiiPushAppTask.MENU_ID.INSTALL_PUSH, regId,
                        getActivity());
            } else if (Constants.ACTION_UNREGISTERED_GCM.equals(action)) {
                Toast.makeText(getActivity(),
                        "GCM unregistration done.", Toast.LENGTH_LONG).show();
            } else if (Constants.ACTION_GCM_ERROR.equals(action)) {
                Toast.makeText(getActivity(),
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
        this.getActivity().registerReceiver(mHandleMessageReceiver, filter);
    }

}
