package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class CreateWorker extends AppCompatActivity {

    EditText workerName, workerPhoneNumber, workerId, workerPassword;
    Button createWorkerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createworker);

        workerName = (EditText)findViewById(R.id.input_workerName);
        workerPhoneNumber = (EditText)findViewById(R.id.input_workerPhoneNumber);
        workerId = (EditText)findViewById(R.id.input_workerId);
        workerPassword = (EditText)findViewById(R.id.input_workerPassword);

        createWorkerButton = (Button)findViewById(R.id.submit_createWorker);

        createWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createWorkerToServer(workerName.getText().toString(), workerPhoneNumber.getText().toString(),
                            workerId.getText().toString(), workerPassword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createWorkerToServer(final String workerName, final String workerPhoneNumber, final String workerId, final String workerPassword) throws Exception {

        final String URL = "http://52.41.19.232/createWorker";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("workerName", workerName);
        postParam.put("workerPhoneNumber", workerPhoneNumber);
        postParam.put("workerId", workerId);
        postParam.put("workerPassword", workerPassword);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(CreateWorker.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("overlap")) {
                            Toast.makeText(CreateWorker.this, "요양사 아이디가 이미 사용중입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("success")) {
                            Toast.makeText(CreateWorker.this, "요양사 등록 완료. 요양사 계정으로 로그인이 가능합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateWorker.this, ViewWorker.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateWorker.this, ViewWorker.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
