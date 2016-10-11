package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class CreateNursingHome extends AppCompatActivity {
    final String URL = "http://52.41.19.232/createNursingHome";

    EditText homeName, address, nursingHomePhoneNumber, adminName, adminPhoneNumber, adminId, adminPassword;
    Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createnursinghome);

        homeName = (EditText) findViewById(R.id.input_homeName);
        address = (EditText) findViewById(R.id.input_address);
        nursingHomePhoneNumber = (EditText) findViewById(R.id.input_nursingHomePhoneNumber);
        adminName = (EditText) findViewById(R.id.input_adminName);
        adminPhoneNumber = (EditText) findViewById(R.id.input_adminPhoneNumber);
        adminId = (EditText) findViewById(R.id.input_adminId);
        adminPassword = (EditText) findViewById(R.id.input_adminPassword);

        registerButton = (Button) findViewById(R.id.submit_registerNursingHome);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerNursingHomeToServer(
                            homeName.getText().toString(),
                            address.getText().toString(),
                            nursingHomePhoneNumber.getText().toString(),
                            adminName.getText().toString(),
                            adminPhoneNumber.getText().toString(),
                            adminId.getText().toString(),
                            adminPassword.getText().toString()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void registerNursingHomeToServer(final String homeName, final String address, final String nursingHomePhoneNumber,
                                             final String adminName, final String adminPhoneNumber, final String adminId,
                                             final String adminPassword) throws Exception {

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("homeName", homeName);
        postParam.put("address", address);
        postParam.put("nursingHomePhoneNumber", nursingHomePhoneNumber);
        postParam.put("adminName", adminName);
        postParam.put("adminPhoneNumber", adminPhoneNumber);
        postParam.put("adminId", adminId);
        postParam.put("adminPassword", adminPassword);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(CreateNursingHome.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        } else if (response.getString("result").equals("overlap")) {
                            Toast.makeText(CreateNursingHome.this, "관리자 아이디가 이미 사용중입니다.", Toast.LENGTH_SHORT).show();
                        } else if (response.getString("result").equals("success")) {
                            Toast.makeText(CreateNursingHome.this, "요양원 등록 완료! 관리자 계정으로 로그인 해주세요.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CreateNursingHome.this, Login.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
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
