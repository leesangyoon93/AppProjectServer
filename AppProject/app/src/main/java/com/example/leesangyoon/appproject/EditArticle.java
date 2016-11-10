package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 29..
 */
public class EditArticle extends AppCompatActivity {

    String from = "";
    String path = "";
    EditText title;
    EditText content;
    Class cls = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editarticle);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        path = intent.getStringExtra("path");

        title = (EditText) findViewById(R.id.input_title);
        content = (EditText) findViewById(R.id.input_content);

        content.setText(Article.getInstance().getContent());
        title.setText(Article.getInstance().getTitle());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editarticle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent;
                if (from.equals("list")) {
                    switch (path) {
                        case "notice":
                            cls = Notice.class;
                            break;
                        case "schedule":
                            cls = Schedule.class;
                            break;
                        case "qa":
                            cls = QA.class;
                            break;
                    }
                    intent = new Intent(EditArticle.this, cls);
                    startActivity(intent);
                } else {
                    intent = new Intent(EditArticle.this, ShowArticle.class);
                    intent.putExtra("path", path);
                }
                startActivity(intent);
                super.onBackPressed();
                break;
            case R.id.menu_createArticle:
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(EditArticle.this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        saveArticleToServer(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveArticleToServer(final String path) throws Exception {

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());
        postParam.put("articleId", Article.getInstance().getId());
        postParam.put("title", title.getText().toString());
        postParam.put("content", content.getText().toString());
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("path", path);

        String URL = "http://52.41.19.232/saveArticle";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(EditArticle.this, "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Article.getInstance().setId(response.getString("articleId"));
                        Intent intent = new Intent(EditArticle.this, ShowArticle.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditArticle.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    public void onBackPressed() {

        Intent intent;
        if (from.equals("list")) {
            switch (path) {
                case "notice":
                    cls = Notice.class;
                    break;
                case "schedule":
                    cls = Schedule.class;
                    break;
                case "qa":
                    cls = QA.class;
                    break;
            }
            intent = new Intent(EditArticle.this, cls);
            startActivity(intent);
        } else {
            intent = new Intent(EditArticle.this, ShowArticle.class);
            intent.putExtra("path", path);
        }
        startActivity(intent);
        super.onBackPressed();
    }


}
