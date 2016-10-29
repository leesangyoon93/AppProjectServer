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
 * Created by daddyslab on 2016. 10. 29..
 */
public class AdapterCommentList extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> comments;

    public AdapterCommentList(Context context, ArrayList<JSONObject> comments) {
        this.comments = comments;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return comments.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.list_comment, null);

        JSONObject comment = comments.get(position);

        TextView content = (TextView)convertView.findViewById(R.id.comment_content);
        TextView author = (TextView)convertView.findViewById(R.id.comment_author);

        try {
            content.setText(comment.getString("content"));
            author.setText(comment.getString("author"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
