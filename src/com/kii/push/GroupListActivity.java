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
        OnClickListener, OnItemClickListener{

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


    private void showEditDialog(String message,
            EditDialogFragment.OnEditDone callback) {
        DialogFragment newFragment = EditDialogFragment.newInstance(message,
                callback);
        newFragment.show(this.getSupportFragmentManager(), "editdialog");
    }

    private void showListDialog(KiiGroup target) {
        DialogFragment newFragment = ListDialogFragment
                .newInstance(target);
        newFragment.show(this.getSupportFragmentManager(), "listdialog");
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

    public static class EditDialogFragment extends DialogFragment {

        interface OnEditDone {
            public void onEditDone(String input);
        }

        EditText editText;
        OnEditDone callback;

        public static EditDialogFragment newInstance(String message, OnEditDone callback) {
            EditDialogFragment frag = new EditDialogFragment();
            frag.callback = callback;
            Bundle args = new Bundle();
            args.putString("message", message);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String message = getArguments().getString("message");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            editText = (EditText) inflater
                    .inflate(R.layout.editdialog, null);

            return new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle(R.string.edit_text)
                    .setMessage(message)
                    .setView(editText)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    String input = editText.getText()
                                            .toString();
                                    if (callback != null) {
                                        callback.onEditDone(input);
                                    }
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    EditDialogFragment.this.dismiss();
                                }
                            }).create();
        }
    }

    public static class ListDialogFragment extends DialogFragment implements
            OnItemClickListener {

        ListView listView;
        KiiGroup target;
        public static ListDialogFragment newInstance(KiiGroup target) {
            ListDialogFragment frag = new ListDialogFragment();
            frag.target = target;
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            listView = (ListView) inflater
                    .inflate(R.layout.listdailog, null);
            listView.setOnItemClickListener(this);

            return new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle(R.string.group_operation)
                    .setView(listView).create();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (position == 0) {
                doAddUser();
            } else if (position == 1) {
                doCreateTopic();
            } else if (position == 2) {
                doSubscribeTopic();
            } else if (position == 3) {
                SendMessageDialogFragment
                        .newInstance(R.layout.sendmessage_listdialog,
                                R.string.send_message,
                                android.R.drawable.ic_menu_edit,
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> parent, View view,
                                            int pos, long id) {
                                        if (pos == 0) {
                                            KiiPushMessage.Data data = new KiiPushMessage.Data();
                                            data.put(
                                                    "From "
                                                            + target.getGroupName(),
                                                    Constants.GROUPTOPIC_MESSAGE
                                                            + " From "
                                                            + Kii.user()
                                                                    .getUsername());
                                            KiiPushMessage msg = KiiPushMessage
                                                    .buildWith(data).build();
                                            doSendMessageToTopic(msg);
                                        } else if (pos == 1) {
                                            // Load message by MessageTemplateLoader and send.
                                            try {
                                                KiiPushMessage msg = MessageTemplateLoader
                                                        .loadMessageFromTemplate();
                                                Log.v(TAG, "Messge: "+msg.toJSON().toString(2));
                                                doSendMessageToTopic(msg);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else if (pos == 2) {
                                            MessageTemplateLoader
                                                    .launchEditor(((GroupListActivity) getActivity())
                                                            .getApplicationContext());
                                        }
                                        ((GroupListActivity) getActivity())
                                                .dismissDialogByTag("SendMessage");
                                    }
                                })
                        .show(((GroupListActivity) getActivity())
                                .getSupportFragmentManager(),
                                "SendMessage");
            }
        }

        public static class SendMessageDialogFragment extends DialogFragment {

            public static final String TAG = "ListDialogFragment";
            ListView listView;
            OnItemClickListener listener; 

            public static SendMessageDialogFragment newInstance(int listViewLayoutId,
                    int titleResId, int iconResId, OnItemClickListener listener) {
                SendMessageDialogFragment frag = new SendMessageDialogFragment();
                Bundle b = new Bundle();
                b.putInt("layoutId", listViewLayoutId);
                b.putInt("titleResId", titleResId);
                b.putInt("iconResId", iconResId);
                frag.setArguments(b);
                frag.listener = listener;
                return frag;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                int layoutId = getArguments().getInt("layoutId");
                int titleResId = getArguments().getInt("titleResId");
                int iconResId = getArguments().getInt("iconResId");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                listView = (ListView) inflater
                        .inflate(layoutId, null);
                listView.setOnItemClickListener(this.listener);

                return new AlertDialog.Builder(getActivity())
                        .setIcon(iconResId)
                        .setTitle(titleResId)
                        .setView(listView).create();
            }

        }



        private void doAddUser() {
            Log.v(TAG, "doAddUser");
            GroupListActivity act = (GroupListActivity) getActivity();
            act.showEditDialog("Input user name",
                new EditDialogFragment.OnEditDone() {
                    @Override
                    public void onEditDone(String input) {
                        Log.v("onEditDone text: ", input);
                        if (!TextUtils.isEmpty(input)) {
                            KiiUser.findUserByUserName(input,
                                    new KiiUserCallBack() {
                                        @Override
                                        public void onFindCompleted(
                                                int token, KiiUser caller,
                                                KiiUser found,
                                                Exception exception) {
                                            if (found != null) {
                                                target.addUser(found);
                                                target.save(new KiiGroupCallBack () {
                                                    @Override
                                                    public void onSaveCompleted(
                                                            int token,
                                                            KiiGroup group,
                                                            Exception exception) {
                                                            ListDialogFragment.this
                                                                    .dismiss();
                                                            if (exception != null) {
                                                                ((GroupListActivity) getActivity())
                                                                        .showAlertDialog(exception
                                                                                .getMessage());
                                                            } else {
                                                                Toast.makeText(getActivity(), "Add user done.",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                    }
                                                });
                                            } else {
                                                    ListDialogFragment.this
                                                            .dismiss();
                                                    ((GroupListActivity) getActivity())
                                                            .showAlertDialog(exception
                                                                    .getMessage());
                                            }
                                        }
                                    });
                        } else {
                            ListDialogFragment.this.dismiss();
                        }
                    }
                });
        }

        private void doCreateTopic() {
            KiiTopic tp = target.topic(Constants.GROUPTOPIC);
            tp.save(new KiiTopicCallBack() {
                @Override
                public void onSaveCompleted(int taskId, KiiTopic target,
                        Exception e) {
                    ListDialogFragment.this.dismiss();
                    if (e != null) {
                        ((GroupListActivity) getActivity()).showAlertDialog(e
                                .getMessage());
                    } else {
                        Toast.makeText(getActivity(), "Topic created.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void doSubscribeTopic() {
            KiiTopic tp = target.topic(Constants.GROUPTOPIC);
            KiiPushSubscription sub = KiiUser.getCurrentUser()
                    .pushSubscription();
            sub.subscribe(tp, new KiiPushCallBack() {
                @Override
                public void onSubscribeCompleted(int taskId,
                        KiiSubscribable target, Exception e) {
                    ListDialogFragment.this.dismiss();
                    if (e != null) {
                        ((GroupListActivity) getActivity()).showAlertDialog(e
                                .getMessage());
                    } else {
                        Toast.makeText(getActivity(), "Subscribe done.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void doSendMessageToTopic(KiiPushMessage msg) {
            KiiTopic tp = target.topic(Constants.GROUPTOPIC);
            tp.sendMessage(msg, new KiiTopicCallBack() {

                @Override
                public void onSendMessageCompleted(int taskId, KiiTopic target,
                        KiiPushMessage message, Exception e) {
                    ListDialogFragment.this.dismiss();
                    if (e != null) {
                        ((GroupListActivity) getActivity()).showAlertDialog(e
                                .getMessage());
                    } else {
                        Toast.makeText(getActivity(), "Send message done.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
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
            this.showEditDialog("Input group name", new EditDialogFragment.OnEditDone() {
                @Override
                public void onEditDone(String input) {
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
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        this.showListDialog(groups.get(position));
    }

    public void dismissDialogByTag(String TAG) {
        DialogFragment df = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG);
        df.dismiss();
    }

}
