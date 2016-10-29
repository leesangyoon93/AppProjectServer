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
public class AdapterArticleList extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> articles;

    public AdapterArticleList(Context context, ArrayList<JSONObject> articles) {
        this.articles = articles;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return articles.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject article = articles.get(position);

        convertView = mInflater.inflate(R.layout.list_article, null);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView date = (TextView) convertView.findViewById(R.id.date);

        try {
            title.setText(article.getString("title"));
            author.setText(article.getString("author"));
            date.setText(article.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
