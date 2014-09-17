package com.kii.push;

import java.util.List;
import java.util.Set;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.KiiACL.BucketAction;
import com.kii.cloud.storage.KiiACL.ObjectAction;
import com.kii.cloud.storage.query.KiiQuery;

public class BucketAsyncTask extends AsyncTask<String, Void, Void> {

    public static class TaskID {
        public static final int OBJECT_SAVE = 0;
        public static final int OBJECT_UPDATE = 1;
        public static final int OBJECT_DELETE = 2;
        public static final int OBJECT_ACL_UPDATE = 3;
        public static final int BUCKET_QUERY = 4;
        public static final int BUCKET_ACL_UPDATE = 5;
        public static final int BUCKET_DELETE = 6;
    }

    private int taskID;
    private String taskName;
    private Exception e;
    private FragmentActivity activity;
    private static KiiObject mCurrentObject;

    public BucketAsyncTask(int taskID, FragmentActivity activity) {
        this.taskID = taskID;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }

    @Override
    protected Void doInBackground(String... params) {
        switch (this.taskID) {
        case TaskID.OBJECT_SAVE:
            doSaveObject(params[0]);
            break;
        case TaskID.OBJECT_UPDATE:
            doUpdateObject();
            break;
        case TaskID.OBJECT_DELETE:
            doDeleteObject();
            break;
        case TaskID.OBJECT_ACL_UPDATE:
            doUpdateObjectACL();
            break;
        case TaskID.BUCKET_QUERY:
            doBucketQuery(params[0]);
            break;
        case TaskID.BUCKET_ACL_UPDATE:
            doBucketACLUpdate(params[0]);
            break;
        case TaskID.BUCKET_DELETE:
            doDeleteBucket(params[0]);
            break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        dismissProgressDialog();
        if (this.e != null) {
            AlertDialogFragment adf = AlertDialogFragment.newInstance(
                    R.string.operation_failed, e.getMessage());
            adf.show(this.activity.getSupportFragmentManager(),
                    AlertDialogFragment.TAG);
        } else {
            String message = this.taskName + " Done.";
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
    }

    void showProgressDialog() {
        ProgressDialogFragment pdf = ProgressDialogFragment.newInstance();
        pdf.show(this.activity.getSupportFragmentManager(),
                ProgressDialogFragment.TAG);
    }

    void dismissProgressDialog() {
        ProgressDialogFragment pdf = (ProgressDialogFragment) this.activity
                .getSupportFragmentManager().findFragmentByTag(
                        ProgressDialogFragment.TAG);
        pdf.dismiss();
    }

    private void doSaveObject(String bucketName) {
        this.taskName = "Create Object";
        try {
            KiiUser user = KiiUser.getCurrentUser();
            KiiObject object = user.bucket(bucketName).object();
            object.set("key", "value");
            object.set("time", String.valueOf(System.currentTimeMillis()));
            object.set("message", "Bucket data was changed.");
            object.save();
            mCurrentObject = object;
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doUpdateObject() {
        this.taskName = "Update Object";
        try {
            KiiObject object = mCurrentObject;
            if (object == null) {
                this.e = new RuntimeException(
                        "Please create object before this operation.");
                return;
            }
            object.set("key_update", "value");
            object.set("time", String.valueOf(System.currentTimeMillis()));
            object.set("message", "Bucket data was updated.");
            object.save();
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doDeleteObject() {
        this.taskName = "Delete Object";
        try {
            KiiObject object = mCurrentObject;
            if (object == null) {
                this.e = new RuntimeException(
                        "Please create object before this operation.");
                return;
            }
            object.delete();
            mCurrentObject = null;
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doUpdateObjectACL() {
        this.taskName = "Update Object ACL";
        try {
            KiiObject object = mCurrentObject;
            if (object == null) {
                this.e = new RuntimeException(
                        "Please create object before this operation.");
                return;
            }
            KiiACL acl = object.acl();
            Set<KiiACLEntry> set = acl.listACLEntries();

            boolean aclgrant = false;
            for (KiiACLEntry en : set) {
                if (en.getSubject() instanceof KiiAnonymousUser
                        && en.grant() == true
                        && en.getAction() == ObjectAction.READ_EXISTING_OBJECT) {
                    aclgrant = true;
                    break;
                }
            }
            // acl.
            KiiACLEntry entry = null;
            if (aclgrant) {
                entry = new KiiACLEntry(KiiAnonymousUser.create(),
                        ObjectAction.READ_EXISTING_OBJECT, false);
            } else {
                entry = new KiiACLEntry(KiiAnonymousUser.create(),
                        ObjectAction.READ_EXISTING_OBJECT, true);
            }
            acl.putACLEntry(entry);
            acl.save();
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doBucketACLUpdate(String bucketName) {
        this.taskName = "Update Bucket ACL";
        try {
            KiiBucket bucket = KiiUser.getCurrentUser().bucket(bucketName);
            KiiACL acl = bucket.acl();
            Set<KiiACLEntry> set = acl.listACLEntries();

            boolean aclgrant = false;
            for (KiiACLEntry en : set) {
                if (en.getSubject() instanceof KiiAnonymousUser
                        && en.grant() == true
                        && en.getAction() == BucketAction.CREATE_OBJECTS_IN_BUCKET) {
                    aclgrant = true;
                    break;
                }
            }
            // acl.
            KiiACLEntry entry = null;
            if (aclgrant) {
                entry = new KiiACLEntry(KiiAnonymousUser.create(),
                        BucketAction.CREATE_OBJECTS_IN_BUCKET, false);
            } else {
                entry = new KiiACLEntry(KiiAnonymousUser.create(),
                        BucketAction.CREATE_OBJECTS_IN_BUCKET, true);
            }
            acl.putACLEntry(entry);
            acl.save();
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doBucketQuery(String bucketName) {
        this.taskName = "Query Bucket";
        try {
            // Query Use Pattern
            KiiBucket bucket = KiiUser.getCurrentUser().bucket(bucketName);
            List<KiiObject> objects = null;
            KiiQuery query = new KiiQuery();
            query.sortByDesc("time");
            query.setLimit(1);
            objects = bucket.query(query).getResult();

            for (KiiObject object : objects) {
                object.refresh();
            }
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void doDeleteBucket(String bucketName) {
        this.taskName = "Delete Bucket";
        try {
            KiiUser user = KiiUser.getCurrentUser();
            KiiBucket bucket = user.bucket(bucketName);
            bucket.delete();
        } catch (Exception e) {
            this.e = e;
        }
    }

}
