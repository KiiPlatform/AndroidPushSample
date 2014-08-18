package com.kii.push;

import java.io.IOException;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.kii.cloud.storage.KiiPushInstallation.PushBackend;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.exception.app.ConflictException;

public abstract class PushInstallationBaseFragment extends Fragment implements
        OnItemClickListener {

    public String TAG = "PushInstallationBaseFragment";

    void showProgressDialog() {
        ProgressDialogFragment pdf = ProgressDialogFragment.newInstance();
        pdf.show(this.getFragmentManager(), ProgressDialogFragment.TAG);
    }

    void dismissProgressDialog() {
        ProgressDialogFragment pdf = (ProgressDialogFragment) this
                .getFragmentManager().findFragmentByTag(
                        ProgressDialogFragment.TAG);
        pdf.dismiss();
    }

    void operatePushInstallation(final InstallationType type) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder b = new StringBuilder();

                String regId = getStoredRegistrationID();
                if (TextUtils.isEmpty(regId)) {
                    b.append("Please register " + getPushBackEnd().toString()
                            + " first.");
                    return b.toString();
                }

                try {
                    switch (type) {
                    case INSTALL:
                        b.append("Installation ");
                        KiiUser.pushInstallation(getPushBackEnd()).install(
                                regId);
                        break;
                    case UNINSTALL:
                        b.append("Uninstallation ");
                        KiiUser.pushInstallation(getPushBackEnd()).uninstall(
                                regId);
                    default:
                        break;
                    }
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

    enum InstallationType {
        INSTALL, UNINSTALL
    }

    public abstract PushBackend getPushBackEnd();

    public abstract String getStoredRegistrationID();

    public abstract void registerPush();

    public abstract void unregisterPush();
}
