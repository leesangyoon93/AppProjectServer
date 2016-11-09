package com.example.leesangyoon.appproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daddyslab on 2016. 10. 31..
 */
public class AdapterGalleryGrid extends BaseAdapter {

    LayoutInflater mInflater;
    ArrayList<JSONObject> gallery;

    public AdapterGalleryGrid(Context context, ArrayList<JSONObject> gallery) {
        this.gallery = gallery;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return gallery.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return gallery.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject galleries = gallery.get(position);

        byte[] decodedString;

        convertView = mInflater.inflate(R.layout.grid_gallery, null);
        TextView title = (TextView) convertView.findViewById(R.id.grid_title);
        ImageView image = (ImageView) convertView.findViewById(R.id.grid_image);


        // 텍스트를 비트맵으로 변환하고 뿌려주면됨.
        try {
            decodedString = Base64.decode(galleries.getString("image"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            title.setText(galleries.getString("title"));
            image.setImageBitmap(decodedByte);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
