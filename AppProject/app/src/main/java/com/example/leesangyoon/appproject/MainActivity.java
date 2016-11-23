package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    BackPressCloseHandler backPressCloseHandler;
    RecyclerView mRecyclerView;
    ArrayList<JSONObject> patients = new ArrayList<JSONObject>();
    AdapterPatientRecycle adapterPatientRecycle;
    TextView adminPatient;
    Button showPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(User.getInstance().getNursingHomeName());
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        adminPatient = (TextView)findViewById(R.id.btn_adminPatient);
        showPatient = (Button)findViewById(R.id.btn_showPatient);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView_patient);
        mRecyclerView.setLayoutManager(layoutManager);

        if(User.getInstance().getAuth() == 1) {
            adminPatient.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            showPatient.setVisibility(View.VISIBLE);
            try {
                getPatientToServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, frag_Notice.class.getName()));
        fragments.add(Fragment.instantiate(this, frag_Schedule.class.getName()));
        fragments.add(Fragment.instantiate(this, frag_Gallery.class.getName()));
        fragments.add(Fragment.instantiate(this, frag_QA.class.getName()));
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);

        final ViewPager pager = (ViewPager)findViewById(R.id.mainPager);
        pager.setAdapter(adapter);

        final PagerTabStrip header = (PagerTabStrip)findViewById(R.id.pager_header);
        header.setTabIndicatorColor(10667642);

        backPressCloseHandler = new BackPressCloseHandler(this);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, EditPatient.class);
                        try {
                            Patient.getInstance().setId(patients.get(position).getString("_id"));
                            Patient.getInstance().setWorkerId(patients.get(position).getString("worker"));
                            Patient.getInstance().setProtectorId(patients.get(position).getString("protector"));
                            Patient.getInstance().setPatientName(patients.get(position).getString("patientName"));
                            Patient.getInstance().setBirthday(patients.get(position).getString("birthday"));
                            Patient.getInstance().setRelation(patients.get(position).getString("relation"));
                            Patient.getInstance().setRoomNumber(patients.get(position).getString("roomNumber"));
                            Patient.getInstance().setImage(patients.get(position).getString("image"));
                            Patient.getInstance().setGender(patients.get(position).getString("gender"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        patients.clear();

        try {
            getPatientsToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterPatientRecycle = new AdapterPatientRecycle(patients);
        mRecyclerView.setAdapter(adapterPatientRecycle);

        adminPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminPatient.class);
                startActivity(intent);
            }
        });

        showPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowPatient.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(User.getInstance().getAuth() != 2)
            getMenuInflater().inflate(R.menu.menu_adminmain, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_intro:
                intent = new Intent(MainActivity.this, Intro.class);
                startActivity(intent);
                break;
            case R.id.menu_notice:
                intent = new Intent(MainActivity.this, Notice.class);
                startActivity(intent);
                break;
            case R.id.menu_schedule:
                intent = new Intent(MainActivity.this, Schedule.class);
                startActivity(intent);
                break;
            case R.id.menu_gallery:
                intent = new Intent(MainActivity.this, Gallery.class);
                startActivity(intent);
                break;
            case R.id.menu_QA:
                intent = new Intent(MainActivity.this, QA.class);
                startActivity(intent);
                break;
            case R.id.menu_userProfile:
                intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void getPatientsToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getPatients?nursingHomeId=%s&userId=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"),
                URLEncoder.encode(User.getInstance().getUserId(), "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(MainActivity.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        patients.add(response.optJSONObject(i));
                        adapterPatientRecycle.notifyDataSetChanged();
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
                            Toast.makeText(MainActivity.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
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
}