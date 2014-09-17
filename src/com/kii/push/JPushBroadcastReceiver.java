package com.kii.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class JPushBroadcastReceiver extends KiiPushBroadcastReceiver {
    private static final String TAG = "JPushBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String jpushMessageType = intent.getAction();
        if (jpushMessageType != null) {
            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(jpushMessageType)) {
                messageReceived(context, intent);
            }
        } else {
            Log.e(TAG, "Unknown message type.");
        }
    }

}
