package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

    EditText workerName, workerPhoneNumber, workerId, workerPassword, workerPasswordCheck;
    RadioButton male, female;
    String gender = "male";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createworker);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        workerName = (EditText)findViewById(R.id.input_workerName);
        workerPhoneNumber = (EditText)findViewById(R.id.input_workerPhoneNumber);
        workerId = (EditText)findViewById(R.id.input_workerId);
        workerPassword = (EditText)findViewById(R.id.input_workerPassword);
        workerPasswordCheck = (EditText)findViewById(R.id.input_workerPasswordCheck);

        male = (RadioButton)findViewById(R.id.radio_male);
        female = (RadioButton)findViewById(R.id.radio_female);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createworker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_createWorker:
                try {
                    if(workerName.getText().toString().equals("") || workerId.getText().toString().equals("") || workerPassword.getText().toString().equals("") || workerPhoneNumber.getText().toString().equals("")) {
                        Toast.makeText(CreateWorker.this, "입력창을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (workerPassword.getText().toString().equals(workerPasswordCheck.getText().toString())) {
                            if (female.isChecked()) {
                                gender = "female";
                            }
                            createWorkerToServer(workerName.getText().toString(), workerPhoneNumber.getText().toString(),
                                    workerId.getText().toString(), workerPassword.getText().toString());
                        } else {
                            Toast.makeText(CreateWorker.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
//            case android.R.id.home:
//                Intent intent = new Intent(CreateWorker.this, ViewWorker.class);
//                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void createWorkerToServer(final String workerName, final String workerPhoneNumber, final String workerId, final String workerPassword) throws Exception {

        final String URL = "http://52.41.19.232/createWorker";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("workerName", workerName);
        postParam.put("workerPhoneNumber", workerPhoneNumber);
        postParam.put("workerId", workerId);
        postParam.put("workerPassword", workerPassword);
        postParam.put("workerGender", gender);

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
