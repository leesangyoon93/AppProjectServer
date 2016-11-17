package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import whdghks913.tistory.floatingactionbutton.FloatingActionButton;



/**
 * Created by daddyslab on 2016. 11. 3..
 */
public class ShowPatient extends AppCompatActivity implements AdapterView.OnItemClickListener {
    AdapterCategoryGrid adapterCategoryGrid ;
    ArrayList<JSONObject> categories = new ArrayList<JSONObject>();
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_showpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        try {
            getPatientToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gridView = (GridView) findViewById(R.id.gridView_category);

        categories.clear();

        try {
            getCategoriesToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterCategoryGrid = new AdapterCategoryGrid(ShowPatient.this, categories);
        adapterCategoryGrid.notifyDataSetChanged();
        gridView.setAdapter(adapterCategoryGrid);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_showpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(ShowPatient.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_editCategory:
                intent = new Intent(ShowPatient.this, EditCategory.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowPatient.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void getPatientToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        String URL = "http://52.41.19.232/getPatient";

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(ShowPatient.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Patient.getInstance().setId(response.getString("_id"));
                        Patient.getInstance().setWorkerId(response.getString("worker"));
                        Patient.getInstance().setProtectorId(response.getString("protector"));
                        Patient.getInstance().setPatientName(response.getString("patientName"));
                        Patient.getInstance().setBirthday(response.getString("birthday"));
                        Patient.getInstance().setRelation(response.getString("relation"));
                        Patient.getInstance().setRoomNumber(response.getString("roomNumber"));
                        Patient.getInstance().setImage(response.getString("image"));
                        Patient.getInstance().setGender(response.getString("gender"));
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

    private void getCategoriesToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getCategories?patientId=%s",
                URLEncoder.encode(Patient.getInstance().getId(), "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(ShowPatient.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        categories.add(response.optJSONObject(i));
                        adapterCategoryGrid.notifyDataSetChanged();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
