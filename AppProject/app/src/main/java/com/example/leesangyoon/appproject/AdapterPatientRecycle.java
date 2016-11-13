package com.example.leesangyoon.appproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class AdapterPatientRecycle extends RecyclerView.Adapter<AdapterPatientRecycle.ListItemViewHolder> {

    ArrayList<JSONObject> patients;

    public AdapterPatientRecycle(ArrayList<JSONObject> patients) {
        patients = patients;
    }


    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_patient, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        JSONObject patient = patients.get(position);

        try {
            holder.patientName.setText(patient.getString("patientName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            patientName = (TextView) itemView.findViewById(R.id.patient_name);
        }
    }
}
