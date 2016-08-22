package com.kii.push;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

public class PropertyManager {

    private static final String APP_ID = "9ab34d8b";
    private static final String APP_KEY = "7a950d78956ed39f3b0815f0f001b43b";
    private static final String APP_URL = "https://api-jp.kii.com/api";
    private static final String APP_TOPIC_NAME = "appTestTopic";
    private static final String APP_BUCKET_NAME = "appBucket";

    private PropertyManager() {
    }

    private static PropertyManager INSTANCE = new PropertyManager();

    public static PropertyManager getInstance() {
        return INSTANCE;
    }

    public synchronized void load(Context ctx) {
        // Loading from file is abolished.
    }

    public String getAppId() {
        return APP_ID;
    }

    public String getAppKey() {
        return APP_KEY;
    }

    public String getBaseUri() {
        return APP_URL;
    }

    public String getAppTopicName() {
        return APP_TOPIC_NAME;
    }

    public String getAppBucketName() {
        return APP_BUCKET_NAME;
    }

}
