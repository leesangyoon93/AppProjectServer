package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by daddyslab on 2016. 10. 12..
 */
public class SettingNursingHome extends AppCompatActivity {

    TextView nursingHomeName, nursingHomeAddress, nursingHomePhoneNumber, adminName, adminPhoneNumber, adminId;
    Button adminLogoutButton;

    SharedPreferences userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_settingnursinghome);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        nursingHomeName = (TextView)findViewById(R.id.text_nursingHomeName);
        nursingHomeAddress = (TextView)findViewById(R.id.text_nursingHomeAddress);
        nursingHomePhoneNumber = (TextView)findViewById(R.id.text_nursingHomePhoneNumber);
        adminName = (TextView)findViewById(R.id.text_adminName);
        adminPhoneNumber = (TextView)findViewById(R.id.text_adminPhoneNumber);
        adminId = (TextView)findViewById(R.id.text_adminId);

        nursingHomeName.setText(User.getInstance().getNursingHomeName());
        nursingHomeAddress.setText(User.getInstance().getNursingHomeAddress());
        nursingHomePhoneNumber.setText(User.getInstance().getNursingHomePhoneNumber());
        adminName.setText(User.getInstance().getUserName());
        adminPhoneNumber.setText(User.getInstance().getPhoneNumber());
        adminId.setText(User.getInstance().getUserId());

        adminLogoutButton = (Button)findViewById(R.id.btn_adminLogout);

        adminLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = userSession.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(SettingNursingHome.this, Login.class);
                startActivity(intent);

                Toast.makeText(SettingNursingHome.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingNursingHome.this, ViewWorker.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
