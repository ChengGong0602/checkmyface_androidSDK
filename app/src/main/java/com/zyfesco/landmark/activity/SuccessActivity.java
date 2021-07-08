package com.zyfesco.landmark.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zyfesco.landmark.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.zyfesco.landmark.util.Constants.Contact_info;
import static com.zyfesco.landmark.util.Constants.User_dob;
import static com.zyfesco.landmark.util.Constants.User_name;

//import com.amazonaws.services.rekognition.model.S3Object;


public class SuccessActivity extends AppCompatActivity {
    private String Facephoto_matched, Facephoto_registred , type;
    private Button mSuccess_btn;
    private TextView mSuccess_txt;
    private ProgressDialog progressDialog;
    public AppCompatActivity activity;
    public static final int LOAD_SUCCESS = 101;
    private String REQUEST_URL ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        mSuccess_btn= (Button)findViewById(R.id.Success_btn);
        mSuccess_txt=(TextView)findViewById(R.id.success_txt);
        Facephoto_matched = getIntent().getStringExtra("FacephotoPath");
        Facephoto_registred=Contact_info+"_Facephoto_registered"+".jpg";
        type = getIntent().getStringExtra("type");
        mSuccess_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
            }
        });
        if (type.equals("register")){
//            progressDialog = new ProgressDialog( activity );
//            progressDialog.setMessage("Please wait.....");
//            progressDialog.show();

            getJSON();
            mSuccess_txt.setText("Your Face is successfully registered");

        }else{
            mSuccess_txt.setText("Your Face is successfully verified");

        }
//        String SourcePath = getDLibDirectoryPath() + File.separator + Facephoto_matched;
//        String targetPath = getDLibDirectoryPath() + File.separator + Facephoto_registred;

//        s3client.deleteObject(new DeleteObjectRequest(bucketName, keyName));

    }

    public void  getJSON() {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                String result;
                try {
                    REQUEST_URL="https://checkmyface.net/faceregister_without_idphoto";
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
                    buffer.append("id_photo").append("=").append(Contact_info+"_IDphoto.jpg").append("&");
                    buffer.append("face_photo").append("=").append(Contact_info+"_Facephoto.jpg");
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

            }

        });
        thread.start();
    }



}