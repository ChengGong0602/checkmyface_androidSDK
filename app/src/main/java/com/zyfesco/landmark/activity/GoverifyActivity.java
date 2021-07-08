package com.zyfesco.landmark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zyfesco.landmark.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.zyfesco.landmark.util.Constants.Contact_info;

public class GoverifyActivity extends AppCompatActivity {
    private Button Face_verify;
    private  EditText phone_number;
    private ImageButton mbackButton;
    private String REQUEST_URL ;
    public   String reg_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goverify);
        Face_verify= (Button)findViewById(R.id.verify_btn);
        phone_number = (EditText)findViewById(R.id.phone_num_edt);
        mbackButton=(ImageButton) findViewById(R.id.start_BackButton);
        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(),StartActivity.class);
//                startActivity(intent);
                onBackPressed();
            }
        });

        Face_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact_info=phone_number.getText().toString();
                if (Contact_info.length() > 0) {
                    check_registration();

                }
                else {
                    phone_number.setError(getResources().getString(R.string.value_required));
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
                    Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
                    intent.putExtra("contact_info", Contact_info);
                    startActivity(intent);
                }else if (reg_result.equals("'dob'")){
                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                    intent.putExtra("Failreason", "Phone/Email is not registered");
                    startActivity(intent);
                }

            }

        });
        thread.start();
    }
}
