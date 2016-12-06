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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        TextView patientAge = (TextView)convertView.findViewById(R.id.text_age);
        TextView patientRoomNumber = (TextView)convertView.findViewById(R.id.text_patientRoomNumber);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        try {
            patientName.setText(patient.getString("patientName"));
            patientRoomNumber.setText(patient.getString("roomNumber") + "호");
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
            Date d = null;
            try {
                d = format.parse(patient.getString("birthday"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int age = getAgeFromBirthday(d) + 1;
            patientAge.setText(String.valueOf(age) + "세");
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

    public static int getAgeFromBirthday(Date birthday) {
        Calendar birth = new GregorianCalendar();
        Calendar today = new GregorianCalendar();

        birth.setTime(birthday);
        today.setTime(new Date());

        int factor = 0;
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }
        return today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
    }
}
