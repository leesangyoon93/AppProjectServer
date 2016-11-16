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
import android.widget.TextView;
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
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    // 메뉴 달때 유저한테 수급자 정보 열람 버튼을 제공해주자. 그거 누르면 showPatient 로 가서 날짜별로 정보 열람할수있고, 수정 안됨.
    // 이부분이 가장 핵심기능, 개 빡셀듯

    // 갤러리 표시 제대로 해주고 밑에꺼 ㄲㄲㄲㄲ // 완료
    // 1. adminPatient 에 리스트뷰 달고, 어떻게 나타낼건지. // 완료
    // 2. createPatient 로 보호자/환자 정보 추가하는거 구현. // 완료
    // 3. showPatient 로 보호자/환자 정보 나타내기 ( 이부분이 막연함 )
    // 4. editPatient 구현. 정보를 나타낼 수 있으면 당연히 수정할 수 있겟지?
    // 5. 정보 추가가 되면 유저프로필에 같이 나오게 해주자.

    // 다른 요양원인데 요양사 아이디가 같으면??.........
    // 게시판에 검색버튼 추가 ( 추가사항 )

    // 공지 게시판 완벽구현 되면 나머지 게시판 두개 마저 하기. UI에 따라 수정사항 생길 수 있으니 게시판 기능 끝나면 다른거 하자.

    // 타이틀 제대로.
    BackPressCloseHandler backPressCloseHandler;
    RecyclerView mRecyclerView;
    ArrayList<JSONObject> patients = new ArrayList<JSONObject>();
    AdapterPatientRecycle adapterPatientRecycle;
    TextView adminPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(User.getInstance().getNursingHomeName());
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

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

        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView_patient);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        try {
                            Toast.makeText(MainActivity.this, patients.get(position).getString("patientName"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
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

        adminPatient = (TextView)findViewById(R.id.btn_adminPatient);
        adminPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminPatient.class);
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
            case R.id.menu_showPatient:
                intent = new Intent(MainActivity.this, ShowPatient.class);
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

}