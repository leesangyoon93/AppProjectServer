package com.example.leesangyoon.appproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 29..
 */
public class ShowArticle extends AppCompatActivity {
    // edit 으로 갈 때 from 넣어줘야함.
    // 삭제도 만들어야함

    ListView commentList;
    TextView content;
    TextView title;
    TextView commentCount;
    TextView date;
    TextView author;
    EditText input_comment;
    Button saveComment;
    AdapterCommentList adapterCommentList;

    ArrayList<JSONObject> comments = new ArrayList<JSONObject>();

    String path = null;
    Class cls = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_showarticle);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        commentList = (ListView) findViewById(R.id.listView_comment);
        saveComment = (Button) findViewById(R.id.btn_saveComment);
        input_comment = (EditText) findViewById(R.id.input_comment);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
//        commentCount = (TextView) findViewById(R.id.commentCount);
        date = (TextView) findViewById(R.id.date);
        content = (TextView) findViewById(R.id.content);

        comments.clear();

//        editArticle.setVisibility(View.GONE);
//        deleteArticle.setVisibility(View.GONE);

        try {
            showArticleToServer();
            showCommentsToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterCommentList = new AdapterCommentList(ShowArticle.this, comments);
        adapterCommentList.notifyDataSetChanged();

        commentList.setAdapter(adapterCommentList);

        saveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_comment.getText().toString().isEmpty()){
                    Toast.makeText(ShowArticle.this,"댓글을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        saveCommentToServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_showarticle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_editArticle:
                intent = new Intent(ShowArticle.this, EditArticle.class);
                intent.putExtra("from", "article");
                startActivity(intent);
                break;
            case R.id.menu_deleteArticle:
                new AlertDialog.Builder(ShowArticle.this)
                        .setTitle("게시글 삭제 확인")
                        .setMessage("게시글을 정말 삭제하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // yes 버튼 누르면
                                try {
                                    deleteArticleToServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // no 버튼 누르면
                            }
                        })
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showCommentsToServer() throws Exception {

        String URL = String.format("http://52.41.19.232/showComments?articleId=%s&path=%s",
                URLEncoder.encode(Article.getInstance().getId(), "utf-8"),
                URLEncoder.encode(path, "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(ShowArticle.this,"댓글을 불러오는데 실패하였습니다.",Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        comments.add(response.optJSONObject(i));
                        adapterCommentList.notifyDataSetChanged();
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

    private void showArticleToServer() throws Exception {

//        String url = "";
//        switch(path) {
//            case "notice":
//                url = "showNotice";
//                break;
//            case "schedule":
//                url = "showSchedule";
//                break;
//            case "qa":
//                url = "showQA";
//                break;
//        }
        String URL = String.format("http://52.41.19.232/showArticle?articleId=%s&path=%s",
                URLEncoder.encode(Article.getInstance().getId(), "utf-8"), URLEncoder.encode(path), "utf-8");

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result") && response.toString().contains("fail")) {
                        Toast.makeText(ShowArticle.this,"게시글을 불러오는데 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    } else {
                        Article.getInstance().setCommentCount(response.getString("commentCount"));
                        Article.getInstance().setDate(response.getString("date"));
                        Article.getInstance().setTitle(response.getString("title"));
                        Article.getInstance().setContent(response.getString("content"));
                        Article.getInstance().setId(response.getString("_id"));
                        Article.getInstance().setAuthor(response.getString("author"));

                        content.setText(Article.getInstance().getContent());
                        title.setText(Article.getInstance().getTitle());
                        author.setText(Article.getInstance().getAuthor());
                        commentCount.setText("(" + Article.getInstance().getCommentCount() + ")");
                        date.setText(Article.getInstance().getDate());

//                        if (User.getInstance().getUserId().equals(response.getString("author"))) {
//                            editArticle.setVisibility(View.VISIBLE);
//                            deleteArticle.setVisibility(View.VISIBLE);

//                            editArticle.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(ShowArticle.this, EditArticle.class);
//                                    intent.putExtra("path", "fromShow");
//                                    startActivity(intent);
//                                }
//                            });
//
//                            deleteArticle.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    new AlertDialog.Builder(ShowArticle.this)
//                                            .setTitle("게시글 삭제 확인")
//                                            .setMessage("게시글을 정말 삭제하시겠습니까?")
//                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    // yes 버튼 누르면
//                                                    try {
//                                                        deleteArticleToServer();
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            })
//                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    // no 버튼 누르면
//                                                }
//                                            })
//                                            .show();
//                                }
//                            });
//                        }
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

    private void saveCommentToServer() throws Exception {

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());
        postParam.put("articleId", Article.getInstance().getId());
        postParam.put("content", input_comment.getText().toString());
        postParam.put("path", path);

        String URL = "http://52.41.19.232/saveComment";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(ShowArticle.this, "댓글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShowArticle.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(ShowArticle.this, ShowArticle.class);
                intent.putExtra("path", path);
                startActivity(intent);
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

    private void deleteArticleToServer() throws Exception {

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("articleId", Article.getInstance().getId());
        postParam.put("path", path);

        String URL = "http://52.41.19.232/deleteArticle";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(ShowArticle.this, "글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        switch(path) {
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
                        Intent intent = new Intent(ShowArticle.this, cls);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ShowArticle.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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
        switch(path) {
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
        Intent intent = new Intent(ShowArticle.this, cls);
        startActivity(intent);
        super.onBackPressed();
    }
}
