package com.davidpaul.getsetgoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private EditText txtUserName;
    private Button btnGo;
    private PreferenceHelper preferenceHelper;
    private CoordinatorLayout Container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");
        Container = (CoordinatorLayout) findViewById(R.id.Container);
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = txtUserName.getText().toString();
                if (userName.equals("")) {
                    Snackbar.make(v, "Please enter your profile Name", Snackbar.LENGTH_LONG).show();
                } else {
                    preferenceHelper.setdata("_keyUserName", userName);
                    Toast.makeText(RegistrationActivity.this, "Welcome, " + preferenceHelper.getData("_keyUserName"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        new UtilityClass(this).setViewGroupTypeface(Container, UtilityClass.fontItalic);
    }
}
