package com.example.leesangyoon.appproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        // 텍스트를 비트맵으로 변환하고 뿌려주면됨.
        try {
            title.setText(category.getString("title"));
            content.setText(category.getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

//    public static Bitmap StringToBitmap(String encodedString) {
//        try {
//            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (NullPointerException e) {
//            e.getMessage();
//            return null;
//        } catch (OutOfMemoryError e) {
//            return null;
//        }
//    }
}
