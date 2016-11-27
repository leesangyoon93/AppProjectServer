package com.example.leesangyoon.appproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int num;

    public PagerAdapter(FragmentManager fm, int num) {
        super(fm);
        this.num = num;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                frag_Notice tab1 = new frag_Notice(); // Fragment 는 알아서 만들자
                return tab1;
            case 1:
                frag_Schedule tab2 = new frag_Schedule();
                return tab2;
            case 2:
                frag_Gallery tab3 = new frag_Gallery();
                return tab3;
            case 3:
                frag_QA tab4 = new frag_QA();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
