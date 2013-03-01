package com.kii.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kii.cloud.storage.KiiPushMessage;
import com.kii.push.ListDialogFragment.ListDialogFragmentCallback;

public class TopicPushActivity extends FragmentActivity implements
        OnItemClickListener, ListDialogFragmentCallback {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.topic_push_list);
        ListView listView = (ListView) findViewById(R.id.topicListView);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        if (pos == 0) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.CREATE_USCOPE_TOPIC,
                    getString(R.string.create_uscope_topic), this).execute();
        } else if (pos == 1) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_USCOPE_TOPIC,
                    getString(R.string.subscribe_uscope_topic), this).execute();
        } else if (pos == 2) {
            showSendMessageListDialog();
        } else if (pos == 3) {
            Intent intent = new Intent(this, GroupListActivity.class);
            startActivity(intent);
        } else if (pos == 4) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_ASCOPE_TOPIC,
                    getString(R.string.subscribe_ascope_topic), this).execute();
        }
    }

    private void showSendMessageListDialog() {
        ListDialogFragment.newInstance(R.layout.sendmessage_listdialog,
                R.string.send_message, android.R.drawable.ic_menu_edit, 0
                ).show(getSupportFragmentManager(), "SendMessage");
    }

    private void dismissDialogByTag(String tag) {
        DialogFragment df = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);
        df.dismiss();
    }

    @Override
    public void onListDialogItemClicked(AdapterView<?> parent, View view,
            int pos, long id, int requestId) {
        if (pos == 0) {
            KiiPushMessage.Data data = new KiiPushMessage.Data();
            data.put("custom_message", "Hello, Kii Push servce!");
            KiiPushMessage msg = KiiPushMessage.buildWith(data).build();
            new KiiPushAppTask(
                    (int) KiiPushAppTask.MENU_ID.SENDMESSAGE_TO_USCOPE_TOPIC,
                    getString(R.string.sendmessage_to_uscope_topic),
                    TopicPushActivity.this).execute(msg);
        } else if (pos == 1) {
            // Load message by MessageTemplateLoader and send.
            try {
                KiiPushMessage message = MessageTemplateLoader
                        .loadMessageFromTemplate();
                new KiiPushAppTask(
                        (int) KiiPushAppTask.MENU_ID.SENDMESSAGE_TO_USCOPE_TOPIC,
                        getString(R.string.sendmessage_to_uscope_topic),
                        TopicPushActivity.this).execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (pos == 2) {
            MessageTemplateLoader.launchEditor(getApplicationContext());
        }
        dismissDialogByTag("SendMessage");
    }

}
