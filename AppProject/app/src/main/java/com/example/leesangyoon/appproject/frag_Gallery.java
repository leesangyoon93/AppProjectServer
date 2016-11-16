package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
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
 * Created by daddyslab on 2016. 11. 13..
 */
public class frag_Gallery extends Fragment implements AdapterView.OnItemClickListener {
    GridView galleryList;
    AdapterGalleryGrid adapterGalleryGrid;
    ArrayList<JSONObject> galleries = new ArrayList<JSONObject>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View root = inflater.inflate(R.layout.activity_gallery, container, false);


        Article.getInstance().initArticle();

        galleryList = (GridView) root.findViewById(R.id.gridView_gallery);
        galleryList.setOnItemClickListener(this);

        FloatingActionButton mFloatingButton = (FloatingActionButton) root.findViewById(R.id.mFloatingActionButton);
        mFloatingButton.hide();

        galleries.clear();

        try {
            getGalleryToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterGalleryGrid = new AdapterGalleryGrid(getActivity(), galleries);
        adapterGalleryGrid.notifyDataSetChanged();

        galleryList.setAdapter(adapterGalleryGrid);
        if(container==null)
            return null;

        return root;
    };

    private void createListView() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ShowGallery.class);
        try {
            GallerySingleton.getInstance().setId(galleries.get(position).getString("_id"));
            GallerySingleton.getInstance().setContent(galleries.get(position).getString("content"));
            GallerySingleton.getInstance().setAuthor(galleries.get(position).getString("author"));
            GallerySingleton.getInstance().setCommentCount(galleries.get(position).getInt("commentCount"));
            GallerySingleton.getInstance().setDate(galleries.get(position).getString("date"));
            GallerySingleton.getInstance().setImage(galleries.get(position).getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void getGalleryToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getArticles?nursingHomeId=%s&path=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"), URLEncoder.encode("gallery", "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(getActivity(), "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        galleries.add(response.optJSONObject(i));
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
}
