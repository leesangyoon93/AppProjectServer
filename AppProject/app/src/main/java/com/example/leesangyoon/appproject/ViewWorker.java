package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class ViewWorker extends AppCompatActivity{

    TextView nursingHomeName;
    Button createWorkerButton;

    // adapter list 달아야됨.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_viewworker);

        nursingHomeName = (TextView)findViewById(R.id.text_nursingHomeName);
        createWorkerButton = (Button)findViewById(R.id.btn_createWorker);

        nursingHomeName.setText(User.getInstance().getNursingHomeName());

        createWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWorker.this, CreateWorker.class);
                startActivity(intent);
            }
        });
    }
}
