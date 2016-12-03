package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by daddyslab on 2016. 10. 31..
 */
public class ShowGallery extends AppCompatActivity {

    ListView commentList;
    TextView title, date, author, commentCount;
    EditText input_comment;
    ImageView galleryImage;
    Button saveComment;
    AdapterCommentList adapterCommentList;

    ArrayList<JSONObject> comments = new ArrayList<JSONObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_showgallery);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        commentList = (ListView) findViewById(R.id.listView_galleryComment);
        saveComment = (Button) findViewById(R.id.btn_galleryCommentSave);
        input_comment = (EditText) findViewById(R.id.input_galleryComment);
        title = (TextView) findViewById(R.id.galleryTitle);
        author = (TextView) findViewById(R.id.galleryAuthor);
        date = (TextView) findViewById(R.id.galleryDate);
        galleryImage = (ImageView) findViewById(R.id.galleryImage);

        comments.clear();

        try {
            showGalleryToServer();
            showCommentsToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterCommentList = new AdapterCommentList(ShowGallery.this, comments);
        adapterCommentList.notifyDataSetChanged();

        commentList.setAdapter(adapterCommentList);

        saveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_comment.getText().toString().isEmpty()) {
                    Toast.makeText(ShowGallery.this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
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
        if (User.getInstance().getUserName().equals(GallerySingleton.getInstance().getAuthor())) {
            getMenuInflater().inflate(R.menu.menu_showgallery, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(ShowGallery.this, Gallery.class);
                startActivity(intent);
                super.onBackPressed();
                break;
            case R.id.menu_editGallery:
                intent = new Intent(ShowGallery.this, EditGallery.class);
                intent.putExtra("from", "detail");
                startActivity(intent);
                break;
            case R.id.menu_deleteGallery:
                new AlertDialog.Builder(ShowGallery.this)
                        .setTitle("갤러리 삭제 확인")
                        .setMessage("갤러리를 정말 삭제하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // yes 버튼 누르면
                                try {
                                    deleteGalleryToServer();
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
                URLEncoder.encode(GallerySingleton.getInstance().getId(), "utf-8"),
                URLEncoder.encode("gallery", "utf-8"));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(ShowGallery.this, "댓글을 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
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

    private void showGalleryToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        String URL = String.format("http://52.41.19.232/showArticle?articleId=%s&path=%s",
                URLEncoder.encode(GallerySingleton.getInstance().getId(), "utf-8"), URLEncoder.encode("gallery", "utf-8"));

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    if (response.toString().contains("result") && response.toString().contains("fail")) {
                        Toast.makeText(ShowGallery.this, "게시글을 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        byte[] decodedString;

                        GallerySingleton.getInstance().setCommentCount(response.getInt("commentCount"));
                        GallerySingleton.getInstance().setDate(response.getString("date"));
                        GallerySingleton.getInstance().setTitle(response.getString("title"));
                        GallerySingleton.getInstance().setId(response.getString("_id"));
                        GallerySingleton.getInstance().setAuthor(response.getString("author"));
                        GallerySingleton.getInstance().setImage(response.getString("image"));

                        title.setText(GallerySingleton.getInstance().getTitle());
                        author.setText(GallerySingleton.getInstance().getAuthor());
                        date.setText(GallerySingleton.getInstance().getDate());
                        decodedString = Base64.decode(GallerySingleton.getInstance().getImage(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        galleryImage.setImageBitmap(decodedByte);
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

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());
        postParam.put("articleId", GallerySingleton.getInstance().getId());
        postParam.put("content", input_comment.getText().toString());
        postParam.put("path", "gallery");

        String URL = "http://52.41.19.232/saveComment";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(ShowGallery.this, "댓글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShowGallery.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(ShowGallery.this, ShowGallery.class);
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

    private void deleteGalleryToServer() throws Exception {

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("articleId", GallerySingleton.getInstance().getId());
        postParam.put("path", "gallery");

        String URL = "http://52.41.19.232/deleteArticle";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(ShowGallery.this, "정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ShowGallery.this, Gallery.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ShowGallery.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(ShowGallery.this, Gallery.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
