package com.example.leesangyoon.appproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    LinearLayout wrap_worker, wrap_user;
    TextView userId, userName, patientName, roomNumber, workerName, workerPhoneNumber, nursingHomePhoneNumber;
    TextView name, workerId, phoneNumber;
    Button changePasswordButton;
    ImageView profileGender;

    private String currentPassword, newPassword1, newPassword2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        userSession = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        userId = (TextView)findViewById(R.id.text_profileUserId);
        userName = (TextView)findViewById(R.id.text_profileUserName);
        patientName = (TextView)findViewById(R.id.text_patientName);
        roomNumber = (TextView)findViewById(R.id.text_roomNumber);
        workerName = (TextView)findViewById(R.id.text_workerName);
        workerPhoneNumber = (TextView)findViewById(R.id.text_workerPhoneNumber);
        nursingHomePhoneNumber = (TextView)findViewById(R.id.text_nursingHomePhoneNumber);
        profileGender = (ImageView)findViewById(R.id.image_profileGender);
        name = (TextView)findViewById(R.id.text_Name);
        workerId = (TextView)findViewById(R.id.text_Id);
        phoneNumber = (TextView)findViewById(R.id.text_phoneNumber);
        wrap_user = (LinearLayout)findViewById(R.id.wrap_userProfile);
        wrap_worker = (LinearLayout)findViewById(R.id.wrap_workerProfile);

        if(User.getInstance().getAuth() == 2) {
            wrap_worker.setVisibility(View.GONE);
            try {
                getWorkerToServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
            userId.setText(User.getInstance().getUserId());
            userName.setText(User.getInstance().getUserName());
            patientName.setText(Patient.getInstance().getPatientName());
            roomNumber.setText(Patient.getInstance().getRoomNumber());
            nursingHomePhoneNumber.setText(User.getInstance().getNursingHomePhoneNumber());
        }
        else {
            wrap_user.setVisibility(View.GONE);
            name.setText(User.getInstance().getUserName());
            workerId.setText(User.getInstance().getUserId());
            phoneNumber.setText(User.getInstance().getPhoneNumber());
        }

        if(User.getInstance().getGender().equals("male")) {
            profileGender.setImageResource(R.drawable.user_male);
        }
        else {
            profileGender.setImageResource(R.drawable.user_female);
        }

        changePasswordButton = (Button)findViewById(R.id.btn_changePassword);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

                LinearLayout layout = new LinearLayout(Profile.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(Profile.this);
                title.setText("비밀번호 변경");
                title.setBackgroundColor(Color.WHITE);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.BLACK);
                title.setTextSize(25);

                final EditText currentPw = new EditText(Profile.this);
                currentPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                currentPw.setLines(1);
                currentPw.setHint("현재 비밀번호");
                currentPw.setTextSize(16);
                currentPw.setWidth(200);

                final EditText newPw1 = new EditText(Profile.this);
                newPw1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPw1.setLines(1);
                newPw1.setHint("변경 할 비밀번호");
                newPw1.setTextSize(16);
                newPw1.setWidth(200);

                final EditText newPw2 = new EditText(Profile.this);
                newPw2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPw2.setLines(1);
                newPw2.setHint("비밀번호 확인");
                newPw2.setTextSize(16);
                newPw2.setWidth(200);

                layout.addView(currentPw);
                layout.addView(newPw1);
                layout.addView(newPw2);

                builder.setTitle("비밀번호 변경")
                        .setView(layout)
                        .setCancelable(true)
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
                                    Toast.makeText(Profile.this, "변경 할 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });
    }

    private void getWorkerToServer() throws Exception {

        final String URL = "http://52.41.19.232/getWorker";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("patientId", Patient.getInstance().getId());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(Profile.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        workerName.setText(response.getString("userName"));
                        workerPhoneNumber.setText(response.getString("phoneNumber"));
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
                            Toast.makeText(Profile.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_logout:
                new AlertDialog.Builder(Profile.this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // yes 버튼 누르면
                                try {
                                    SharedPreferences.Editor editor = userSession.edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(Profile.this, Login.class);
                                    startActivity(intent);

                                    Toast.makeText(Profile.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // no 버튼 누르면
                            }
                        })
                        .show();

                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
