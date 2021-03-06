package com.example.leesangyoon.appproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    BackPressCloseHandler backPressCloseHandler;

    SharedPreferences userSession;
    LinearLayout mainLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        mainLayout = (LinearLayout)findViewById(R.id.login_layout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userId.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
        });

        loginButton = (Button) findViewById(R.id.btn_login);
        registerButton = (Button) findViewById(R.id.btn_registerNursinHome);

        userId = (EditText) findViewById(R.id.input_userId);
        password = (EditText) findViewById(R.id.input_password);
        userId.setLines(1);
        password.setLines(1);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        if(userSession.contains("userId")){
            try {
                autoLoginToServer(userSession.getString("userId", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        backPressCloseHandler = new BackPressCloseHandler(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
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
                            Toast.makeText(Login.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        User.getInstance().setUserId(response.getString("userId"));
                        User.getInstance().setUserName(response.getString("userName"));
                        User.getInstance().setPhoneNumber(response.getString("phoneNumber"));
                        User.getInstance().setGender(response.getString("gender"));
                        User.getInstance().setAuth(response.getInt("auth"));
                        User.getInstance().setNursingHomeId(response.getString("nursingHome"));

                        getNursingHomeToServer();
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
                        User.getInstance().setUserName(response.getString("userName"));
                        User.getInstance().setPhoneNumber(response.getString("phoneNumber"));
                        User.getInstance().setGender(response.getString("gender"));
                        User.getInstance().setAuth(response.getInt("auth"));
                        User.getInstance().setNursingHomeId(response.getString("nursingHome"));

                        getNursingHomeToServer();

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

    private void getNursingHomeToServer() throws Exception {

        final String URL = "http://52.41.19.232/getNursingHome";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());

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
                        User.getInstance().setNursingHomeName(response.getString("homeName"));
                        User.getInstance().setNursingHomeAddress(response.getString("address"));
                        User.getInstance().setNursingHomePhoneNumber(response.getString("phoneNumber"));

                        SharedPreferences.Editor editor = userSession.edit();
                        editor.putString("userId", User.getInstance().getUserId());
                        editor.apply();

                        if(User.getInstance().getAuth() == 0) {
                            Intent intent = new Intent(Login.this, ViewWorker.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
                        // 로그인 하면 가입되어잇는 요양원 객체 받아와서 싱글톤에 저장
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
