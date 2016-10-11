package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by daddyslab on 2016. 10. 12..
 */
public class EditWorker extends AppCompatActivity {

    TextView workerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editworker);

        workerId = (TextView)findViewById(R.id.text_editWorkerId);

        Intent intent = getIntent();
        workerId.setText(intent.getStringExtra("workerId"));
    }
}
