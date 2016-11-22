package com.example.leesangyoon.appproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daddyslab on 2016. 11. 23..
 */
public class AdapterCategoryList extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> categories;

    public AdapterCategoryList(Context context, ArrayList<JSONObject> categories) {
        this.categories = categories;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return categories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.list_category, null);

        JSONObject category = categories.get(position);

        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.image_category);
        TextView title = (TextView)convertView.findViewById(R.id.title_category);
        Switch s = (Switch)convertView.findViewById(R.id.switch_category);

        try {
            title.setText(category.getString("title"));
            s.setChecked(category.getBoolean("state"));
            switch(String.valueOf(category.getInt("num"))) {
                case "0":
                    image.setImageResource(R.mipmap.meal_icon);
                    break;
                case "1":
                    image.setImageResource(R.mipmap.shower_icon);
                    break;
                case "2":
                    image.setImageResource(R.mipmap.walking_icon);
                    break;
                case "3":
                    image.setImageResource(R.mipmap.body_icon);
                    break;
                case "4":
                    image.setImageResource(R.mipmap.comment_icon);
                    break;
                case "5":
                    image.setImageResource(R.mipmap.toilet_icon);
                    break;
                case "6":
                    image.setImageResource(R.mipmap.medicine_icon);
                    break;
                case "7":
                    image.setImageResource(R.mipmap.mental_icon);
                    break;
                case "8":
                    image.setImageResource(R.mipmap.physical_icon);
                    break;
                default:
                    image.setImageResource(R.mipmap.custom_icon);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
