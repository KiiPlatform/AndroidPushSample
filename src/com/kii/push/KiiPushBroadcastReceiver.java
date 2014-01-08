package com.kii.push;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class KiiPushBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "KiiPushBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            messageReceived(context, intent);
            Log.e(TAG, "Error occurred while gcm messge sending.");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                .equals(messageType)) {
            Log.i(TAG, "Received deleted messages notification");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                .equals(messageType)) {
            messageReceived(context, intent);
        } else {
            Log.e(TAG, "Unknown message type.");
        }
        setResultCode(Activity.RESULT_OK);
    }

    protected void messageReceived(Context context, Intent intent) {
        fileLog(TAG, "Received message :" + intent.getExtras().toString());
        fileLog(TAG, "Time: " + System.currentTimeMillis());
        NotificationManager nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();
        Bundle extras = intent.getExtras();
        String str = "Tap to view receive JSON contents.";
        Log.i(TAG, str);

        n.icon = R.drawable.ic_launcher;
        n.tickerText = str;
        n.number = 1;
        n.when = System.currentTimeMillis();

        Intent i = new Intent(context.getApplicationContext(),
                ShowPushMessageActivity.class);
        i.putExtras(extras);
        i.setAction(Context.ACTIVITY_SERVICE);
        PendingIntent pend = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        n.setLatestEventInfo(context.getApplicationContext(),
                context.getString(R.string.app_name), str, pend);

        nManager.cancelAll();
        nManager.notify(1, n);
    }

    private void fileLog(String tag, String message) {
        Log.i(tag, message);
        final String path = "pushlog.txt";
        File f = new File(Environment.getExternalStorageDirectory(), path);

        BufferedWriter bw = null;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(f, true));
            bw.write(tag + " : " + message);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}