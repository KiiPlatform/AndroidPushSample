package com.kii.push;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.KiiFileBucket;
import com.kii.cloud.storage.KiiSubscribable;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiFileCallBack;
import com.kii.cloud.storage.callback.KiiPushCallBack;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FileBucketPushActivity extends FragmentActivity implements
        OnItemClickListener {

    private static final String FILE_BUCKET_NAME = "testFileBucket";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.filebucketpush);
        ListView lv = (ListView) findViewById(R.id.filebucket_push_list);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) { // File bucket controll
            showListDialog();
        } else if (pos == 1) { // Subscribe file bucket
            KiiFileBucket bucket = Kii.fileBucket(FILE_BUCKET_NAME);
            KiiUser.getCurrentUser().pushSubscription()
                    .subscribe(bucket, new KiiPushCallBack() {
                        @Override
                        public void onSubscribeCompleted(int taskId,
                                KiiSubscribable target, Exception e) {
                            if (e != null) {
                                showAlertDialog(e.getMessage());
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Subscribe succeeded",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void showAlertDialog(String message) {
        DialogFragment newFragment = AlertDialogFragment
                .newInstance(android.R.string.dialog_alert_title, message);
        newFragment.show(this.getSupportFragmentManager(),
                AlertDialogFragment.TAG);
    }

    private void showListDialog() {
        ListDialogFragment frag = ListDialogFragment.newInstance(
                R.layout.file_con_listdialog, R.string.file_bucket_controll,
                android.R.drawable.ic_menu_edit, new OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int pos, long id) {
                        if (pos == 0 ) { // Create file in bucket.
                            KiiFile f = Kii.fileBucket(FILE_BUCKET_NAME).file();
                            f.save(new KiiFileCallBack() {
                                @Override
                                public void onSaveCompleted(int token,
                                        KiiFile file, Exception exception) {
                                    if (exception != null) {
                                        // TODO: impl progress.
                                        showAlertDialog(exception.getMessage());
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Create file suceeded",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            // TODO: implement other menu.
                            Toast.makeText(getApplicationContext(),
                                    "Not implemented yet.", Toast.LENGTH_LONG)
                                    .show();
                        }
                        dismissDialogByTag(ListDialogFragment.TAG);
                    }
                });
        frag.show(this.getSupportFragmentManager(), ListDialogFragment.TAG);
    }

    private void dismissDialogByTag(String tag) {
        DialogFragment df = (DialogFragment) FileBucketPushActivity.this
                .getSupportFragmentManager().findFragmentByTag(
                        ListDialogFragment.TAG);
        df.dismiss();
    }

}
