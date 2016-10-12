package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by daddyslab on 2016. 10. 12..
 */
public class SettingNursingHome extends AppCompatActivity {

    TextView nursingHomeName, nursingHomeAddress, nursingHomePhoneNumber, adminName, adminPhoneNumber, adminId;
    Button logoutButton;

    SharedPreferences userSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_settingnursinghome);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        nursingHomeName = (TextView)findViewById(R.id.text_nursingHomeName);
        nursingHomeAddress = (TextView)findViewById(R.id.text_nursingHomeAddress);
        nursingHomePhoneNumber = (TextView)findViewById(R.id.text_nursingHomePhoneNumber);
        adminName = (TextView)findViewById(R.id.text_adminName);
        adminPhoneNumber = (TextView)findViewById(R.id.text_adminPhoneNumber);
        adminId = (TextView)findViewById(R.id.text_adminId);

        nursingHomeName.setText(User.getInstance().getNursingHomeName());
        nursingHomeAddress.setText(User.getInstance().getNursingHomeAddress());
        nursingHomePhoneNumber.setText(User.getInstance().getNursingHomePhoneNumber());
        adminName.setText(User.getInstance().getUserName());
        adminPhoneNumber.setText(User.getInstance().getPhoneNumber());
        adminId.setText(User.getInstance().getUserId());

        logoutButton = (Button)findViewById(R.id.btn_logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    logoutToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingNursingHome.this, ViewWorker.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void logoutToServer() throws Exception {

        String URL = String.format("http://52.41.19.232/logout");

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {

                        SharedPreferences.Editor editor = userSession.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(SettingNursingHome.this, Login.class);
                        startActivity(intent);

                        Toast.makeText(SettingNursingHome.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        //서버연결에 실패하였습니다.
                        Toast.makeText(SettingNursingHome.this, "알 수 없는 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("development", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        volley.getInstance().addToRequestQueue(req);
    }

}
