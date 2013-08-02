package com.kii.push;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class KiiGCMRegisterer {
    private static KiiGCMRegisterer reg = null;
    private String regId = null;
    private GoogleCloudMessaging gcm = null;

    public static KiiGCMRegisterer getInstance(Context context) {
        if (reg == null) {
            reg = new KiiGCMRegisterer();
            reg.gcm = GoogleCloudMessaging.getInstance(context);
        }
        return reg;
    }

    public void register(String senderId) throws IOException {
        regId = gcm.register(senderId);
    }

    public void unregister() throws IOException {
        try {
            gcm.unregister();
            regId = null;
        } catch (IOException e) {
            throw e;
        }
    }

    public boolean isRegistered() {
        return regId != null;
    }

    public String getRegId() {
        return regId;
    }
}
