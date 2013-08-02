package com.kii.push;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.kii.cloud.storage.exception.app.ConflictException;


public class GCMFragment extends Fragment implements
        OnItemClickListener {

    private static final String TAG = "GCMFragment";
    KiiGCMRegisterer gcm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gcm_list, container, false);
        ListView lv = (ListView) v.findViewById(R.id.gcmListView);
        lv.setOnItemClickListener(this);
        gcm = KiiGCMRegisterer.getInstance(getActivity().getApplicationContext());
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
            if(!gcm.isRegistered()) {
                registerGCM();
            } else {
                showProgressDialog();
                KiiUser.pushInstallation().install(gcm.getRegId(),
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
                    gcm.register(Constants.GCM_SENDER_ID);
                    return "Registration Successful, Id :" + gcm.getRegId();
                } catch (IOException e) {
                    return "Registration failed, Error : " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                KiiUser.pushInstallation().install(gcm.getRegId(),
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
                if(!gcm.isRegistered())
                    return "Not registered";
                try {
                    gcm.unregister();
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
