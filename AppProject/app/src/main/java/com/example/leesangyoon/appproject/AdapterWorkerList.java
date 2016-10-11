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
 * Created by daddyslab on 2016. 10. 12..
 */
public class AdapterWorkerList extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> workers;

    public AdapterWorkerList(Context context, ArrayList<JSONObject> workers) {
        this.workers = workers;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return workers.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return workers.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject worker = workers.get(position);

        convertView = mInflater.inflate(R.layout.list_worker, null);
        TextView textRole = (TextView) convertView.findViewById(R.id.text_workerRole);
        TextView textName = (TextView) convertView.findViewById(R.id.text_workerName);
        TextView textId = (TextView) convertView.findViewById(R.id.text_workerId);

        try {
            textRole.setText(worker.getString("role"));
            textId.setText(worker.getString("userId"));
            textName.setText(worker.getString("userName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
