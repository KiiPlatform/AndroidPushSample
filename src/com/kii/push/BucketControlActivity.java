package com.kii.push;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kii.cloud.storage.KiiACL;
import com.kii.cloud.storage.KiiACL.BucketAction;
import com.kii.cloud.storage.KiiACL.ObjectAction;
import com.kii.cloud.storage.KiiACLEntry;
import com.kii.cloud.storage.KiiAnonymousUser;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KiiQuery;

public class BucketControlActivity extends ListActivityCore {

    private Activity mActivity;
    private String[] mControlItems = null;
    private AsyncTask<String, Void, ?> mBucketTask = null;
    private static KiiObject mCurrentObject = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (Activity) this;
        // Set menu
        mControlItems = getResources().getStringArray(R.array.kiipush_control);
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mControlItems));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Get item String from position
        String item = mControlItems[position];

        if (item.equals(getString(R.string.add_object))) {
            executeBucketTask(getBucketObjectSaveTask(),
                    Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.edit_object))) {
            executeBucketTask(getBucketObjectEditTask(),
                    Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.show_bucket_data))) {
            executeBucketTask(getBucketDataShowTask(),
                    Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.delete_bucket))) {
            executeBucketTask(getBucketDeleteTask(), Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.delete_object))) {
             executeBucketTask(getBucketObjectDeleteTask(), Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.update_object_acl))) {
             executeBucketTask(getObjectACLUpdateTask(), Constants.PUSH_BUCKET_NAME);
        } else if (item.equals(getString(R.string.update_bucket_acl))) {
             executeBucketTask(getBucketACLUpdateTask(), Constants.PUSH_BUCKET_NAME);
        }
    }

    public void executeBucketTask(AsyncTask<String, Void, Boolean> bucketTask,
            String bucketName) {
        // New task check
        if (TextUtils.isEmpty(bucketName)) {
            showToastMessage("Bucket name is empty", mActivity);
            return;
        }
        // Exist task check
        if (mBucketTask != null && mBucketTask.getStatus() != Status.FINISHED) {
            showToastMessage("Bucket task is running", mActivity);
            return;
        }
        // Task execute
        mBucketTask = bucketTask;
        openDialog(DIALOG_PROGRESS, "Now Loading...", mActivity);
        mBucketTask.execute(bucketName, null, null);
    }

    public AsyncTask<String, Void, Boolean> getBucketObjectSaveTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiUser user = KiiUser.getCurrentUser();
                    KiiObject object = user.bucket(params[0]).object();
                    object.set("key", "value");
                    object.set("time",
                            String.valueOf(System.currentTimeMillis()));
                    object.set("message", "Bucket data was changed.");
                    object.save();
                    mCurrentObject = object;
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket add/edit success",
                        "Bucket add/edit failed", success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getBucketObjectEditTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiObject object = mCurrentObject;
                    if(object == null)
                        return false;
                    object.set("key_update", "value");
                    object.set("time",
                            String.valueOf(System.currentTimeMillis()));
                    object.set("message", "Bucket data was updated.");
                    object.save();
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket add/edit success",
                        "Bucket add/edit failed", success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getBucketObjectDeleteTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiObject object = mCurrentObject;
                    if(object == null)
                        return false;
                    object.delete();
                    mCurrentObject = null;
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket object delete success",
                        "Bucket object delete failed", success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getObjectACLUpdateTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiObject object = mCurrentObject;
                    if (object == null)
                        return false;
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
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket acl update success",
                        "Bucket acl update failed", success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getBucketACLUpdateTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiBucket bucket = KiiUser.getCurrentUser().bucket(
                            params[0]);
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
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket acl update success",
                        "Bucket acl update failed", success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getBucketDataShowTask() {
        return new AsyncTask<String, Void, Boolean>() {
            private String message = "";

            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    // Query Use Pattern
                    KiiBucket bucket = KiiUser.getCurrentUser().bucket(
                            params[0]);
                    List<KiiObject> objects = null;
                    KiiQuery query = new KiiQuery();
                    query.sortByDesc("time");
                    query.setLimit(1);
                    objects = bucket.query(query).getResult();

                    message = "";
                    for (KiiObject object : objects) {
                        object.refresh();
                        message += expandKiiObjectToString(object);
                    }
                    if (TextUtils.isEmpty(message)) {
                        message = "Bucket not exist OR Bucket Data is empty";
                    }
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "";
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage(message, "Bucket data show task failed",
                        success, mActivity);
            }
        };
    }

    public AsyncTask<String, Void, Boolean> getBucketDeleteTask() {
        return new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = false;
                try {
                    KiiUser user = KiiUser.getCurrentUser();
                    KiiBucket bucket = user.bucket(params[0]);
                    bucket.delete();
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mBucketTask = null;
                closeDialog(DIALOG_PROGRESS, mActivity);
                showToastMessage("Bucket delete success",
                        "Bucket delete failed", success, mActivity);
            }
        };
    }

    public static String expandKiiObjectToString(KiiObject object) {
        Iterator<String> keys = object.keySet().iterator();
        String hkey;
        String hvalue;
        String str = "";
        while (keys.hasNext()) {
            hkey = keys.next();
            hvalue = object.getString(hkey);
            str += hkey + " : " + hvalue + "\n";
        }
        return str;
    }

}