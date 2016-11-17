package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

/**
 * Created by daddyslab on 2016. 11. 17..
 */
public class EditCategory extends AppCompatActivity {
    Switch meal, clean, activity, moveTrain, comment, restRoom, medicine, mentalTrain, physicalCare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editcategory);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        meal = (Switch)findViewById(R.id.switch_meal);
        clean = (Switch)findViewById(R.id.switch_bodyClean);
        activity = (Switch)findViewById(R.id.switch_activity);
        moveTrain = (Switch)findViewById(R.id.switch_moveTrain);
        comment = (Switch)findViewById(R.id.switch_comment);
        restRoom = (Switch)findViewById(R.id.switch_restRoom);
        medicine = (Switch)findViewById(R.id.switch_medicine);
        mentalTrain = (Switch)findViewById(R.id.switch_mentalTrain);
        physicalCare = (Switch)findViewById(R.id.switch_physicalCare);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editcategory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(EditCategory.this, ShowPatient.class);
                startActivity(intent);
                break;
            case R.id.menu_saveCategory:
                //saveCategoryToServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditCategory.this, ShowPatient.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
