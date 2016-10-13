package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

// 비밀번호 변경 다이얼로그에 입력 1개밖에 안뜸.
// 비밀번호 변경 서버 api 구현

public class MainActivity extends AppCompatActivity {

    // 수급자 관리 또는 정보열람 버튼 넣기
    // 보호자 프로필일 경우에는 연결된 수급자 정보까지 프로필에 띄워주기, 요양사는 그냥 자기정보만.??

    BackPressCloseHandler backPressCloseHandler;
    int mCurrentFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent intent = getIntent();
        mCurrentFragmentIndex = intent.getIntExtra("fragNum", 0);
        fragmentReplace(mCurrentFragmentIndex);
    }

    public void fragmentReplace(int index) {
        Fragment frag;

        frag = getFragment(index);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragments, frag);
        transaction.commit();
    }

    private Fragment getFragment(int index) {
        Fragment frag = null;

        switch(index) {
            case 0:
                frag = new frag_GroupMain();
                break;
            case 1:
                frag = new frag_Intro();
                break;
            case 2:
                frag = new frag_Notice();
                break;
            case 3:
                frag = new frag_Schedule();
                break;
            case 4:
                frag = new frag_Gallery();
                break;
            case 5:
                frag = new frag_QA();
                break;
        }
        return frag;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_home:
                mCurrentFragmentIndex = 0;
                break;
            case R.id.menu_intro:
                mCurrentFragmentIndex = 1;
                break;
            case R.id.menu_notice:
                mCurrentFragmentIndex = 2;
                break;
            case R.id.menu_schedule:
                mCurrentFragmentIndex = 3;
                break;
            case R.id.menu_gallery:
                mCurrentFragmentIndex = 4;
                break;
            case R.id.menu_QA:
                mCurrentFragmentIndex = 5;
                break;
            case R.id.menu_userSetting:
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                backPressCloseHandler.onBackPressed();
                break;
        }
        fragmentReplace(mCurrentFragmentIndex);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}