package com.example.leesangyoon.appproject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
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
import whdghks913.tistory.floatingactionbutton.FloatingActionButton;

/**
 * Created by daddyslab on 2016. 11. 3..
 */
public class ShowPatient extends AppCompatActivity {
    AdapterCategoryGrid adapterCategoryGrid ;
    ArrayList<JSONObject> categories = new ArrayList<JSONObject>();
    GridView gridView;
    TextView roomNumber, birthday;
    ActionBar actionBar;
    CircleImageView patientImage;
    TextView date;
    int year, month, day;
    String msg;
    int lyear, lmonth, lday;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
        setContentView(R.layout.activity_showpatient);

        actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        gridView = (GridView) findViewById(R.id.gridView_category);
        roomNumber = (TextView)findViewById(R.id.patient_roomNumber);
        birthday = (TextView)findViewById(R.id.patient_birthday);
        patientImage = (CircleImageView)findViewById(R.id.image_patient);
        date = (TextView)findViewById(R.id.text_date);

        categories.clear();
        try {
            setup();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        categories.clear();
        adapterCategoryGrid = new AdapterCategoryGrid(ShowPatient.this, categories);
        adapterCategoryGrid.notifyDataSetChanged();
        gridView.setAdapter(adapterCategoryGrid);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ShowPatient.this, dateSetListener, year, month, day).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_showpatient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(ShowPatient.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_editCategory:
                intent = new Intent(ShowPatient.this, EditCategory.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShowPatient.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void setup() throws ParseException {
        actionBar.setTitle(Patient.getInstance().getPatientName());
        assert actionBar != null;
        roomNumber.setText(Patient.getInstance().getRoomNumber() + "호");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = format.parse(Patient.getInstance().getBirthday());
        int age = getAgeFromBirthday(d) + 1;
        birthday.setText(String.valueOf(age) + "세");
        patientImage.setImageBitmap(StringToBitmap(Patient.getInstance().getImage()));

        GregorianCalendar calendar = new GregorianCalendar();
        lyear = calendar.get(Calendar.YEAR);
        lmonth = calendar.get(Calendar.MONTH);
        lday= calendar.get(Calendar.DAY_OF_MONTH);

        date.setText(String.format("%d-%d-%d", lyear,lmonth+1, lday));
        try {
            getCategoriesToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCategoriesToServer() throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);

        String currentDate = String.valueOf(lyear) + "-" + String.valueOf(lmonth+1) + "-" + String.valueOf(lday);
        String URL = String.format("http://52.41.19.232/getCategories?patientId=%s&date=%s",
                URLEncoder.encode(Patient.getInstance().getId(), "utf-8"), URLEncoder.encode(currentDate), "utf-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                loading.dismiss();
                if (response.toString().contains("result") && response.toString().contains("fail")) {
                    Toast.makeText(ShowPatient.this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(response.toString().contains("result") && response.toString().contains("nothing")) {
                    Toast.makeText(ShowPatient.this, "데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    msg = String.format("%d-%d-%d", year, month+1, day);
                    date.setText(msg);
                }
                else {
                    year = lyear;
                    month = lmonth;
                    day = lday;
                    msg = String.format("%d-%d-%d", lyear, lmonth+1, lday);
                    date.setText(msg);
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

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            if (view.isShown()) {
                lyear = year;
                lmonth = monthOfYear;
                lday = dayOfMonth;
                try {
                    getCategoriesToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

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
}
