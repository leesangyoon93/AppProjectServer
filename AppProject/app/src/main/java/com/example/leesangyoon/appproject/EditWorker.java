package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 12..
 */
public class EditWorker extends AppCompatActivity {

    TextView workerId, workerName, workerPhoneNumber;
    Button deleteWorkerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editworker);

        workerId = (TextView)findViewById(R.id.text_detailWorkerId);
        workerName = (TextView)findViewById(R.id.text_detailWorkerName);
        workerPhoneNumber = (TextView)findViewById(R.id.text_detailWorkerPhoneNumber);
        deleteWorkerButton = (Button)findViewById(R.id.btn_deleteWorker);

        Intent intent = getIntent();
        workerId.setText(intent.getStringExtra("workerId"));
        workerName.setText(intent.getStringExtra("workerName"));
        workerPhoneNumber.setText(intent.getStringExtra("workerPhoneNumber"));

        deleteWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteUserToServer(workerId.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteUserToServer(final String userId) throws Exception {

        final String URL = "http://52.41.19.232/deleteUser";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", userId);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(EditWorker.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("success")) {
                            Toast.makeText(EditWorker.this, "요양사 삭제 완료.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditWorker.this, ViewWorker.class);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("development", "Error: " + error.getMessage());
                    }
                });

        volley.getInstance().addToRequestQueue(req);
    }
}
