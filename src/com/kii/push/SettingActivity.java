package com.kii.push;

import com.kii.cloud.storage.KiiUser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity implements View.OnClickListener {

    Button loginBtn;
    Button logoutBtn;
    EditText usernameEdit;
    EditText passwordEdit;
    PrefWrapper pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting);
        pref = PrefWrapper.getInstance(this);
        loginBtn = (Button) this.findViewById(R.id.setting_login);
        logoutBtn = (Button) this.findViewById(R.id.setting_logout);
        usernameEdit = (EditText) this.findViewById(R.id.username_edit);
        passwordEdit = (EditText) this.findViewById(R.id.password_edit);

        usernameEdit.setText(pref.getUsername());
        passwordEdit.setText(pref.getPassword());
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.setting_login) {
            String uname = usernameEdit.getText().toString();
            String pass = passwordEdit.getText().toString();
            KiiPushAppTask task = new KiiPushAppTask(
                    KiiPushAppTask.MENU_ID.LOGIN, "Login", this);
            task.execute(uname, pass);
        } else if (resId == R.id.setting_logout) {
            KiiUser.logOut();
            usernameEdit.getText().clear();
            passwordEdit.getText().clear();
            Toast.makeText(this, "Logout done", Toast.LENGTH_LONG).show();
        }
    }

}
