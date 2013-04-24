package com.kii.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BucketPushFragment extends Fragment implements
        OnItemClickListener {

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bucket_push_list, container, false);
        ListView listView = (ListView) v.findViewById(R.id.bucketListView);
        listView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        if (pos == 0) {
            Intent intent = new Intent(this.getActivity(),
                    BucketControlActivity.class);
            startActivity(intent);
        } else if (pos == 1) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_BUCKET,
                    getString(R.string.subscribe), this.getActivity())
                    .execute();
        } else if (pos == 2) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.UNSUBSCRIBE_BUCKET,
                    getString(R.string.unsubscribe), this.getActivity())
                    .execute();
        } else if (pos == 3) {
            new KiiPushAppTask(KiiPushAppTask.MENU_ID.SUBSCRIBE_ABUCKET,
                    getString(R.string.subscribe_ascope_bucket),
                    this.getActivity()).execute();
        }
    }

}
