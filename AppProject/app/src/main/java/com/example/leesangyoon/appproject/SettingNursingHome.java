package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

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
                new AlertDialog.Builder(SettingNursingHome.this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // yes 버튼 누르면
                                try {
                                    SharedPreferences.Editor editor = userSession.edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(SettingNursingHome.this, Login.class);
                                    startActivity(intent);

                                    Toast.makeText(SettingNursingHome.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // no 버튼 누르면
                            }
                        })
                        .show();
                break;
            case android.R.id.home:
                Intent intent = new Intent(SettingNursingHome.this, ViewWorker.class);
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
