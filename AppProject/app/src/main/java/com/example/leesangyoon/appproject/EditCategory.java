package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 11. 17..
 */
public class EditCategory extends AppCompatActivity {
    Switch meal, clean, activity, moveTrain, comment, restRoom, medicine, mentalTrain, physicalCare;
    ArrayList<Switch> switchs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editcategory);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        meal = (Switch)findViewById(R.id.switch_meal);
        clean = (Switch)findViewById(R.id.switch_bodyClean);
        activity = (Switch)findViewById(R.id.switch_activity);
        moveTrain = (Switch)findViewById(R.id.switch_moveTrain);
        comment = (Switch)findViewById(R.id.switch_comment);
        restRoom = (Switch)findViewById(R.id.switch_restRoom);
        medicine = (Switch)findViewById(R.id.switch_medicine);
        mentalTrain = (Switch)findViewById(R.id.switch_mentalTrain);
        physicalCare = (Switch)findViewById(R.id.switch_physicalCare);
        switchs.add(meal);
        switchs.add(clean);
        switchs.add(activity);
        switchs.add(moveTrain);
        switchs.add(comment);
        switchs.add(restRoom);
        switchs.add(medicine);
        switchs.add(mentalTrain);
        switchs.add(physicalCare);

        try {
            getCategoryStateToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editcategory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(EditCategory.this, ShowPatient.class);
                startActivity(intent);
                break;
            case R.id.menu_saveCategory:
                try {
                    saveCategoryStateToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.menu_addCategory:
                //
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditCategory.this, ShowPatient.class);
        startActivity(intent);
        super.onBackPressed();
    }

    // 여기 해야함. 디비에 저장되있는 카테고리 활성화 상태 제이슨으로 만들어서 보내주자.
    private void getCategoryStateToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getCategoryState?patientId=%s",
                URLEncoder.encode(Patient.getInstance().getId(), "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(EditCategory.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            Log.e("adsf", response.optJSONObject(i).getString("state"));
                            if(response.optJSONObject(i).getString("state").equals("true")) {
                                switchs.get(i).setChecked(true);
                            }
                            else {
                                switchs.get(i).setChecked(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
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

    // 현재 체크상태 보내서 디비에 업데이트 해주자.
    private void saveCategoryStateToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        String URL = "http://52.41.19.232/saveCategoryState";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("patientId", Patient.getInstance().getId());
        postParam.put("mealCheck", String.valueOf(meal.isChecked()));
        postParam.put("cleanCheck", String.valueOf(clean.isChecked()));
        postParam.put("activityCheck", String.valueOf(activity.isChecked()));
        postParam.put("moveTrainCheck", String.valueOf(moveTrain.isChecked()));
        postParam.put("commentCheck", String.valueOf(comment.isChecked()));
        postParam.put("restRoomCheck", String.valueOf(restRoom.isChecked()));
        postParam.put("medicineCheck", String.valueOf(medicine.isChecked()));
        postParam.put("mentalTrainCheck", String.valueOf(mentalTrain.isChecked()));
        postParam.put("physicalCareCheck", String.valueOf(physicalCare.isChecked()));

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(EditCategory.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(EditCategory.this, "카테고리 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditCategory.this, ShowPatient.class);
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
