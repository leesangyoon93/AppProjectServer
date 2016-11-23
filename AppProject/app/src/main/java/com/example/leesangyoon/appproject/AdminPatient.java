package com.example.leesangyoon.appproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import whdghks913.tistory.floatingactionbutton.FloatingActionButton;


/**
 * Created by daddyslab on 2016. 11. 1..
 */

public class AdminPatient extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView patientList;
    AdapterPatientList adapterPatientList;
    ArrayList<JSONObject> patients = new ArrayList<JSONObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_adminpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        patientList = (ListView) findViewById(R.id.listView_patient);
        patientList.setOnItemClickListener(this);

        FloatingActionButton mFloatingButton = (FloatingActionButton) findViewById(R.id.mFloatingActionButton);
        mFloatingButton.attachToListView(patientList);

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPatient.this, CreatePatient.class);
                startActivity(intent);
            }
        });

        patients.clear();

        try {
            getPatientsToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterPatientList = new AdapterPatientList(AdminPatient.this, patients);
        adapterPatientList.notifyDataSetChanged();

        patientList.setAdapter(adapterPatientList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(AdminPatient.this, MainActivity.class);
                startActivity(intent);
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(AdminPatient.this, EditPatient.class);
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

    private void getPatientsToServer() throws Exception {

        String URL = String.format("http://52.41.19.232/getPatients?nursingHomeId=%s&userId=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"),
                URLEncoder.encode(User.getInstance().getUserId(), "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(AdminPatient.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        patients.add(response.optJSONObject(i));
                        adapterPatientList.notifyDataSetChanged();
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
}
