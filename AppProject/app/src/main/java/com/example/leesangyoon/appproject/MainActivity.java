package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    BackPressCloseHandler backPressCloseHandler;
    RecyclerView mRecyclerView;
    ArrayList<JSONObject> patients = new ArrayList<JSONObject>();
    AdapterPatientRecycle adapterPatientRecycle;
    TextView adminPatient, showPatient, patientName;
    CircleImageView circleImageView;
    LinearLayout wrap_showPatient;
    TabLayout mainTab;

    TextView mInformationTextView;
    Button mRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(User.getInstance().getNursingHomeName());
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        // 토큰을 가져오는 Button을 정의
        getInstanceIdToken();
        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getInstanceIdToken();
            }
        });

        adminPatient = (TextView)findViewById(R.id.btn_adminPatient);
        showPatient = (TextView) findViewById(R.id.btn_showPatient);
        circleImageView = (CircleImageView)findViewById(R.id.patientImage);
        patientName = (TextView)findViewById(R.id.patientName);
        wrap_showPatient = (LinearLayout)findViewById(R.id.wrap_showPatient);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView_patient);
        mRecyclerView.setLayoutManager(layoutManager);

        if(User.getInstance().getAuth() == 1) {
            adminPatient.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            wrap_showPatient.setVisibility(View.GONE);
        }
        else {
            showPatient.setVisibility(View.VISIBLE);

            try {
                getPatientToServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mainTab = (TabLayout) findViewById(R.id.mainTab);
        mainTab.addTab(mainTab.newTab().setText("공지사항"));
        mainTab.addTab(mainTab.newTab().setText("일정"));
        mainTab.addTab(mainTab.newTab().setText("갤러리"));
        mainTab.addTab(mainTab.newTab().setText("Q&A"));
        mainTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mainTab.setTabTextColors(ColorStateList.valueOf(Color.BLACK));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.mainPager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), mainTab.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTab));
        mainTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

        wrap_showPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowPatient.class);
                startActivity(intent);
            }
        });
    }

    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case QuickstartPreferences.REGISTRATION_READY:
                        // 액션이 READY일 경우
                        mInformationTextView.setVisibility(View.GONE);
                        break;
                    case QuickstartPreferences.REGISTRATION_GENERATING:
                        // 액션이 GENERATING일 경우
                        mInformationTextView.setVisibility(View.VISIBLE);
                        mInformationTextView.setText(getString(R.string.registering_message_generating));
                        break;
                    case QuickstartPreferences.REGISTRATION_COMPLETE:
                        // 액션이 COMPLETE일 경우
                        mRegistrationButton.setText(getString(R.string.registering_message_complete));
                        mRegistrationButton.setEnabled(false);
                        String token = intent.getStringExtra("token");
                        User.getInstance().setToken(token);
                        mInformationTextView.setText(token);
                        break;
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

                        circleImageView.setImageBitmap(StringToBitmap(Patient.getInstance().getImage()));
                        patientName.setText(Patient.getInstance().getPatientName());
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

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}