package com.example.leesangyoon.appproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class frag_Schedule extends Fragment {
    View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        root = inflater.inflate(R.layout.fragment_schedule, container, false);

        return root;
    }
}
