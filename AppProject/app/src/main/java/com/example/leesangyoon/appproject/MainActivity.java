package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    // 메뉴 달때 유저한테 수급자 정보 열람 버튼을 제공해주자. 그거 누르면 showPatient 로 가서 날짜별로 정보 열람할수있고, 수정 안됨.
    // 이부분이 가장 핵심기능, 개 빡셀듯

    // 갤러리 표시 제대로 해주고 밑에꺼 ㄲㄲㄲㄲ
    // 1. adminPatient 에 리스트뷰 달고, 어떻게 나타낼건지.
    // 2. createPatient 로 보호자/환자 정보 추가하는거 구현.
    // 3. showPatient 로 보호자/환자 정보 나타내기 ( 이부분이 막연함 )
    // 4. editPatient 구현. 정보를 나타낼 수 있으면 당연히 수정할 수 있겟지?
    // 5. 정보 추가가 되면 유저프로필에 같이 나오게 해주자.

    // 다른 요양원인데 요양사 아이디가 같으면??.........
    // 게시판에 검색버튼 추가 ( 추가사항 )

    // 공지 게시판 완벽구현 되면 나머지 게시판 두개 마저 하기. UI에 따라 수정사항 생길 수 있으니 게시판 기능 끝나면 다른거 하자.

    // 타이틀 제대로.
    BackPressCloseHandler backPressCloseHandler;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(User.getInstance().getNursingHomeName());
        //actionBar.setDisplayHomeAsUpEnabled(true);
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

        backPressCloseHandler = new BackPressCloseHandler(this);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_patient);
        mRecyclerView.setLayoutManager(layoutManager);
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
            case R.id.menu_adminPatient:
                intent = new Intent(MainActivity.this, AdminPatient.class);
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
//            case android.R.id.home:
//                backPressCloseHandler.onBackPressed();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}