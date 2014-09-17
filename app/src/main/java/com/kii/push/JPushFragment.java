package com.kii.push;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.kii.cloud.storage.KiiPushInstallation.PushBackend;

public class JPushFragment extends PushInstallationBaseFragment {

    private static final String TAG = "JPushFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.jpush_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.jpushListView);
        lv.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            registerPush();
        } else if (pos == 1) {
            operatePushInstallation(InstallationType.INSTALL);
        } else if (pos == 2) {
            operatePushInstallation(InstallationType.UNINSTALL);
        } else if (pos == 3) {
            unregisterPush();
        }
    }

    @Override
    public PushBackend getPushBackEnd() {
        return PushBackend.JPUSH;
    }

    @Override
    public void registerPush() {
        JPushInterface.resumePush(getActivity().getApplicationContext());
        String regId = getStoredRegistrationID();
        if (TextUtils.isEmpty(regId)) {
            regId = JPushInterface.getUdid(getActivity()
                    .getApplicationContext());
            JPushPreference.setRegistrationId(getActivity()
                    .getApplicationContext(), regId);
        }
        Log.i(TAG, "JPush Registration ID : " + regId);
        JPushInterface.setAlias(getActivity().getApplicationContext(), regId,
                null);
        Toast.makeText(getActivity(), "Registered : " + regId,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void unregisterPush() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                if (TextUtils.isEmpty(JPushPreference
                        .getRegistrationId(getActivity()
                                .getApplicationContext()))) {
                    return "Not registered";
                }
                JPushPreference.clearRegistrationId(getActivity()
                        .getApplicationContext());
                return "Unregister Successful";
            }

            @Override
            protected void onPostExecute(String msg) {
                JPushInterface.stopPush(getActivity().getApplicationContext());
                dismissProgressDialog();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    @Override
    public String getStoredRegistrationID() {
        return JPushPreference.getRegistrationId(getActivity()
                .getApplicationContext());
    }
}
