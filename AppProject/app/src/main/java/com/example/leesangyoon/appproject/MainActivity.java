package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    // 게시판에 키보드좀 어떻게 하자;
    // 권한별로 게시판 기능 제한걸기. 빡셀듯; 생각좀 해야함.
    // 공지 게시판 완벽구현 되면 나머지 게시판 두개 마저 하기. UI에 따라 수정사항 생길 수 있으니 게시판 기능 끝나면 다른거 하자.

    // 수급자 관리 또는 정보열람 버튼 넣기
    // 보호자 프로필일 경우에는 연결된 수급자 정보까지 프로필에 띄워주기, 요양사는 그냥 자기정보만.??

    ImageButton homeButton, profileButton;

    // 타이틀 제대로.
    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_actionbar);

        backPressCloseHandler = new BackPressCloseHandler(this);

        profileButton = (ImageButton)findViewById(R.id.btn_profile);
        homeButton = (ImageButton)findViewById(R.id.btn_home);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}