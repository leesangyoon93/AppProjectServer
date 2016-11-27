package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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

    AdapterWorkerList adapterWorkerList;
    ArrayList<JSONObject> workers = new ArrayList<JSONObject>();

    BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_viewworker);

        backPressCloseHandler = new BackPressCloseHandler(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        nursingHomeName = (TextView)findViewById(R.id.text_nursingHomeName);

        nursingHomeName.setText(User.getInstance().getNursingHomeName() + " 원장님\n환영합니다.");

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_addWorker:
                Intent intent = new Intent(ViewWorker.this, CreateWorker.class);
                startActivity(intent);
                break;
            case R.id.menu_adminProfile:
                intent = new Intent(ViewWorker.this, SettingNursingHome.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createListView() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String workerId = null;
        String workerName = null;
        String workerPhoneNumber = null;
        String workerGender = null;
        try {
            workerId = adapterWorkerList.getItem(position).getString("userId");
            workerName = adapterWorkerList.getItem(position).getString("userName");
            workerPhoneNumber = adapterWorkerList.getItem(position).getString("phoneNumber");
            workerGender = adapterWorkerList.getItem(position).getString("gender");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ViewWorker.this, EditWorker.class);
        intent.putExtra("workerId", workerId);
        intent.putExtra("workerName", workerName);
        intent.putExtra("workerPhoneNumber", workerPhoneNumber);
        intent.putExtra("workerGender", workerGender);
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
