package com.example.leesangyoon.appproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by daddyslab on 2016. 10. 11..
 */
public class frag_GroupMain extends Fragment {
    View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        root = inflater.inflate(R.layout.fragment_groupmain, container, false);

        return root;
    }
}
