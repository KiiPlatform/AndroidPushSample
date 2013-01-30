package com.kii.push;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class ListActivityCore extends ListActivity {

    public static final int DIALOG_ALERT = 10;
    public static final int DIALOG_PROGRESS = 11;
    public static final String MESSAGE_KEY = "message";

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
        case DIALOG_PROGRESS:
            return createProgressDialog(args, this);
        case DIALOG_ALERT:
            return createAlertDialogForCommon(args, this);
        default:
            return super.onCreateDialog(id, args);
        }
    }

    public static void openDialog(int resId, String message, Activity activity) {
        if (!activity.isFinishing()) {
            Bundle args = new Bundle();
            args.putString(MESSAGE_KEY, message);
            activity.showDialog(resId, args);
        }
    }

    public static void closeDialog(int resId, Activity activity) {
        if (!activity.isFinishing()) {
            activity.removeDialog(resId);
        }
    }

    private static Dialog createAlertDialogForCommon(Bundle b,
            final Activity activity) {
        Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(b.getString(MESSAGE_KEY));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                closeDialog(DIALOG_ALERT, activity);
            }
        });
        dialog.setOnKeyListener(backKeyListener(activity));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                closeDialog(DIALOG_ALERT, activity);
            }
        });
        return dialog.create();
    }

    private static OnKeyListener backKeyListener(final Activity activity) {
        OnKeyListener listener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    closeDialog(DIALOG_ALERT, activity);
                    return true;
                } else {
                    return false;
                }
            }
        };
        return listener;
    }

    private static Dialog createProgressDialog(Bundle b, final Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(b.getString(MESSAGE_KEY));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        closeDialog(DIALOG_PROGRESS, activity);
                    }
                });
        return progressDialog;
    }

    public static void showToastMessage(String successMessage, Activity activity) {
        showToastMessage(successMessage, null, true, activity);
    }

    public static void showToastMessage(String successMessage,
            String failMessage, boolean condition, Activity activity) {
        if (!activity.isFinishing()) {
            Toast.makeText(activity, condition ? successMessage : failMessage,
                    Toast.LENGTH_LONG).show();
        }
    }
}