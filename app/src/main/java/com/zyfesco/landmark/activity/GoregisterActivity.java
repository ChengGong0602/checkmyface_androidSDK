package com.zyfesco.landmark.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zyfesco.landmark.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static com.zyfesco.landmark.Uploadtos3.uploadtos3;
import static com.zyfesco.landmark.util.Constants.Contact_info;
import static com.zyfesco.landmark.util.Constants.User_dob;
import static com.zyfesco.landmark.util.Constants.User_name;
import static com.zyfesco.landmark.util.Constants.getModelDirectoryPath;


public class GoregisterActivity extends AppCompatActivity {
    private Button Face_register;
    private ImageButton mbackButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String REQUEST_URL ;
    public   String reg_result;
    public String check_result;
    public String FacephotoPath;
    private static final int REQUEST_CODE_FACECHECK = 300;
    private EditText phone_number, name, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Face_register= (Button)findViewById(R.id.register_btn);
        phone_number = (EditText)findViewById(R.id.phone_num_edt);
        name = (EditText)findViewById(R.id.name_edt);
        dob = (EditText)findViewById(R.id.DOB_edt);
        dob.setInputType(InputType.TYPE_NULL);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year= cal.get(Calendar.YEAR);
                int month= cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(
                        GoregisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date =year+"-" +month+"-"+day;
                dob.setText(date);
//            POB_value = POB_edt.getText().toString().trim();


            }
        };
        mbackButton = (ImageButton)findViewById(R.id.start_BackButton);
        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(),StartActivity.class);
//                startActivity(intent);
                onBackPressed();
            }
        });



        Face_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact_info=phone_number.getText().toString();
                User_name=name.getText().toString();
                User_dob=dob.getText().toString();

                int val1=Contact_info.length();
                int val2=User_name.length();
                int val3=User_dob.length();
                int val_total=val1*val2*val3;
                Log.i("BBBBBBBBBB","register preseed");
                Log.i("BBBBBBBBBB",Integer.toString(val1));

                if (val_total > 0) {
                    check_registration();
                }
                else {
                    if (val1==0){
                        phone_number.setError(getResources().getString(R.string.value_required));
                    }else if(val2==0) {
                        name.setError(getResources().getString(R.string.value_required));
                    }else if(val3==0) {
                        dob.setError(getResources().getString(R.string.value_required));
                    }

                }
            }
        });
    }
    public void  check_registration() {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                String result;
                try {
                    REQUEST_URL="https://checkmyface.net/registercheck_without_idphoto";
//                 example:   REQUEST_URL="http://192.168.110.48:8000/api_save/";
                    Log.d("ssssssssssssssssssssss", REQUEST_URL);
                    URL url = new URL(REQUEST_URL); //
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();// URL connection
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);   //
                    httpURLConnection.setRequestMethod("POST"); //
                    httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");
                    httpURLConnection.setRequestProperty("Content-Type","application/json");
//                    httpURLConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept","application/json");
                    httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Authorization", "Token eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyLCJ1c2VybmFtZSI6ImNoZWNrbXlmYWNlIiwiZXhwIjoxNjMxNTY4NDYyLCJlbWFpbCI6InJqczA2MDJAMTYzLmNvbSIsIm9yaWdfaWF0IjoxNjAwMDMyNDYyfQ.upXJqVUTIdPbliOkdjfmY7kPzyQ6bYRwAxEf06KeRrg");
                    httpURLConnection.connect();
                    //--------------------------
                    //--------------------------
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("contact_info").append("=").append(Contact_info).append("&");
                    buffer.append("name").append("=").append(User_name).append("&");
                    buffer.append("dob").append("=").append(User_dob).append("&");
                    OutputStreamWriter outStream = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    //--------------------------
                    //--------------------------
                    int responseStatusCode = httpURLConnection.getResponseCode();
                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    result = sb.toString().trim();

                } catch (Exception e) {
                    result = e.toString();
                }
                try {
                    JSONObject json_obj = new JSONObject(result);
                    reg_result = json_obj.getString("result");
                    Log.d("cccccddd", reg_result);
                    Log.d("cccccddd", result);
                }catch (Throwable t){
                    Log.d("ccccerror", "Couldn't parse JSON:\n"+result+"\"");
                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                    intent.putExtra("Failreason", "Connecting server is failed");
                    startActivity(intent);
                }
                if (reg_result.equals("contact_info")) {
                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                    intent.putExtra("Failreason", "Phone/Email already exists");
                    startActivity(intent);
                }else if (reg_result.equals("name")){
                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                    intent.putExtra("Failreason", "Name and DOB already exists");
                    startActivity(intent);
                }else if(reg_result.equals("new")){
                    Intent intent = new Intent(getApplicationContext(), LivenessDetectionActivity.class);
                    intent.putExtra("authtype", "register");
                    startActivityForResult(intent, REQUEST_CODE_FACECHECK);
                }
            }

        });
        thread.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FACECHECK && resultCode == RESULT_OK) {
            if (data != null) {
                check_result = data.getStringExtra("facecheckdata");
                if (check_result.equals("Failed")) {
//                    Toast.makeText(this, check_result, Toast.LENGTH_SHORT).show();
                    Intent fail_intent = new Intent(getApplicationContext(), FailActivity.class);
                    startActivity(fail_intent);
                } else {    // Here, check_result is the path of face photo in the phone storage
                    File imageFile = null;
                    File storageDir = new File(getModelDirectoryPath(), "image");
                    imageFile = new File(storageDir, check_result);
                    uploadtos3(getApplicationContext(), imageFile);
                    Log.d("cccccccccc", check_result);
//                    Toast.makeText(this, imageFile.getName(), Toast.LENGTH_SHORT).show();
                    FacephotoPath = check_result;
                    Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                    intent.putExtra("FacephotoPath", FacephotoPath);
                    intent.putExtra("type", "register");
                    startActivity(intent);
                }
            }
        }
    }
}
