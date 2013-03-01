package com.kii.push;

import java.util.List;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiGroup;
import com.kii.cloud.storage.KiiPushMessage;
import com.kii.cloud.storage.KiiPushSubscription;
import com.kii.cloud.storage.KiiSubscribable;
import com.kii.cloud.storage.KiiTopic;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiGroupCallBack;
import com.kii.cloud.storage.callback.KiiPushCallBack;
import com.kii.cloud.storage.callback.KiiTopicCallBack;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.push.EditDialogFragment.EditDialogFragmentCallback;
import com.kii.push.ListDialogFragment.ListDialogFragmentCallback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupListActivity extends FragmentActivity implements
        OnClickListener, OnItemClickListener, ListDialogFragmentCallback,
        EditDialogFragmentCallback {

    private KiiGroup currentTarget;
    private static final String TAG = "GroupListActivity";
    ListView listView;
    Button addButton;
    GroupAdapter groupAdapter;
    List<KiiGroup> groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.grouplist);
        listView = (ListView) findViewById(R.id.groupListView);
        addButton = (Button) findViewById(R.id.groupAddButton);
        groupAdapter = new GroupAdapter(this,
                android.R.layout.simple_list_item_1);
        listView.setAdapter(groupAdapter);
        listView.setOnItemClickListener(this);
        addButton.setOnClickListener(this);
        loadGroups();
    }

    private void showAlertDialog(String message) {
        DialogFragment newFragment = MyAlertDialogFragment
                .newInstance(android.R.string.dialog_alert_title, message);
        newFragment.show(this.getSupportFragmentManager(), "dialog");
    }


    private void showEditDialog(String message, int requestId) {
        DialogFragment newFragment = EditDialogFragment.newInstance(message,
                requestId);
        newFragment.show(this.getSupportFragmentManager(), "editdialog");
    }

    private void showListDialog() {
        ListDialogFragment.newInstance(R.layout.listdailog,
                R.string.group_operation, android.R.drawable.ic_menu_edit, 0)
                .show(this.getSupportFragmentManager(), "listdialog");
    }

    private void showSendMessageListDialog() {
        ListDialogFragment.newInstance(R.layout.sendmessage_listdialog,
                R.string.send_message, android.R.drawable.ic_menu_edit, 1)
                .show(this.getSupportFragmentManager(), "SendMessage");
    }

    private void doSendMessageToTopic(KiiGroup target, KiiPushMessage msg) {
        KiiTopic tp = target.topic(Constants.GROUPTOPIC);
        tp.sendMessage(msg, new KiiTopicCallBack() {

            @Override
            public void onSendMessageCompleted(int taskId, KiiTopic target,
                    KiiPushMessage message, Exception e) {
                if (e != null) {
                    GroupListActivity.this.showAlertDialog(e.getMessage());
                } else {
                    Toast.makeText(GroupListActivity.this,
                            "Send message done.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void doAddUser(final KiiGroup target) {
        Log.v(TAG, "doAddUser");
        this.showEditDialog("Input user name", 0);
    }

    private void doCreateTopic(final KiiGroup target) {
        KiiTopic tp = target.topic(Constants.GROUPTOPIC);
        tp.save(new KiiTopicCallBack() {
            @Override
            public void onSaveCompleted(int taskId, KiiTopic target, Exception e) {
                dismissDialogByTag("listdialog");
                if (e != null) {
                    GroupListActivity.this.showAlertDialog(e.getMessage());
                } else {
                    Toast.makeText(GroupListActivity.this, "Topic created.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void doSubscribeTopic(final KiiGroup target) {
        KiiTopic tp = target.topic(Constants.GROUPTOPIC);
        KiiPushSubscription sub = KiiUser.getCurrentUser().pushSubscription();
        sub.subscribe(tp, new KiiPushCallBack() {
            @Override
            public void onSubscribeCompleted(int taskId,
                    KiiSubscribable target, Exception e) {
                dismissDialogByTag("listdialog");
                if (e != null) {
                    GroupListActivity.this.showAlertDialog(e.getMessage());
                } else {
                    Toast.makeText(GroupListActivity.this, "Subscribe done.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadGroups() {
        KiiUser currentUser = Kii.user();
        if (currentUser == null)
            return;
        currentUser.memberOfGroups(new KiiUserCallBack() {
            @Override
            public void onMemberOfGroupsCompleted(int token, KiiUser user,
                    List<KiiGroup> groupList, Exception exception) {
                GroupListActivity.this
                        .setProgressBarIndeterminateVisibility(false);
                if (exception == null) {
                    groups = groupList;
                    for (KiiGroup g : groups) {
                        groupAdapter.add(g.getGroupName());
                    }
                } else {
                    GroupListActivity.this.showAlertDialog(exception
                            .getMessage());
                }
            }
        });
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title, String message) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");
            String message = getArguments().getString("message");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                    )
                    .create();
        }
    }

    class GroupAdapter extends ArrayAdapter<String> {

        public GroupAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.groupAddButton) {
            this.showEditDialog("Input group name", 1);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        this.currentTarget = groups.get(position);
        this.showListDialog();
    }

    public void dismissDialogByTag(String TAG) {
        DialogFragment df = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG);
        if(df != null)
            df.dismiss();
    }

    @Override
    public void onListDialogItemClicked(AdapterView<?> parent, View view,
            int pos, long id, int requestId) {
            if (requestId == 0) {
                if (pos == 0) {
                    doAddUser(currentTarget);
                } else if (pos == 1) {
                    doCreateTopic(currentTarget);
                } else if (pos == 2) {
                    doSubscribeTopic(currentTarget);
                } else if (pos == 3) {
                    showSendMessageListDialog();
                }
            } else if (requestId ==1) {
                if (pos == 0) {
                    KiiPushMessage.Data data = new KiiPushMessage.Data();
                    data.put("custom-messge",
                            "Hello, group: " + currentTarget.getGroupName());
                    KiiPushMessage msg = KiiPushMessage.buildWith(data)
                            .build();
                    doSendMessageToTopic(currentTarget, msg);
                } else if (pos == 1) {
                    // Load message by MessageTemplateLoader and send.
                    try {
                        KiiPushMessage msg = MessageTemplateLoader
                                .loadMessageFromTemplate();
                        Log.v(TAG, "Messge: "
                                + msg.toJSON().toString(2));
                        doSendMessageToTopic(currentTarget, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (pos == 2) {
                    MessageTemplateLoader
                            .launchEditor(getApplicationContext());
                }
                dismissDialogByTag("SendMessage");
                dismissDialogByTag("listdialog");
            }
    }

    @Override
    public void onEditDone(String input, int requestId) {
        Log.v("onEditDone text: ", input);
        if (requestId == 0) {
            if (!TextUtils.isEmpty(input)) {
                KiiUser.findUserByUserName(input, new KiiUserCallBack() {
                    @Override
                    public void onFindCompleted(int token, KiiUser caller,
                            KiiUser found, Exception exception) {
                        if (found != null) {
                            currentTarget.addUser(found);
                            currentTarget.save(new KiiGroupCallBack() {
                                @Override
                                public void onSaveCompleted(int token,
                                        KiiGroup group, Exception exception) {
                                    dismissDialogByTag("listdialog");
                                    if (exception != null) {
                                        GroupListActivity.this
                                                .showAlertDialog(exception
                                                        .getMessage());
                                    } else {
                                        Toast.makeText(GroupListActivity.this,
                                                "Add user done.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            dismissDialogByTag("listdialog");
                            GroupListActivity.this.showAlertDialog(exception
                                    .getMessage());
                        }
                    }
                });
            } else {
                dismissDialogByTag("listdialog");
            }
        } else if (requestId == 1) {
            DialogFragment df = (DialogFragment) getSupportFragmentManager()
                    .findFragmentByTag("editdialog");
            df.dismiss();

            final KiiGroup gp = Kii.group(input);
            gp.save(new KiiGroupCallBack() {
                @Override
                public void onSaveCompleted(int token, KiiGroup group,
                        Exception exception) {
                    GroupListActivity.this
                            .setProgressBarIndeterminateVisibility(false);
                    if (exception == null) {
                        groups.add(gp);
                        groupAdapter.clear();
                        for (KiiGroup g : groups) {
                            groupAdapter.add(g.getGroupName());
                        }
                    } else {
                        GroupListActivity.this.showAlertDialog(exception
                                .getMessage());
                    }
                }
            });
        }
    }
}
