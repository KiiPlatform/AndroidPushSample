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
    private static final String TAG = "KiiPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String gcmMessageType = gcm.getMessageType(intent);
        if (gcmMessageType != null) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(gcmMessageType)) {
                messageReceived(context, intent);
                Log.e(TAG, "Error occurred while gcm messge sending.");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(gcmMessageType)) {
                Log.i(TAG, "Received deleted messages notification");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(gcmMessageType)) {
                messageReceived(context, intent);
            }
            setResultCode(Activity.RESULT_OK);
        } else {
            Log.e(TAG, "Unknown message type.");
        }
    }

    protected void messageReceived(Context context, Intent intent) {
        fileLog(TAG, "Received message :" + intent.getExtras().toString());
        fileLog(TAG, "Time: " + System.currentTimeMillis());
        NotificationManager nManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle extras = intent.getExtras();
        String str = "Tap to view receive JSON contents.";
        Log.i(TAG, str);


        Intent i = new Intent(context.getApplicationContext(),
                ShowPushMessageActivity.class);
        i.putExtras(extras);
        i.setAction(Context.ACTIVITY_SERVICE);
        PendingIntent pend = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder b = new Notification.Builder(context);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setTicker(str);
        b.setNumber(1);
        b.setWhen(System.currentTimeMillis());
        b.setContentIntent(pend);

        nManager.cancelAll();
        nManager.notify(1, b.build());
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
