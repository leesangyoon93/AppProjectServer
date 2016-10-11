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

    SharedPreferences userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.btn_login);
        registerButton = (Button) findViewById(R.id.btn_registerNursinHome);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        if(userSession.contains("userId")){
            try {
                autoLoginToServer(userSession.getString("userId", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = (EditText) findViewById(R.id.input_userId);
                password = (EditText) findViewById(R.id.input_password);
                String id = userId.getText().toString();
                String pw = password.getText().toString();

                if(id.isEmpty()) {
                    Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(pw.isEmpty()) {
                    Toast.makeText(Login.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        loginToServer(id, pw);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, CreateNursingHome.class);
                startActivity(intent);
            }
        });
    }

    private void loginToServer(final String userId, final String password) throws Exception {

        final String URL = "http://52.41.19.232/login";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", userId);
        postParam.put("password", password);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(Login.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("failId")) {
                            Toast.makeText(Login.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("failPw")) {
                            Toast.makeText(Login.this, "비밀번호가 옳바르지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        User.getInstance().setUserId(response.getString("userId"));
                        User.getInstance().setAuth(response.getInt("auth"));
                        User.getInstance().setNursingHomeId(response.getString("nursingHome"));

                        SharedPreferences.Editor editor = userSession.edit();
                        editor.putString("userId", User.getInstance().getUserId());
                        editor.apply();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
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

    private void autoLoginToServer(final String userId) throws Exception {

        final String URL = "http://52.41.19.232/getUser";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", userId);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(Login.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        User.getInstance().setUserId(response.getString("userId"));
                        User.getInstance().setAuth(response.getInt("auth"));
                        User.getInstance().setNursingHomeId(response.getString("nursingHome"));

                        SharedPreferences.Editor editor = userSession.edit();
                        editor.putString("userId", User.getInstance().getUserId());
                        editor.apply();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
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
