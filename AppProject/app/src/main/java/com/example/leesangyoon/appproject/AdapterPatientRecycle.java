package com.example.leesangyoon.appproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class AdapterPatientRecycle extends RecyclerView.Adapter<AdapterPatientRecycle.ListItemViewHolder> {

    ArrayList<JSONObject> patients;

    public AdapterPatientRecycle(ArrayList<JSONObject> patients) {
        this.patients = patients;
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
            if(patient.getString("image").equals("-")) {
                if(patient.getString("gender").equals("male")) {
                    holder.patientGender.setImageResource(R.drawable.user_male);
                }
                else {
                    holder.patientGender.setImageResource(R.drawable.user_female);
                }
            }
            else {
                holder.patientGender.setImageBitmap(StringToBitmap(patient.getString("image")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }


    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        CircleImageView patientGender;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            patientName = (TextView) itemView.findViewById(R.id.patient_name);
            patientGender = (CircleImageView) itemView.findViewById(R.id.patient_gender);
        }
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
