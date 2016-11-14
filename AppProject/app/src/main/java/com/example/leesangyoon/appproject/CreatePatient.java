package com.example.leesangyoon.appproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daddyslab on 2016. 11. 1..
 */

// 여기는 누구든지 보호자환자 생성할 수 있게. 프로텍터를 지정하면 해당 프로텍터한테만 보임.
public class CreatePatient extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;
    Bitmap photo;

    CircleImageView patient_image;
    EditText patient_name, patient_birthday, patient_relation;
    EditText worker_id, user_name, user_phone_number, user_id, user_password, user_password_check;
    RadioButton patient_female;
    String patientName, birthday, relation, workerId, userName, userPhoneNumber, userId, userPassword, userPasswordCheck;
    String gender = "male";
    String image = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        patient_image = (CircleImageView)findViewById(R.id.image_patient);
        patient_name = (EditText)findViewById(R.id.input_patientName);
        patient_birthday = (EditText)findViewById(R.id.input_birthday);
        patient_relation = (EditText)findViewById(R.id.input_relation);
        worker_id = (EditText)findViewById(R.id.input_workerId);
        user_name = (EditText)findViewById(R.id.input_protectorName);
        user_id = (EditText)findViewById(R.id.input_protectorId);
        user_phone_number = (EditText)findViewById(R.id.input_protectorPhoneNumber);
        user_password = (EditText)findViewById(R.id.input_protectorPassword);
        user_password_check = (EditText)findViewById(R.id.input_protectorPasswordCheck);
        patient_female = (RadioButton)findViewById(R.id.radio_patient_female);

        patient_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                new AlertDialog.Builder(CreatePatient.this)
                        .setTitle("업로드할 이미지 선택")
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

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
                    patient_image.setImageBitmap(photo);
                }

//                 임시 파일 삭제
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_createpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(CreatePatient.this, AdminPatient.class);
                startActivity(intent);
                super.onBackPressed();
                break;
            case R.id.menu_createPatient:
                patientName = patient_name.getText().toString();
                birthday = patient_birthday.getText().toString();
                relation = patient_relation.getText().toString();
                workerId = worker_id.getText().toString();
                userName = user_name.getText().toString();
                userId = user_id.getText().toString();
                userPhoneNumber = user_phone_number.getText().toString();
                userPassword = user_password.getText().toString();
                userPasswordCheck = user_password_check.getText().toString();
                // 환자/보호자 생성 요청 후 인덴트
                try {
                    if(patientName.equals("") || birthday.equals("") || relation.equals("") || workerId.equals("") || userName.equals("") ||
                            userId.equals("") || userPassword.equals("") || userPhoneNumber.equals("") || userPasswordCheck.equals("")) {
                        Toast.makeText(CreatePatient.this, "입력창을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (userPassword.equals(userPasswordCheck)) {
                            if (patient_female.isChecked()) {
                                gender = "female";
                            }
                            createPatientToServer();
                        } else {
                            Toast.makeText(CreatePatient.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPatientToServer() throws Exception {

        final String URL = "http://52.41.19.232/createPatient";

        image = getStringImage(photo);
        patientName = patient_name.getText().toString();
        birthday = patient_birthday.getText().toString();
        relation = patient_relation.getText().toString();
        workerId = worker_id.getText().toString();
        userName = user_name.getText().toString();
        userId = user_id.getText().toString();
        userPhoneNumber = user_phone_number.getText().toString();
        userPassword = user_password.getText().toString();
        userPasswordCheck = user_password_check.getText().toString();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("nursingHomeId", User.getInstance().getNursingHomeId());
        postParam.put("patientName", patientName);
        postParam.put("birthday", birthday);
        postParam.put("relation", relation);
        postParam.put("workerId", workerId);
        postParam.put("userName", userName);
        postParam.put("userId", userId);
        postParam.put("password", userPassword);
        postParam.put("phoneNumber", userPhoneNumber);
        postParam.put("image", image);
        postParam.put("gender", gender);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(CreatePatient.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("userOverlap")) {
                            Toast.makeText(CreatePatient.this, "보호자 아이디가 이미 사용중입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("patientOverlap")) {
                            Toast.makeText(CreatePatient.this, "이미 등록된 수급자입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.getString("result").equals("success")) {
                            Toast.makeText(CreatePatient.this, "수급자 등록 완료", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreatePatient.this, AdminPatient.class);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("development", "Error: " + error.getMessage());
                    }
                });

        volley.getInstance().addToRequestQueue(req);
    }
}
