package com.kii.push;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiPushCallBack;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.exception.app.ConflictException;


public class GCMFragment extends Fragment implements
        OnItemClickListener {

    private static final String TAG = "GCMFragment";
    GoogleCloudMessaging gcm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gcm_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.gcmListView);
        lv.setOnItemClickListener(this);
        gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
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
            String regId = GCMPreference.getRegistrationId(getActivity().getApplicationContext());
            if(TextUtils.isEmpty(regId)) {
                registerGCM();
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
            unregisterGCM();
        }
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

    private void registerGCM() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    String regId = gcm.register(Constants.GCM_SENDER_ID);
                    KiiUser.pushInstallation().install(regId);
                    GCMPreference.setRegistrationId(getActivity().getApplicationContext(), regId);
                    dismissProgressDialog();
                    return "Registration Successful, Id :" + regId;
                } catch (IOException e) {
                    return "Registration failed, Error : " + e.getMessage();
                } catch (AppException e) {
                    return "Registration failed, Error : " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void unregisterGCM() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                if(TextUtils.isEmpty(GCMPreference.getRegistrationId(getActivity().getApplicationContext())))
                    return "Not registered";
                try {
                    gcm.unregister();
                    GCMPreference.clearRegistrationId(getActivity().getApplicationContext());
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


}
