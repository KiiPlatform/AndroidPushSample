package com.kii.push;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListDialogFragment extends DialogFragment {

    public static final String TAG = "ListDialogFragment";
    ListView listView;
    OnItemClickListener listener; 

    public static ListDialogFragment newInstance(int listViewLayoutId,
            int titleResId, int iconResId, OnItemClickListener listener) {
        ListDialogFragment frag = new ListDialogFragment();
        Bundle b = new Bundle();
        b.putInt("layoutId", listViewLayoutId);
        b.putInt("titleResId", titleResId);
        b.putInt("iconResId", iconResId);
        frag.setArguments(b);
        frag.listener = listener;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int layoutId = getArguments().getInt("layoutId");
        int titleResId = getArguments().getInt("titleResId");
        int iconResId = getArguments().getInt("iconResId");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        listView = (ListView) inflater
                .inflate(layoutId, null);
        listView.setOnItemClickListener(this.listener);

        return new AlertDialog.Builder(getActivity())
                .setIcon(iconResId)
                .setTitle(titleResId)
                .setView(listView).create();
    }

}
