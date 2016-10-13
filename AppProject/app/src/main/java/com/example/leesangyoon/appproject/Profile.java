package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
 * Created by daddyslab on 2016. 10. 12..
 */
public class Profile extends AppCompatActivity {

    SharedPreferences userSession;

    TextView userId, userName, userPhoneNumber;
    Button changePasswordButton;

    private String currentPassword, newPassword1, newPassword2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        userId = (TextView)findViewById(R.id.text_profileUserId);
        userName = (TextView)findViewById(R.id.text_profileUserName);
        userPhoneNumber = (TextView)findViewById(R.id.text_profileUserPhoneNumber);

        userId.setText(User.getInstance().getUserId());
        userName.setText(User.getInstance().getUserName());
        userPhoneNumber.setText(User.getInstance().getPhoneNumber());

        changePasswordButton = (Button)findViewById(R.id.btn_changePassword);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                LinearLayout layout = new LinearLayout(Profile.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText currentPw = new EditText(Profile.this);
                currentPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                currentPw.setLines(1);
                currentPw.setHint("현재 비밀번호");

                final EditText newPw1 = new EditText(Profile.this);
                newPw1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPw1.setLines(1);
                newPw1.setHint("변경 할 비밀번호");

                final EditText newPw2 = new EditText(Profile.this);
                newPw2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPw2.setLines(1);
                newPw2.setHint("비밀번호 확인");

                layout.addView(currentPw);
                layout.addView(newPw1);
                layout.addView(newPw2);

//                TextView title = new TextView(this);
//                title.setText("Custom Centered Title");
//                title.setBackgroundColor(Color.DKGRAY);
//                title.setPadding(10, 10, 10, 10);
//                title.setGravity(Gravity.CENTER);
//                title.setTextColor(Color.WHITE);
//                title.setTextSize(20);
//
//                builder.setCustomTitle(title);

                builder.setTitle("비밀번호 변경")
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currentPassword = currentPw.getText().toString();
                                newPassword1 = newPw1.getText().toString();
                                newPassword2 = newPw2.getText().toString();
                                if(newPassword1.equals(newPassword2)) {
                                    try {
                                        changePasswordToServer(currentPassword, newPassword1, newPassword2);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Toast.makeText(Profile.this, "새로운 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                                // yes 버튼 누르면
//                                try {
//                                    deleteUserToServer(workerId.getText().toString());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // no 버튼 누르면
                            }
                        })
                        .show();
            }
        });
    }

    private void changePasswordToServer(final String currentPassword, final String newPassword1, final String newPassword2) throws Exception {

        final String URL = "http://52.41.19.232/changePassword";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());
        postParam.put("currentPassword", currentPassword);
        postParam.put("newPassword1", newPassword1);
        postParam.put("newPassword2", newPassword2);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(Profile.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("failPw")) {
                            Toast.makeText(Profile.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("success")) {
                            Toast.makeText(Profile.this, "비밀번호 변경 완료.", Toast.LENGTH_SHORT).show();
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
