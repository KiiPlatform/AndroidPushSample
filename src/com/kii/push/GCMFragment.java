package com.kii.push;

import java.io.IOException;

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

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.KiiPushInstallation.PushBackend;

public class GCMFragment extends PushInstallationBaseFragment {

    private static final String TAG = "GCMFragment";
    GoogleCloudMessaging gcm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gcm_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.gcmListView);
        lv.setOnItemClickListener(this);
        gcm = GoogleCloudMessaging.getInstance(getActivity()
                .getApplicationContext());
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
    public void registerPush() {
        String regId = GCMPreference.getRegistrationId(getActivity()
                .getApplicationContext());
        if (!TextUtils.isEmpty(regId)) {
            Toast.makeText(getActivity(), "Already registered : " + regId,
                    Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder b = new StringBuilder("Registration ");
                try {
                    String regId = gcm.register(Constants.GCM_SENDER_ID);
                    GCMPreference.setRegistrationId(getActivity()
                            .getApplicationContext(), regId);
                    Log.i(TAG, "GCM Registration ID : " + regId);
                    dismissProgressDialog();
                    b.append("successful, Id :" + regId);
                } catch (IOException e) {
                    b.append("failed, Error : " + e.getMessage());
                }
                return b.toString();
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
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
                if (TextUtils.isEmpty(GCMPreference
                        .getRegistrationId(getActivity()
                                .getApplicationContext())))
                    return "Not registered";
                try {
                    gcm.unregister();
                    GCMPreference.clearRegistrationId(getActivity()
                            .getApplicationContext());
                    return "Unregister Successful";
                } catch (IOException e) {
                    return "Unregister failed, Error : " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    @Override
    public PushBackend getPushBackEnd() {
        return PushBackend.GCM;
    }

    @Override
    public String getStoredRegistrationID() {
        return GCMPreference.getRegistrationId(getActivity()
                .getApplicationContext());
    }

}
