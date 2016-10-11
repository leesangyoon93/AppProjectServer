package com.example.leesangyoon.appproject;

import android.content.Context;
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

public class Login extends AppCompatActivity {
    Button loginButton, registerButton;
    EditText userId, password;

    final String URL = "http://52.41.19.232/login";

    SharedPreferences userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.btn_login);
        registerButton = (Button) findViewById(R.id.btn_registerNursinHome);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 테스트용 인텐트
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);


//                userId = (EditText) findViewById(R.id.input_userId);
//                password = (EditText) findViewById(R.id.input_password);
//                String id = userId.getText().toString();
//                String pw = password.getText().toString();
//
//                if(id.isEmpty()) {
//                    Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                else if(pw.isEmpty()) {
//                    Toast.makeText(Login.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    try {
//                        userLoginToServer(id, pw);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 요양원 등록 액티비티로 이동
                Intent intent = new Intent(Login.this, CreateNursingHome.class);
                startActivity(intent);
            }
        });
    }

//    private void userLoginToServer(final String id, final String pw) throws Exception {
//
//        Map<String, String> postParam = new HashMap<String, String>();
//        postParam.put("userId", id);
//        postParam.put("password", pw);
//
//        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
//                new JSONObject(postParam), new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//
//                    if (response.toString().contains("result")) {
//                        if (response.getString("result").equals("fail")) {
//                            Toast.makeText(Login.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
//                        }
//                        else if(response.getString("result").equals("failId")) {
//                            Toast.makeText(Login.this, "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show();
//                        }
//                        else if(response.getString("result").equals("failPw")) {
//                            Toast.makeText(Login.this, "비밀번호가 옳바르지 않습니다.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        //response받아와서 user객체에 넘김
//                        User.getInstance().setId(response.getString("_id"));
//                        User.getInstance().setAuth(response.getInt("auth"));
//
//                        SharedPreferences.Editor editor = userSession.edit();
//                        editor.putString("id", User.getInstance().getId());
//                        editor.apply();
//
//                        Intent intent = new Intent(Login.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.d("development", "Error: " + error.getMessage());
//                    }
//                });
//
//        volley.getInstance().addToRequestQueue(req);
//    }
}
