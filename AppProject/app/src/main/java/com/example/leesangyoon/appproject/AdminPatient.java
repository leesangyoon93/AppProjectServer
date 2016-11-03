package com.example.leesangyoon.appproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by daddyslab on 2016. 11. 1..
 */

// 여기는 환자 리스트 보여주자 자기가 가지고 있는 .. 리스트 클릭하면 showPatient 로 가지고 showPatient 에서 수정 가능하게 ( 요양사만 )
//
public class AdminPatient extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_adminpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_adminpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(AdminPatient.this, MainActivity.class);
                startActivity(intent);
                super.onBackPressed();
                break;
            case R.id.menu_addPatient:
                intent = new Intent(AdminPatient.this, CreatePatient.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
