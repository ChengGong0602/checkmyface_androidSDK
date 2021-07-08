package com.zyfesco.landmark;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;

public class Uploadtos3 {

    // TAG for logging;
    private static final String TAG = "UploadActivity";

    public static void uploadtos3 (final Context context, final File file) {

        if (file != null) {
            CognitoCachingCredentialsProvider credentialsProvider;
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "ap-northeast-1:734ebae8-09de-4a3e-9f68-4042446c77ef", // Identity Pool ID
                    Regions.AP_NORTHEAST_1 // Region
            );

            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
            TransferUtility transferUtility = new TransferUtility(s3, context);
            final TransferObserver observer = transferUtility.upload(
                    "kyc-checkdata", //this is the bucket name on S3
                    file.getName(),
                    file,
                    CannedAccessControlList.PublicRead //to make the file public
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
//                        Toast.makeText(context, "Completed to facephoto upload", Toast.LENGTH_LONG).show();
                        Log.i("TTTTTTTTTTTTTT", "success");
                    } else if (state.equals(TransferState.FAILED)) {
//                        Toast.makeText(context, "Failed to face photo upload", Toast.LENGTH_LONG).show();
                        Log.i("TTTTTTTTTTTTTT", "failed");
                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                            id, bytesTotal, bytesCurrent));
                    int percentage =(int)(bytesCurrent/bytesTotal*100);
//                    updateList();

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }
    }
}