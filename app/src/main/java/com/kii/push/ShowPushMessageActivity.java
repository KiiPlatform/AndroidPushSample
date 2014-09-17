package com.kii.push;

import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class ShowPushMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // Show Content
        setContentView(R.layout.showview);
        // Get intent bundle
        final Bundle b = getIntent().getExtras();
        // Expand
        TableLayout table = (TableLayout) findViewById(R.id.showview);
        setTextToTable(b, this, table);
        final Button button = (Button) findViewById(R.id.parse);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        PushMessageParseActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public static void setTextToTable(Bundle bundle, Activity activity,
            TableLayout table) {
        Iterator<String> keys = bundle.keySet().iterator();
        String hkey;
        String hvalue;

        while (keys.hasNext()) {
            hkey = keys.next();
            hvalue = bundle.getString(hkey);

            android.widget.TableRow row = new android.widget.TableRow(activity);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);
            table.addView(row);

            TextView tvkey = new TextView(activity);
            tvkey.setText(hkey + "  ");
            row.addView(tvkey);

            TextView tvvalue = new TextView(activity);
            tvvalue.setText(hvalue);
            row.addView(tvvalue);
        }
    }

}