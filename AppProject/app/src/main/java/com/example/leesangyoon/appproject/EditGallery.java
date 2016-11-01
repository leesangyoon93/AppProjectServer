package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by daddyslab on 2016. 10. 31..
 */
public class EditGallery extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;

    String from = "";
    Class cls = null;
    EditText title, content;
    Button uploadButton;
    ImageView image;
    Bitmap photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editgallery);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");

        title = (EditText) findViewById(R.id.input_galleryTitle);
        content = (EditText) findViewById(R.id.input_galleryContent);
        uploadButton = (Button) findViewById(R.id.btn_uploadImage);
        image = (ImageView) findViewById(R.id.input_galleryImage);

        uploadButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (User.getInstance().getAuth() == 1) {
            getMenuInflater().inflate(R.menu.menu_editgallery, menu);
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 300, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

//    private void doTakePhotoAction() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // 임시로 사용할 파일의 경로를 생성
//        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
//        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
//        //intent.putExtra("return-data", true);
//        if( isIntentAvailable( getApplicationContext(), MediaStore.ACTION_IMAGE_CAPTURE)) {
//            startActivityForResult(intent, PICK_FROM_CAMERA);
//        }
//    }
//
//    public static boolean isIntentAvailable(Context context, String action){
//        final PackageManager packageManager = context.getPackageManager();
//        final Intent intent = new Intent( action);
//        List<ResolveInfo> list = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY);
//        return list.size() > 0;
//    }

    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CROP_FROM_CAMERA: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    photo = extras.getParcelable("data");
                    image.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 1000);
                intent.putExtra("outputY", 1000);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
//        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                doTakePhotoAction();
//            }
//        };
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

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
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

        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        String image = getStringImage(photo);

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("userId", User.getInstance().getUserId());
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("galleryId", GallerySingleton.getInstance().getId());
        postParam.put("image", image);
        postParam.put("title", title.getText().toString());
        postParam.put("content", content.getText().toString());

        String URL = "http://52.41.19.232/saveGallery";

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
}
