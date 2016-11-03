package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by daddyslab on 2016. 11. 1..
 */

// 여기는 누구든지 보호자환자 생성할 수 있게. 프로텍터를 지정하면 해당 프로텍터한테만 보임.
public class CreatePatient extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(CreatePatient.this, AdminPatient.class);
                startActivity(intent);
                super.onBackPressed();
                break;
            case R.id.menu_createPatient:
                // 환자/보호자 생성 요청 후 인덴트
        }
        return super.onOptionsItemSelected(item);
    }
}
