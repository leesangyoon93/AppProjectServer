package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 31..
 */
public class EditGallery extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1;

    String from = "";
    Class cls = null;
    EditText title, content;
    ImageView image;
    Bitmap photo;
    String strImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editgallery);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");

        title = (EditText) findViewById(R.id.input_galleryTitle);
        content = (EditText) findViewById(R.id.input_galleryContent);
        image = (ImageView) findViewById(R.id.input_galleryImage);

        title.setText(GallerySingleton.getInstance().getTitle());
        content.setText(GallerySingleton.getInstance().getContent());
        photo = StringToBitmap(GallerySingleton.getInstance().getImage());
        image.setImageBitmap(photo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editgallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                if (from.equals("list")) {
                    cls = Gallery.class;
                    intent = new Intent(EditGallery.this, cls);
                } else {
                    cls = ShowGallery.class;
                    intent = new Intent(EditGallery.this, cls);
                    // 인텐트로 파라미터 넘겨야함.
                }
                startActivity(intent);
                break;
            case R.id.menu_saveGallery:
                try {
                    saveGalleryToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.menu_uploadImage:
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(EditGallery.this)
                        .setTitle("업로드할 이미지 선택")
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && null != data) {
            Uri mImageCaptureUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(mImageCaptureUri,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photo = BitmapFactory.decodeFile(picturePath);
            image.setImageBitmap(photo);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (from.equals("list")) {
            cls = Gallery.class;
            intent = new Intent(EditGallery.this, cls);
        } else {
            cls = ShowGallery.class;
            intent = new Intent(EditGallery.this, cls);
            // 인텐트로 파라미터 넘겨야함.
        }
        startActivity(intent);
        super.onBackPressed();
    }

    private void saveGalleryToServer() throws Exception {

        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        strImage = BitmapToString(photo);

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserName());
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("articleId", GallerySingleton.getInstance().getId());
        postParam.put("image", strImage);
        postParam.put("title", title.getText().toString());
        postParam.put("content", content.getText().toString());
        postParam.put("path", "gallery");

        String URL = "http://52.41.19.232/saveArticle";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(EditGallery.this, "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        GallerySingleton.getInstance().setId(response.getString("galleryId"));
                        Intent intent = new Intent(EditGallery.this, ShowGallery.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditGallery.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
