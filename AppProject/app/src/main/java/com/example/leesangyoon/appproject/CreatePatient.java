package com.example.leesangyoon.appproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
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
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daddyslab on 2016. 11. 1..
 */

public class CreatePatient extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 1;
    Bitmap photo;

    CircleImageView patient_image;
    EditText patient_name, patient_birthday, patient_roomNumber, patient_relation;
    EditText worker_id, user_name, user_phone_number, user_id, user_password, user_password_check;
    RadioButton patient_female;
    String patientName, birthday, roomNumber, relation, workerId, userName, userPhoneNumber, userId, userPassword, userPasswordCheck;
    String gender = "male";
    String image = "";
    int lyear, lmonth, lday;
    String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_createpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        GregorianCalendar calendar = new GregorianCalendar();
        lyear = calendar.get(Calendar.YEAR);
        lmonth = calendar.get(Calendar.MONTH);
        lday = calendar.get(Calendar.DAY_OF_MONTH);
        date = String.valueOf(lyear) + "-" + String.valueOf(lmonth+1) + "-" + String.valueOf(lday);

        patient_image = (CircleImageView)findViewById(R.id.image_patient);
        patient_name = (EditText)findViewById(R.id.input_patientName);
        patient_birthday = (EditText)findViewById(R.id.input_birthday);
        patient_roomNumber = (EditText)findViewById(R.id.input_roomNumber);
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

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
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
            uriToBitmap(data.getData());
            if (picturePath != null) {
                patient_image.setImageURI(data.getData());
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
                roomNumber = patient_roomNumber.getText().toString();
                relation = patient_relation.getText().toString();
                workerId = worker_id.getText().toString();
                userName = user_name.getText().toString();
                userId = user_id.getText().toString();
                userPhoneNumber = user_phone_number.getText().toString();
                userPassword = user_password.getText().toString();
                userPasswordCheck = user_password_check.getText().toString();
                try {
                    if(patientName.equals("") || birthday.equals("") || relation.equals("") || workerId.equals("") || userName.equals("") ||
                            userId.equals("") || userPassword.equals("") || userPhoneNumber.equals("") || userPasswordCheck.equals("") || roomNumber.equals("")) {
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
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        final String URL = "http://52.41.19.232/createPatient";

        image = BitmapToString(photo);
        patientName = patient_name.getText().toString();
        birthday = patient_birthday.getText().toString();
        roomNumber = patient_roomNumber.getText().toString();
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
        postParam.put("roomNumber", roomNumber);
        postParam.put("date", date);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
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

    private void uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            photo = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
