package com.example.leesangyoon.appproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        CircleImageView patientGender = (CircleImageView)convertView.findViewById(R.id.image_patientGender);

        try {
            patientName.setText(patient.getString("patientName"));
            if(patient.getString("image").equals("-")) {
                if(patient.getString("gender").equals("male")) {
                    patientGender.setImageResource(R.drawable.user_male);
                }
                else {
                    patientGender.setImageResource(R.drawable.user_female);
                }
            }
            else {
                patientGender.setImageBitmap(StringToBitmap(patient.getString("image")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
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
