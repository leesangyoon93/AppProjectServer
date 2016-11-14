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

    private int tabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                frag_Notice tabFragment1 = new frag_Notice();
                return tabFragment1;
            case 1:
                frag_Schedule tabFragment2 = new frag_Schedule();
                return tabFragment2;
            case 2:
                frag_Gallery tabFragment3 = new frag_Gallery();
                return tabFragment3;
            case 3:
                frag_QA tabFragment4 = new frag_QA();
                return tabFragment4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
