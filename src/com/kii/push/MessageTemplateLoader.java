package com.kii.push;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.kii.cloud.storage.KiiPushMessage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class MessageTemplateLoader {

    private static final String filepath = "com.kii.push/messagetemplate.txt";
    private static final File templateFile = new File(
            Environment.getExternalStorageDirectory(), filepath);

    public static JSONObject loadMessageTemplate() throws IOException,
            JSONException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(new FileInputStream(templateFile));
            byte[] buff = new byte[8192];
            while (true) {
                int len = bis.read(buff);
                if (len < 1) {
                    break;
                }
                baos.write(buff, 0, len);
            }
            String jsonStr = baos.toString("UTF-8");
            return new JSONObject(jsonStr);
        } finally {
            try {
                if (bis != null)
                    bis.close();
                baos.close();
            } catch (IOException e1) {
            }
        }
    }

    public static KiiPushMessage loadMessageFromTemplate() throws IOException,
            JSONException {
        JSONObject obj = loadMessageTemplate();
        // TODO: implement
        return null;
    }

    public static void launchEditor(Context ctx) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(templateFile),
                "text/plain");
        i.putExtra(Intent.ACTION_VIEW, i.getDataString());
        ctx.startActivity(i);
    }

}
