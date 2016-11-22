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
        ImageView image = (ImageView)convertView.findViewById(R.id.btn_editCategoryContent);
        LinearLayout wrap_category = (LinearLayout)convertView.findViewById(R.id.wrap_category);

        try {
            title.setText(category.getString("title"));
            content.setText(category.getString("content"));
            if (User.getInstance().getAuth() == 1 && category.getString("content").equals("")) {
                image.setVisibility(View.VISIBLE);
            }
//            if(category.getString("state").equals("false")) {
//                wrap_category.setVisibility(View.GONE);
//
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            switch(String.valueOf(position)) {
//                case "0":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("title"));
//                        content.setText(category.getString("content"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("content").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "1":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("cleanTitle"));
//                        content.setText(category.getString("clean"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("clean").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "2":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("activityTitle"));
//                        content.setText(category.getString("activity"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("activity").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "3":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("moveTrainTitle"));
//                        content.setText(category.getString("moveTrain"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("moveTrain").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "4":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("commentTitle"));
//                        content.setText(category.getString("comment"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("comment").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "5":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("restRoomTitle"));
//                        content.setText(category.getString("restRoom"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("restRoom").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "6":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("medicineTitle"));
//                        content.setText(category.getString("medicine"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("medicine").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "7":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("mentalTrainTitle"));
//                        content.setText(category.getString("mentalTrain"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("mentalTrain").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//                case "8":
//                    if(category.getString("state").equals("true")) {
//                        wrap_category.setVisibility(View.VISIBLE);
//                        title.setText(category.getString("physicalCareTitle"));
//                        content.setText(category.getString("physicalCare"));
//                        if (User.getInstance().getAuth() == 1 && category.getString("physicalCare").equals("")) {
//                            image.setVisibility(View.VISIBLE);
//                        }
//                    }
//                    break;
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return convertView;
    }
}
