package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by daddyslab on 2016. 10. 12..
 */
public class SettingNursingHome extends AppCompatActivity {

    TextView nursingHomeName, nursingHomeAddress, nursingHomePhoneNumber, adminName, adminPhoneNumber, adminId;

    SharedPreferences userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_settingnursinghome);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_logout:
                SharedPreferences.Editor editor = userSession.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(SettingNursingHome.this, Login.class);
                startActivity(intent);

                Toast.makeText(SettingNursingHome.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                intent = new Intent(SettingNursingHome.this, ViewWorker.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingNursingHome.this, ViewWorker.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
