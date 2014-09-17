package com.kii.push;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiTopic;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.PushMessageBundleHelper;
import com.kii.cloud.storage.PushMessageBundleHelper.MessageType;
import com.kii.cloud.storage.PushToAppMessage;
import com.kii.cloud.storage.PushToUserMessage;
import com.kii.cloud.storage.ReceivedMessage;

public class PushMessageParseActivity extends Activity {
    public static final String TAG = "PushMessageParseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show Content
        setContentView(R.layout.parseview);
        // Get intent bundle
        final Bundle b = getIntent().getExtras();
        // Expand
        parseMessage(b);
    }

    public void parseMessage(Bundle b) {
        ReceivedMessage message = PushMessageBundleHelper.parse(b);
        MessageType type = message.pushMessageType();
        setKeyValueToTable("Type", type.toString());
        switch (type) {
        case PUSH_TO_APP:
            PushToAppMessage pam = (PushToAppMessage) message;
            KiiUser sender = pam.getSender();
            setKeyValueToTable("Sender", sender.toUri().toString());
            if (pam.containsKiiBucket()) {
                if (pam.containsKiiObject()) {
                    KiiObject obj = pam.getKiiObject();
                    setKeyValueToTable("KiiObject", obj.toString());
                }
            }
            break;
        case PUSH_TO_USER:
            PushToUserMessage pum = (PushToUserMessage) message;
            KiiUser usr = pum.getSender();
            setKeyValueToTable("Sender", usr.toUri().toString());
            if (pum.containsKiiTopic()) {
                KiiTopic topic = pum.getKiiTopic();
                setKeyValueToTable("Topic", topic.toString());
            }
            break;
        case DIRECT_PUSH:
            break;
        }
    }

    public void setKeyValueToTable(String key, String value) {
        TableLayout table = (TableLayout) findViewById(R.id.parseview);
        android.widget.TableRow row = new android.widget.TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.CENTER);
        table.addView(row);

        TextView tvkey = new TextView(this);
        tvkey.setText(key + "  ");
        row.addView(tvkey);

        TextView tvvalue = new TextView(this);
        tvvalue.setText(value);
        row.addView(tvvalue);
    }

}