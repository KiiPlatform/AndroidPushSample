package com.kii.push;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

public class EditDialogFragment extends DialogFragment {

    interface EditDialogFragmentCallback {
        public void onEditDone(String input, int requestId);
    }

    EditText editText;

    public static EditDialogFragment newInstance(String message, int requestId) {
        EditDialogFragment frag = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putInt("requestId", requestId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        final int requestId = getArguments().getInt("requestId");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        editText = (EditText) inflater.inflate(R.layout.editdialog, null);

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle(R.string.edit_text)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String input = editText.getText().toString();
                                Activity ac = getActivity();
                                if (ac != null) {
                                    ((EditDialogFragmentCallback) ac)
                                            .onEditDone(input, requestId);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                EditDialogFragment.this.dismiss();
                            }
                        }).create();
    }
}
