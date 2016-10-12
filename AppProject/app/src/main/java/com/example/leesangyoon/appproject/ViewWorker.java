package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class ViewWorker extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView workerList;
    TextView nursingHomeName;
    Button createWorkerButton, settingNursingHomeButton;

    AdapterWorkerList adapterWorkerList;
    ArrayList<JSONObject> workers = new ArrayList<JSONObject>();

    BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_viewworker);

        backPressCloseHandler = new BackPressCloseHandler(this);

        nursingHomeName = (TextView)findViewById(R.id.text_nursingHomeName);
        createWorkerButton = (Button)findViewById(R.id.btn_createWorker);
        settingNursingHomeButton = (Button)findViewById(R.id.btn_settingNursingHome);

        nursingHomeName.setText(User.getInstance().getNursingHomeName());

        workers.clear();

        try {
            getNursingHomeWorkers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        workerList = (ListView)findViewById(R.id.list_worker);
        workerList.setOnItemClickListener(this);

        adapterWorkerList = new AdapterWorkerList(ViewWorker.this, workers);
        adapterWorkerList.notifyDataSetChanged();

        workerList.setAdapter(adapterWorkerList);

        createWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWorker.this, CreateWorker.class);
                startActivity(intent);
            }
        });

        settingNursingHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewWorker.this, SettingNursingHome.class);
                startActivity(intent);
            }
        });
    }

    private void createListView() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String workerId = null;
        String workerName = null;
        String workerPhoneNumber = null;
        try {
            workerId = adapterWorkerList.getItem(position).getString("userId");
            workerName = adapterWorkerList.getItem(position).getString("userName");
            workerPhoneNumber = adapterWorkerList.getItem(position).getString("phoneNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ViewWorker.this, EditWorker.class);
        intent.putExtra("workerId", workerId);
        intent.putExtra("workerName", workerName);
        intent.putExtra("workerPhoneNumber", workerPhoneNumber);
        startActivity(intent);
    }

    private void getNursingHomeWorkers() throws Exception{

        String URL= String.format("http://52.41.19.232/getNursingHomeWorkers?nursingHomeId=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    workers.add(response.optJSONObject(i));
                    adapterWorkerList.notifyDataSetChanged();
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
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
