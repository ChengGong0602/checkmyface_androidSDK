package com.zyfesco.landmark.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.InvalidParameterException;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.zyfesco.landmark.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.zyfesco.landmark.util.Constants.bucketname;
import static com.zyfesco.landmark.util.Constants.getModelDirectoryPath;


public class VerifyActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FACECHECK = 300;
    public String Contact_info,check_result,FacephotoPath,RegiteredFacePhoto;
    private TextView mVerifystatus;
    public static final int LOAD_SUCCESS = 102;
    private String based64string;
    private String REQUEST_URL="https://checkmyface.net/faceverify";
    public   String liveness_result;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Contact_info = getIntent().getStringExtra("contact_info");
        mVerifystatus = (TextView)findViewById(R.id.verify_status);
        Intent intent = new Intent(getApplicationContext(), LivenessDetectionActivity2.class);
        intent.putExtra("authtype", "verify");
        startActivityForResult(intent, REQUEST_CODE_FACECHECK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FACECHECK && resultCode == RESULT_OK) {
            if (data != null) {
                check_result = data.getStringExtra("facecheckdata");
                FacephotoPath=check_result;
                RegiteredFacePhoto=Contact_info+"_Facephoto_only_registered" + ".jpg";

                File storageDir = new File(getModelDirectoryPath(), "image");
                if(!storageDir.exists()){
                    storageDir.mkdirs();
                }

                File register_file=new File(storageDir,RegiteredFacePhoto);



                    if (check_result.equals("Failed")) {
//                    Toast.makeText(this, check_result, Toast.LENGTH_SHORT).show();
                        Intent fail_intent = new Intent(getApplicationContext(), FailActivity.class);
                        fail_intent.putExtra("Failreason","");
                        startActivity(fail_intent);
                    } else {
                        File imageFile = null;
                        imageFile = new File(storageDir, check_result);
                        String image_filePath=imageFile.getAbsolutePath();
                        Log.d("CCCC",image_filePath);
                        Bitmap image= BitmapFactory.decodeFile(image_filePath);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteImage = stream.toByteArray();
                        based64string = Base64.encodeToString(byteImage, Base64.NO_WRAP);

                        Thread thread = new Thread(new Runnable() {

                            public void run() {
                                String result;
                                try {
                                    URL url = new URL(REQUEST_URL);
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.setReadTimeout(15000);
                                    httpURLConnection.setConnectTimeout(15000);
                                    httpURLConnection.setUseCaches(false);
                                    httpURLConnection.setDoOutput(true);
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.setRequestMethod("POST");
                                    httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");
                                    httpURLConnection.setRequestProperty("Content-Type","application/json");
//                            httpURLConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
                                    httpURLConnection.setRequestProperty("Accept","application/json");
                                    httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                                    httpURLConnection.setRequestProperty("Authorization", "Token eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyLCJ1c2VybmFtZSI6ImNoZWNrbXlmYWNlIiwiZXhwIjoxNjMxNTY4NDYyLCJlbWFpbCI6InJqczA2MDJAMTYzLmNvbSIsIm9yaWdfaWF0IjoxNjAwMDMyNDYyfQ.upXJqVUTIdPbliOkdjfmY7kPzyQ6bYRwAxEf06KeRrg");
                                    httpURLConnection.connect();
                                    //--------------------------
                                    //
                                    //--------------------------
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append("contact_info").append("=").append(Contact_info).append("&");
                                    buffer.append("face_photo").append("=").append(based64string);
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
//                                  JSONObject result_json=jobj.getJSONObject("result");
                                    liveness_result = json_obj.getString("result");
//                                  MainocrActivity.textviewJSONText.setText(jsonString);
                                    Log.d("cccccddd", liveness_result);
                                    Log.d("cccccddd", result);
                                }catch (Throwable t){
                                    Log.d("ccccerror", "Couldn't parse JSON:\n"+result+"\"");
                                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                                    intent.putExtra("Failreason", "Connecting server is failed");
                                    startActivity(intent);
                                }
                                if (liveness_result.equals("real")) {
                                    // Here, check_result is the path of face photo in the phone storage
                                    File imageFile = null;
                                    File storageDir = new File(Environment.getExternalStorageDirectory() + "/.Facecheck_data", "image");
                                    imageFile = new File(storageDir, check_result);
//                                    uploadtos3(getApplicationContext(), imageFile);

                                    CognitoCachingCredentialsProvider credentialsProvider;
                                    credentialsProvider = new CognitoCachingCredentialsProvider(
                                            getApplicationContext(),
                                            "ap-northeast-1:734ebae8-09de-4a3e-9f68-4042446c77ef", // Identity Pool ID
                                            Regions.AP_NORTHEAST_1 // Region
                                    );

                                    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);


                                    TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
                                    final TransferObserver observer = transferUtility.upload(
                                            "kyc-checkdata", //this is the bucket name on S3
                                            imageFile.getName(),
                                            imageFile,
                                            CannedAccessControlList.PublicRead //to make the file public
                                    );
                                    observer.setTransferListener(new TransferListener() {
                                        @Override
                                        public void onStateChanged(int id, TransferState state) {
                                            if (state.equals(TransferState.COMPLETED)) {
//                        Toast.makeText(context, "Completed to facephoto upload", Toast.LENGTH_LONG).show();
                                                Log.i("TTTTTTTTTTTTTT", "success");
                                                Thread th = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AWSCredentials credentials = new BasicAWSCredentials("", "");
                                                        AmazonRekognition client = new AmazonRekognitionClient(credentials);
                                                        client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1 ));

                                                        Image source = new Image()
                                                                .withS3Object(new S3Object().withBucket(bucketname).withName(RegiteredFacePhoto));
                                                        Image target = new Image()
                                                                .withS3Object(new S3Object().withBucket(bucketname).withName(FacephotoPath));

                                                        CompareFacesRequest request = new CompareFacesRequest()
                                                                .withSourceImage(source)
                                                                .withTargetImage(target);
                                                        try {
                                                            CompareFacesResult compareFacesResult = client.compareFaces(request);
                                                            List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
                                                            Log.d("FFFFFFFFFFFF", compareFacesResult.toString());

                                                            for (CompareFacesMatch match : faceDetails) {
                                                                if (match.getSimilarity() >= 90f) {
                                                                    Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                                                                    intent.putExtra("FacephotoPath", FacephotoPath);
                                                                    Log.d("FFFFFFFFFFFFpath", FacephotoPath);
                                                                    Log.d("FFFFFFFFFFFFreg", RegiteredFacePhoto);

                                                                    intent.putExtra("type", "verify");
                                                                    startActivity(intent);
                                                                } else {
                                                                    Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                                                                    intent.putExtra("Failreason", "Face is similar,but not match");
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                            List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
                                                            if (uncompared.size() > 0) {
                                                                //                                Toast.makeText(getApplicationContext(),"Face doesn't match",Toast.LENGTH_LONG);
                                                                Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                                                                intent.putExtra("Failreason", "Face doesn't match");
                                                                startActivity(intent);
                                                            }
                                                            Log.d("faceUncompare", "There was " + uncompared.size()
                                                                    + " face(s) that did not match");
                                                        } catch (InvalidParameterException e) {
                                                            Log.d("Face doens't exist", "Face doesn't exist");
                                                            Intent intent = new Intent(getApplicationContext(), FailActivity.class);
                                                            intent.putExtra("Failreason", "Face doesn't exist on ID Card");

                                                            startActivity(intent);
                                                        }
                                                        catch (AmazonClientException ace) {
                                                            Log.d("Error", "Internal error occurred communicating with Rekognition");
                                                            //                            Toast.makeText(getApplicationContext(),"Internal error occurred communicating with Rekognition",Toast.LENGTH_LONG);
                                                            Intent intent=new Intent(getApplicationContext(),FailActivity.class);
                                                            intent.putExtra("Failreason","Your face is not registered. ");
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                                th.start();
                                            } else if (state.equals(TransferState.FAILED)) {
//                        Toast.makeText(context, "Failed to face photo upload", Toast.LENGTH_LONG).show();
                                                Log.i("TTTTTTTTTTTTTT", "failed");
                                            }

                                        }

                                        @Override
                                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                            Log.d("", String.format("onProgressChanged: %d, total: %d, current: %d",
                                                    id, bytesTotal, bytesCurrent));
                                            int percentage =(int)(bytesCurrent/bytesTotal*100);
//                    updateList();

                                        }

                                        @Override
                                        public void onError(int id, Exception ex) {

                                        }
                                    });


                                }
                                else{
                                    Intent intent=new Intent(getApplicationContext(),FailActivity.class);
                                    intent.putExtra("Failreason","Your face is fake. ");
                                    startActivity(intent);
                                }
                            }
                        });
                        thread.start();
                    }

            }
        }
    }
}

