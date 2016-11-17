package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
 * Created by daddyslab on 2016. 10. 13..
 */
public class QA extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView qaList;
    AdapterArticleList adapterArticleList;
    ArrayList<JSONObject> qas = new ArrayList<JSONObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_qa);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        Article.getInstance().initArticle();

        qaList = (ListView) findViewById(R.id.listView_article);
        qaList.setOnItemClickListener(this);

        FloatingActionButton mFloatingButton = (FloatingActionButton) findViewById(R.id.mFloatingActionButton);
        mFloatingButton.attachToListView(qaList);

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QA.this, EditArticle.class);
                intent.putExtra("from", "list");
                intent.putExtra("path", "qa");
                startActivity(intent);
            }
        });
        mFloatingButton.setVisibility(View.VISIBLE);

        qas.clear();

        try {
            getArticlesToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterArticleList = new AdapterArticleList(QA.this, qas);
        adapterArticleList.notifyDataSetChanged();

        qaList.setAdapter(adapterArticleList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_qa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_searchSchedule:
                // 검색 버튼
                break;
            case android.R.id.home:
                Intent intent = new Intent(QA.this, MainActivity.class);
                startActivity(intent);
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QA.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(QA.this, ShowArticle.class);
        intent.putExtra("path", "schedule");
        try {
            Article.getInstance().setId(qas.get(position).getString("_id"));
            Article.getInstance().setContent(qas.get(position).getString("content"));
            Article.getInstance().setAuthor(qas.get(position).getString("author"));
            Article.getInstance().setCommentCount(qas.get(position).getInt("commentCount"));
            Article.getInstance().setDate(qas.get(position).getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void getArticlesToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        String URL = String.format("http://52.41.19.232/getArticles?nursingHomeId=%s&path=%s",
                URLEncoder.encode(User.getInstance().getNursingHomeId(), "utf-8"),
                URLEncoder.encode("qa", "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(QA.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        qas.add(response.optJSONObject(i));
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
