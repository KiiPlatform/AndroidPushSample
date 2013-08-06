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
                installPush(regId);
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
                StringBuilder b = new StringBuilder("Registration ");
                try {
                    String regId = gcm.register(Constants.GCM_SENDER_ID);
                    GCMPreference.setRegistrationId(getActivity().getApplicationContext(), regId);
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
                if(msg.startsWith("Registration successful")) {
                    installPush(GCMPreference.getRegistrationId(getActivity()
                            .getApplicationContext()));
                }
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
                if (TextUtils.isEmpty(GCMPreference
                        .getRegistrationId(getActivity()
                                .getApplicationContext())))
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

    private void installPush(final String regId) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder b = new StringBuilder("Installation ");
                try {
                    KiiUser.pushInstallation().install(regId);
                    b.append("succeeded.");
                } catch (ConflictException e) {
                    b.append("already exist.");
                } catch (IOException e) {
                    b.append("failed due to ");
                    b.append(e.getMessage());
                } catch (AppException e) {
                    b.append("failed due to ");
                    b.append(e.getMessage());
                }
                return b.toString();
            }

            @Override
            protected void onPostExecute(String msg) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

}
