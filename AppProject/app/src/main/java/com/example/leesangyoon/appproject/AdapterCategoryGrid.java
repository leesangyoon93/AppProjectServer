package com.example.leesangyoon.appproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 11. 17..
 */
public class AdapterCategoryGrid extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<JSONObject> categories;

    public AdapterCategoryGrid(Context context, ArrayList<JSONObject> categories) {
        this.categories = categories;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        JSONObject category = categories.get(position);

        convertView = mInflater.inflate(R.layout.grid_category, null);
        TextView title = (TextView) convertView.findViewById(R.id.categoryTitle);
        TextView content = (TextView) convertView.findViewById(R.id.categoryContent);
        ImageView categoryIcon = (ImageView)convertView.findViewById(R.id.img_categoryIcon);
        TextView editContent = (TextView)convertView.findViewById(R.id.text_editCategoryContent);
        try {
            title.setText(category.getString("title"));
            content.setText(category.getString("content"));
            if (User.getInstance().getAuth() == 1 && category.getString("content").equals("")) {
                editContent.setVisibility(View.VISIBLE);
            }
            switch(String.valueOf(category.getInt("num"))) {
                case "0":
                    categoryIcon.setImageResource(R.drawable.meal_icon);
                    break;
                case "1":
                    categoryIcon.setImageResource(R.drawable.shower_icon);
                    break;
                case "2":
                    categoryIcon.setImageResource(R.drawable.walking_icon);
                    break;
                case "3":
                    categoryIcon.setImageResource(R.drawable.body_icon);
                    break;
                case "4":
                    categoryIcon.setImageResource(R.drawable.comment_icon);
                    break;
                case "5":
                    categoryIcon.setImageResource(R.drawable.toilet_icon);
                    break;
                case "6":
                    categoryIcon.setImageResource(R.drawable.medicine_icon);
                    break;
                case "7":
                    categoryIcon.setImageResource(R.drawable.mental_icon);
                    break;
                case "8":
                    categoryIcon.setImageResource(R.drawable.physical_icon);
                    break;
                default:
                    categoryIcon.setImageResource(R.drawable.custom_icon);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
