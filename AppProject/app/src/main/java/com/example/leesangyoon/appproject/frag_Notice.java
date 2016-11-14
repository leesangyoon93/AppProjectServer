package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
public class frag_Notice extends Fragment implements AdapterView.OnItemClickListener {

    ListView noticeList;
    AdapterArticleList adapterArticleList;
    ArrayList<JSONObject> notices = new ArrayList<JSONObject>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        View root = inflater.inflate(R.layout.activity_notice, container, false);


        Article.getInstance().initArticle();

        noticeList = (ListView) root.findViewById(R.id.listView_article);
        noticeList.setOnItemClickListener(this);

        FloatingActionButton mFloatingButton = (FloatingActionButton) root.findViewById(R.id.mFloatingActionButton);
        mFloatingButton.hide();

        notices.clear();

        try {
            getArticlesToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterArticleList = new AdapterArticleList(getActivity(), notices);
        adapterArticleList.notifyDataSetChanged();

        noticeList.setAdapter(adapterArticleList);
        if(container==null)
            return null;

        return root;
    };

    private void createListView() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ShowArticle.class);
        intent.putExtra("path", "notice");
        try {
            Article.getInstance().setId(notices.get(position).getString("_id"));
            Article.getInstance().setContent(notices.get(position).getString("content"));
            Article.getInstance().setAuthor(notices.get(position).getString("author"));
            Article.getInstance().setCommentCount(notices.get(position).getInt("commentCount"));
            Article.getInstance().setDate(notices.get(position).getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void getArticlesToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getArticles?nursingHomeId=%s&path=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"),
                URLEncoder.encode("notice", "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(getActivity(), "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        notices.add(response.optJSONObject(i));
                        adapterArticleList.notifyDataSetChanged();
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
