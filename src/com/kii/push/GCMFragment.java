package com.kii.push;
import com.google.android.gcm.GCMRegistrar;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiPushCallBack;
import com.kii.cloud.storage.exception.app.ConflictException;

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
                showProgressDialog();
                GCMRegistrar.register(this.getActivity().getApplicationContext(),
                        Constants.GCM_SENDER_ID);
            } else {
                showProgressDialog();
                KiiUser.pushInstallation().install(regId,
                        new KiiPushCallBack() {
                            @Override
                            public void onInstallCompleted(int taskId,
                                    Exception e) {
                                dismissProgressDialog();
                                StringBuilder b = new StringBuilder();
                                b.append("Installation ");
                                if (e != null) {
                                    if (e instanceof ConflictException) {
                                        b.append("already exist.");
                                    } else {
                                        b.append("failed due to ");
                                        b.append(e.getMessage());
                                    }
                                } else {
                                    b.append("succeeded.");
                                }
                                Toast.makeText(getActivity(), b.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else if (pos == 1) {
            showProgressDialog();
            String regId = GCMRegistrar.getRegistrationId(this.getActivity()
                    .getApplicationContext());
            if (TextUtils.isEmpty(regId)) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), "GCM push has not registered.",
                        Toast.LENGTH_LONG).show();
            }
            // Wait for completion.
            GCMRegistrar.unregister(this.getActivity().getApplicationContext());
        }
    }

    
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_REGISTERED_GCM.equals(action)) {
                String regId = intent.getExtras().getString(
                        Constants.EXTRA_MESSAGE);
                KiiUser.pushInstallation().install(regId,
                        new KiiPushCallBack() {
                            @Override
                            public void onInstallCompleted(int taskId,
                                    Exception e) {
                                dismissProgressDialog();
                                StringBuilder b = new StringBuilder();
                                b.append("Installation ");
                                if (e != null) {
                                    if (e instanceof ConflictException) {
                                        b.append("already exist.");
                                    } else {
                                        b.append("failed due to ");
                                        b.append(e.getMessage());
                                    }
                                } else {
                                    b.append("succeeded.");
                                }
                                Toast.makeText(getActivity(), b.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            } else if (Constants.ACTION_UNREGISTERED_GCM.equals(action)) {
                dismissProgressDialog();
                Toast.makeText(getActivity(),
                        "GCM unregistration done.", Toast.LENGTH_LONG).show();
            } else if (Constants.ACTION_GCM_ERROR.equals(action)) {
                dismissProgressDialog();
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

    void showProgressDialog() {
        ProgressDialogFragment pdf = ProgressDialogFragment.newInstance();
        pdf.show(this.getFragmentManager(),
                ProgressDialogFragment.TAG);
    }

    void dismissProgressDialog() {
        ProgressDialogFragment pdf = (ProgressDialogFragment) this
                .getFragmentManager().findFragmentByTag(
                        ProgressDialogFragment.TAG);
        pdf.dismiss();
    }
}
