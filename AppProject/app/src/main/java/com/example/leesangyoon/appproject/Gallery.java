package com.example.leesangyoon.appproject;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import whdghks913.tistory.floatingactionbutton.FloatingActionButton;

/**
 * Created by daddyslab on 2016. 10. 13..
 */
public class Gallery extends AppCompatActivity implements AdapterView.OnItemClickListener {
    AdapterGalleryGrid adapterGalleryGrid;
    ArrayList<JSONObject> gallery = new ArrayList<JSONObject>();
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_gallery);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        GallerySingleton.getInstance().initGallery();

        gridView = (GridView) findViewById(R.id.gridView_gallery);

        if (User.getInstance().getAuth() == 1) {
            FloatingActionButton mFloatingButton = (FloatingActionButton) findViewById(R.id.mFloatingActionButton);
            mFloatingButton.attachToListView(gridView);

            mFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Gallery.this, EditGallery.class);
                    intent.putExtra("from", "list");
                    startActivity(intent);
                }
            });
        }


        gallery.clear();

        try {
            getGalleryToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterGalleryGrid = new AdapterGalleryGrid(Gallery.this, gallery);
        adapterGalleryGrid.notifyDataSetChanged();
        gridView.setAdapter(adapterGalleryGrid);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(Gallery.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_searchGallery:
                // 검색
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Gallery.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void getGalleryToServer() throws Exception {

        String URL = String.format("http://52.41.19.232/getArticles?nursingHomeId=%s&path=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"), URLEncoder.encode("gallery", "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(Gallery.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        gallery.add(response.optJSONObject(i));
                        adapterGalleryGrid.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("development", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        volley.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Gallery.this, ShowGallery.class);
        try {
            GallerySingleton.getInstance().setId(gallery.get(position).getString("_id"));
            GallerySingleton.getInstance().setContent(gallery.get(position).getString("content"));
            GallerySingleton.getInstance().setAuthor(gallery.get(position).getString("author"));
            GallerySingleton.getInstance().setCommentCount(gallery.get(position).getInt("commentCount"));
            GallerySingleton.getInstance().setDate(gallery.get(position).getString("date"));
            GallerySingleton.getInstance().setImage(gallery.get(position).getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
