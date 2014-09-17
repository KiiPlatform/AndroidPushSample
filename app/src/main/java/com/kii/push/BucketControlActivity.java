package com.kii.push;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class BucketControlActivity extends FragmentActivity implements OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_controll_list);
        ListView lv = (ListView) findViewById(R.id.bucket_controll_listview);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.OBJECT_SAVE, this)
                    .execute(Constants.PUSH_BUCKET_NAME);
        } else if (pos == 1) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.OBJECT_UPDATE, this)
                    .execute();
        } else if (pos == 2) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.BUCKET_QUERY, this)
                    .execute(Constants.PUSH_BUCKET_NAME);
        } else if (pos == 3) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.BUCKET_DELETE, this)
                    .execute(Constants.PUSH_BUCKET_NAME);
        } else if (pos == 4) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.OBJECT_DELETE, this)
                    .execute();
        } else if (pos == 5) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.BUCKET_ACL_UPDATE, this)
                    .execute();
        } else if (pos == 6) {
            new BucketAsyncTask(BucketAsyncTask.TaskID.OBJECT_ACL_UPDATE, this)
                    .execute();
        }
    }

    public void executeBucketTask(AsyncTask<String, Void, Boolean> bucketTask,
            String bucketName) {
        // New task check
        if (TextUtils.isEmpty(bucketName)) {
            Toast.makeText(this, "Bucket name is empty", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // Task execute
        bucketTask.execute(bucketName, null, null);
    }

}