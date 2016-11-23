package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 11. 17..
 */
public class EditCategory extends AppCompatActivity {
    AdapterCategoryList adapterCategoryList;
    ListView categoryList;
    ArrayList<JSONObject> categories = new ArrayList<>();
    FloatingActionButton floatingActionButton;
    String categoryTitle, date;
    int lyear, lmonth, lday;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editcategory);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        GregorianCalendar calendar = new GregorianCalendar();
        lyear = calendar.get(Calendar.YEAR);
        lmonth = calendar.get(Calendar.MONTH);
        lday = calendar.get(Calendar.DAY_OF_MONTH);
        date = String.valueOf(lyear) + "-" + String.valueOf(lmonth+1) + "-" + String.valueOf(lday);

        categoryList = (ListView)findViewById(R.id.listView_category);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.mFloatingActionButton);
        adapterCategoryList = new AdapterCategoryList(EditCategory.this, categories);
        adapterCategoryList.notifyDataSetChanged();
        categoryList.setAdapter(adapterCategoryList);

        categories.clear();
        try {
            getCategoryStateToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCategory.this);

                LinearLayout layout = new LinearLayout(EditCategory.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText customCategory = new EditText(EditCategory.this);
                customCategory.setHint("카테고리 제목을 입력하세요.");

                layout.addView(customCategory);

                builder.setTitle("카테고리 추가")
                        .setView(layout)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                categoryTitle = customCategory.getText().toString();
                                if(categoryTitle.equals("")) {
                                    Toast.makeText(EditCategory.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    try {
                                        addCategoryToServer();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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

        String URL = String.format("http://52.41.19.232/getCategoryState?patientId=%s&date=%s",
                URLEncoder.encode(Patient.getInstance().getId(), "utf-8"), date);

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(EditCategory.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    categories.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            categories.add(response.getJSONObject(i));
                            adapterCategoryList.notifyDataSetChanged();
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
        for(int i=0; i<categories.size(); i++) {
            String state = String.valueOf(categories.get(i).getBoolean("state"));
            postParam.put(("state" + String.valueOf(i)), state);
        }
        postParam.put("patientId", Patient.getInstance().getId());
//        postParam.put("mealCheck", String.valueOf(meal.isChecked()));
//        postParam.put("cleanCheck", String.valueOf(clean.isChecked()));
//        postParam.put("activityCheck", String.valueOf(activity.isChecked()));
//        postParam.put("moveTrainCheck", String.valueOf(moveTrain.isChecked()));
//        postParam.put("commentCheck", String.valueOf(comment.isChecked()));
//        postParam.put("restRoomCheck", String.valueOf(restRoom.isChecked()));
//        postParam.put("medicineCheck", String.valueOf(medicine.isChecked()));
//        postParam.put("mentalTrainCheck", String.valueOf(mentalTrain.isChecked()));
//        postParam.put("physicalCareCheck", String.valueOf(physicalCare.isChecked()));
        postParam.put("date", date);

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

    private void addCategoryToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        String URL = "http://52.41.19.232/addCategory";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("patientId", Patient.getInstance().getId());
        postParam.put("customTitle", categoryTitle);
        postParam.put("date", date);

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
                            Toast.makeText(EditCategory.this, "카테고리 추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditCategory.this, EditCategory.class);
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
