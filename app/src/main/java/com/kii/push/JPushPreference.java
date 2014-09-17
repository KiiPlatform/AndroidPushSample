package com.kii.push;

import android.content.Context;
import android.content.SharedPreferences;

public class JPushPreference {
    private static final String PROPERTY_REG_ID = "JPushRegId";

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getJPushPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, null);
        return registrationId;
    }

    public static void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getJPushPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }

    public static void clearRegistrationId(Context context) {
        final SharedPreferences prefs = getJPushPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
    }

    private static SharedPreferences getJPushPreferences(Context context) {
        return context.getSharedPreferences(
                JPushFragment.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
