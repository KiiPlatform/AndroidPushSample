package com.kii.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kii.cloud.storage.KiiPushMessage;
import com.kii.push.ListDialogFragment.ListDialogFragmentCallback;

public class TopicPushFragment extends Fragment implements
        OnItemClickListener, ListDialogFragmentCallback {

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.topic_push_list, container, false);
        ListView listView = (ListView) v.findViewById(R.id.topicListView);
        listView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        if (pos == 0) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.CREATE_USCOPE_TOPIC,
                    getString(R.string.create_uscope_topic), getActivity()).execute();
        } else if (pos == 1) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_USCOPE_TOPIC,
                    getString(R.string.subscribe_uscope_topic), getActivity()).execute();
        } else if (pos == 2) {
            showSendMessageListDialog();
        } else if (pos == 3) {
            Intent intent = new Intent(getActivity(), GroupListActivity.class);
            startActivity(intent);
        } else if (pos == 4) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_ASCOPE_TOPIC,
                    getString(R.string.subscribe_ascope_topic), getActivity())
                    .execute();
        }
    }

    private void showSendMessageListDialog() {
        ListDialogFragment ldf = ListDialogFragment.newInstance(
                R.layout.sendmessage_listdialog, R.string.send_message,
                android.R.drawable.ic_menu_edit, 0);
        ldf.setTargetFragment(this, 0);
        ldf.show(getActivity().getSupportFragmentManager(), "SendMessage");
    }

    private void dismissDialogByTag(String tag) {
        DialogFragment df = (DialogFragment) getActivity()
                .getSupportFragmentManager().findFragmentByTag(tag);
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
                    getActivity()).execute(msg);
        } else if (pos == 1) {
            // Load message by MessageTemplateLoader and send.
            try {
                KiiPushMessage message = MessageTemplateLoader
                        .loadMessageFromTemplate();
                new KiiPushAppTask(
                        (int) KiiPushAppTask.MENU_ID.SENDMESSAGE_TO_USCOPE_TOPIC,
                        getString(R.string.sendmessage_to_uscope_topic),
                        getActivity()).execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (pos == 2) {
            MessageTemplateLoader.launchEditor(getActivity());
        }
        dismissDialogByTag("SendMessage");
    }

}
