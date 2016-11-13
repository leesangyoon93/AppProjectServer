package com.example.leesangyoon.appproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class AdapterPatientList extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> patients;

    public AdapterPatientList(Context context, ArrayList<JSONObject> patients) {
        this.patients = patients;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return patients.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return patients.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject patient = patients.get(position);

        convertView = mInflater.inflate(R.layout.list_patient, null);
        TextView patientName = (TextView) convertView.findViewById(R.id.text_patientName);

        try {
            patientName.setText(patient.getString("patientName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
