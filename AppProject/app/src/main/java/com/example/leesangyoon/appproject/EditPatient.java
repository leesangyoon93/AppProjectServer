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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by daddyslab on 2016. 11. 13..
 */
public class EditPatient extends AppCompatActivity implements AdapterView.OnItemClickListener {
    AdapterCategoryGrid adapterCategoryGrid ;
    ArrayList<JSONObject> categories = new ArrayList<JSONObject>();
    GridView gridView;
    TextView roomNumber, birthday;
    ActionBar actionBar;
    CircleImageView patientImage;
    TextView date;
    int lyear, lmonth, lday;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_editpatient);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Patient.getInstance().getPatientName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        gridView = (GridView) findViewById(R.id.gridView_category);
        roomNumber = (TextView)findViewById(R.id.patient_roomNumber);
        birthday = (TextView)findViewById(R.id.patient_birthday);
        patientImage = (CircleImageView)findViewById(R.id.image_patient);
        date = (TextView)findViewById(R.id.text_date);

        roomNumber.setText(Patient.getInstance().getRoomNumber() + "호");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = format.parse(Patient.getInstance().getBirthday());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int age = getAgeFromBirthday(d) + 1;
        birthday.setText(String.valueOf(age) + "세");
        patientImage.setImageBitmap(StringToBitmap(Patient.getInstance().getImage()));

        GregorianCalendar calendar = new GregorianCalendar();
        lyear = calendar.get(Calendar.YEAR);
        lmonth = calendar.get(Calendar.MONTH);
        lday= calendar.get(Calendar.DAY_OF_MONTH);

        date.setText(String.format("%d-%d-%d", lyear,lmonth+1, lday));

        categories.clear();

        try {
            getCategoriesToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapterCategoryGrid = new AdapterCategoryGrid(EditPatient.this, categories);
        adapterCategoryGrid.notifyDataSetChanged();
        gridView.setAdapter(adapterCategoryGrid);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(EditPatient.this, AdminPatient.class);
                startActivity(intent);
                super.onBackPressed();
                break;
//            case R.id.menu_savePatient:
//                savePatientToServer();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCategoriesToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        String URL = String.format("http://52.41.19.232/getCategories?patientId=%s&date=%s",
                URLEncoder.encode(Patient.getInstance().getId(), "utf-8"), String.format("%d-%d-%d", lyear, lmonth+1, lday));

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(EditPatient.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(response.toString().contains("result") && response.toString().contains("nothing")) {
                    Toast.makeText(EditPatient.this, "데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    categories.clear();
                    for (int i = 0; i < response.length(); i++) {
                        categories.add(response.optJSONObject(i));
                        adapterCategoryGrid.notifyDataSetChanged();
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

    public static int getAgeFromBirthday(Date birthday) {
        Calendar birth = new GregorianCalendar();
        Calendar today = new GregorianCalendar();

        birth.setTime(birthday);
        today.setTime(new Date());

        int factor = 0;
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }
        return today.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + factor;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showInputDialog(view, position);
    }

    protected void showInputDialog(View v, final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(EditPatient.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditPatient.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        final TextView text = (TextView) v.findViewById(R.id.categoryContent);
        final ImageView image = (ImageView) v.findViewById(R.id.btn_editCategoryContent);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        text.setText(editText.getText());
                        image.setVisibility(View.INVISIBLE);
                        try {
                            saveCategoryToServer(position, editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void saveCategoryToServer(final int position, final String content) throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("patientId", Patient.getInstance().getId());
        postParam.put("content", content);
        postParam.put("position", String.valueOf(position));
        postParam.put("date", date.getText().toString());

        String URL = "http://52.41.19.232/saveCategory";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    if (response.getString("result").equals("success")) {
                        Toast.makeText(EditPatient.this, "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditPatient.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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
