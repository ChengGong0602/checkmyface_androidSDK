package com.zyfesco.landmark.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.tu.tcircleprogresslibrary.TCircleProgressView;
import com.zyfesco.landmark.CameraOverlap;
import com.zyfesco.landmark.EGLUtils;
import com.zyfesco.landmark.FileUtil;
import com.zyfesco.landmark.GLBitmap;
import com.zyfesco.landmark.GLFrame;
import com.zyfesco.landmark.GLFramebuffer;
import com.zyfesco.landmark.GLPoints;
import com.zyfesco.landmark.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zeusee.main.hyperlandmark.jni.Face;
import com.zeusee.main.hyperlandmark.jni.FaceTracking;
import com.zyfesco.landmark.util.ImageUtils;

import static com.zyfesco.landmark.util.Constants.Contact_info;
import static com.zyfesco.landmark.util.Constants.getModelDataDirectoryPath;
import static com.zyfesco.landmark.util.Constants.getModelDirectoryPath;
import static com.zyfesco.landmark.util.Constants.getModelDirectoryPath;

//import static com.zyfesco.hyperlandmark.activity.MainActivity.KEY_DOC_FILE_PATH;

public class LivenessDetectionActivity2 extends AppCompatActivity {
    private String[] denied;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private TCircleProgressView mTcpv;


    private FaceTracking mMultiTrack106 = null;
    private boolean mTrack106 = false;
    public String result_faceliveness_info;
    private int actionFramecount = 0;
    int step = 1;
    int i=0;
    int randomAction=0;   //
    public  boolean isstarted=false;
    public  String CHECK_RESULT_SUCCESS="Success";
    public  String CHECK_RESULT_FAIL="Failed";
    public  String isSuccess;
    public  String savedFacephoto;
    private boolean TAKEN=false;
    public  int white_color=Color.WHITE;
    public  int green_color=Color.GREEN;
    Random random=new Random();
    private Context mContext;
    private boolean init_face = false;
    private ImageButton   mbackButton;
    private ImageView arrow_image;



    private Bitmap mRGBframeBitmap = null;
    private int[] mRGBBytes = null;



    private float eyeLeftx=0 ;
    private float eyeLefty=0;
    private float eyeRightx = 0;
    private float eyeRighty = 0;
    private float leftRawx=0;
    private float leftRawy=0;
    private float rightRawx=0;
    private float rightRawy=0;
    private float RawXD =0;
    private float RawYD =0;
    private float RawD = 0;
    private float constA = 0;
    private float constB =0;
    private float leftComparePointY = 0;
    private float rightComparePointY =0;
    private float    eyepx1= 0;
    private float    eyepx2=0;
    private float    eyepx3=0;
    private float    eyepx4=0;
    private float    eyepx5=0;
    private float    eyepx6=0;
    private float    eyepy1=0;
    private float    eyepy2=0;
    private float    eyepy3=0;
    private float    eyepy4=0;
    private float    eyepy5=0;
    private float    eyepy6=0;
    private float     eyedisP26=0;
    private float     eyedisP14=0;
    private float     eyedisP35=0;
    private float     EAR=0;
    private float Mouthtopx1=0;
    private float   Mouthtopy1=0;
    private float  Mouthtopx2=0;
    private float  Mouthtopy2=0;
    private float  Mouthbottomx1=0;
    private float  Mouthbottomy1=0;
    private float  Mouthbottomx2=0;
    private float Mouthbottomy2=0;
    private float MouthTopdis= 0;
    private float MouthBottomdis=0;
    private float MouthTotaldis=0;
    private float MouthRatio = 0;
    private float  faceCenterx=0;
    private float    faceCentery=0;
    private float   LeftDis=0;
    private float  RightDis=0;
    private float  RLratio=0;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private byte[] mNv21Data;
    private CameraOverlap cameraOverlap;
    private final Object lockObj = new Object();

    private SurfaceView mSurfaceView;
    private EGLUtils mEglUtils;
    private GLFramebuffer mFramebuffer;
    private GLFrame mFrame;
    private GLPoints mPoints;
    private String TAG="tttttt";
    public static int height = 480;
    public static int width = 640;
    private String authtype;

    public void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.mkdir())
                {
                    Log.d("mkdir","can't make folder");
                }

                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_detection2);
        arrow_image=(ImageView)findViewById(R.id.arrow_img);
        mbackButton = (ImageButton)findViewById(R.id.backButton);
        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
//                startActivity(intent);
                onBackPressed();
            }
        });
//        String outputPath = getIntent().getStringExtra(KEY_DOC_FILE_PATH);
//        Log.d("TTTTTTTT",outputPath);
        mTcpv = findViewById(R.id.tcpv_dam_board);
        mTcpv.setTotalProgress(1000);
        mTcpv.setAnimationDuration(1000);
        mTcpv.setHintTextSize(28);
        mTcpv.setTextPadding(50);
        authtype = getIntent().getStringExtra("authtype");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (PermissionChecker.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                    list.add(permissions[i]);
                }
            }
            if (list.size() != 0) {
                denied = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    denied[i] = list.get(i);
                }
                ActivityCompat.requestPermissions(this, denied, 5);
            } else {
                init();
            }
        } else {
            init();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5) {
            boolean isDenied = false;
            for (int i = 0; i < denied.length; i++) {
                String permission = denied[i];
                for (int j = 0; j < permissions.length; j++) {
                    if (permissions[j].equals(permission)) {
                        if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                            isDenied = true;
                            break;
                        }
                    }
                }
            }
            if (isDenied) {
                Toast.makeText(this, "Please allow permissions", Toast.LENGTH_SHORT).show();
            } else {
                init();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init(){
        InitModelFiles();
        FaceTracking.getInstance().FaceTrackingInit(getModelDataDirectoryPath(), height, width);
        cameraOverlap = new CameraOverlap(this);
        mNv21Data = new byte[CameraOverlap.PREVIEW_WIDTH * CameraOverlap.PREVIEW_HEIGHT * 2];
        mFramebuffer = new GLFramebuffer();
        mFrame = new GLFrame();
        mPoints = new GLPoints();
        mHandlerThread = new HandlerThread("DrawFacePointsThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        cameraOverlap.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, Camera camera) {

                final Camera.Size previewSize = camera.getParameters().getPreviewSize();


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils == null) {
                            return;
                        }
                        mFrame.setS(1.0f);
                        mFrame.setH(0.0f);
                        mFrame.setL(0.0f );
                        mRGBframeBitmap = Bitmap.createBitmap(width, 820, Bitmap.Config.ARGB_8888);

                        long start = System.currentTimeMillis();
                        FaceTracking.getInstance().Update(data, previewSize.height, previewSize.width);
//                        Log.e("TAG", "====用时=====" + (System.currentTimeMillis() - start));
                        boolean rotate270 = cameraOverlap.getOrientation() == 270;
                        List<Face> faceActions = FaceTracking.getInstance().getTrackingInfo();
                        float[] points = null;
                        for (Face r : faceActions) {
                            points = new float[106 * 2];
                            for (int i = 0; i < 106; i++) {
                                int x;
                                if (rotate270) {
                                    x = r.landmarks[i * 2] * CameraOverlap.SCALLE_FACTOR;
                                } else {
                                    x = CameraOverlap.PREVIEW_HEIGHT - r.landmarks[i * 2];
                                }
                                int y = r.landmarks[i * 2 + 1] * CameraOverlap.SCALLE_FACTOR;
                                points[i * 2] = view2openglX(x, CameraOverlap.PREVIEW_HEIGHT);
                                points[i * 2 + 1] = view2openglY(y, CameraOverlap.PREVIEW_WIDTH);
                            }
                        }

                        if (points != null) {
                            if (step==1) {
                                mTcpv.setText("Liveness checking");
                                updatetitleView();
                            }
                            eyeLeftx = points[59*2];
                            eyeLefty = points[59*2+1];
                            eyeRightx = points[27*2];
                            eyeRighty = points[27*2+1];
                            leftRawx = points[9*2];
                            leftRawy = points[9*2+1];
                            rightRawx = points[14*2];
                            rightRawy = points[14*2+1];
                            RawXD = Math.abs(leftRawx - rightRawx);
                            RawYD = Math.abs(leftRawy - rightRawy);
                            RawD = (float) Math.sqrt(Math.pow(RawXD, 2) + Math.pow(RawYD, 2));
                            constA = (leftRawy - rightRawy) / (leftRawx - rightRawx);
                            constB = (leftRawx * rightRawy - rightRawx * leftRawy) / (leftRawx - rightRawx);
                            leftComparePointY = (constA * eyeLeftx + constB - eyeLefty) / RawD * 100;
                            rightComparePointY = (constA * eyeRightx + constB - eyeRighty) / RawD * 100;
                            // EYE BLINK
                            eyepx1 = points[94*2];
                            eyepy1 = points[94*2+1];
                            eyepx2 = points[1*2];
                            eyepy2 = points[1*2+1];
                            eyepx3 = points[53*2];
                            eyepy3 = points[53*2+1];
                            eyepx4 = points[59*2];
                            eyepy4 = points[59*2+1];
                            eyepx5 = points[67*2];
                            eyepy5 = points[67*2+1];
                            eyepx6 = points[12*2];
                            eyepy6 = points[12*2+1];
                            eyedisP26 = (float) Math.sqrt(Math.pow(Math.abs(eyepx2 - eyepx6), 2) + Math.pow(Math.abs(eyepy2 - eyepy6), 2));
                            eyedisP35 = (float) Math.sqrt(Math.pow(Math.abs(eyepx3 - eyepx5), 2) + Math.pow(Math.abs(eyepy3 - eyepy5), 2));
                            eyedisP14 = (float) Math.sqrt(Math.pow(Math.abs(eyepx1 - eyepx4), 2) + Math.pow(Math.abs(eyepy1 - eyepy4), 2));
                            EAR = (eyedisP26 + eyedisP35) /  eyedisP14;
                            //MOUTH
                            Mouthtopx1 = points[38*2];
                            Mouthtopy1 = points[38*2+1];
                            Mouthtopx2 = points[37*2];
                            Mouthtopy2 = points[37*2+1];
                            Mouthbottomx1 = points[103*2];
                            Mouthbottomy1 = points[103*2+1];
                            Mouthbottomx2 = points[32*2];
                            Mouthbottomy2 = points[32*2+1];
                            MouthTopdis = (float) Math.sqrt(Math.pow(Math.abs(Mouthtopx1 - Mouthtopx2), 2) + Math.pow(Math.abs(Mouthtopy1 - Mouthtopy2), 2));
                            MouthBottomdis = (float) Math.sqrt(Math.pow(Math.abs(Mouthbottomx1 - Mouthbottomx2), 2) + Math.pow(Math.abs(Mouthbottomy1 - Mouthbottomy2), 2));
                            MouthTotaldis = (float) Math.sqrt(Math.pow(Math.abs(Mouthtopx1 - Mouthbottomx1), 2) + Math.pow(Math.abs(Mouthtopy1 - Mouthbottomy1), 2));
                            MouthRatio = MouthTotaldis / (MouthTopdis + MouthBottomdis);

                            //FACE RIGHT AND LEFT
                            faceCenterx = points[23*2];
                            faceCentery = points[23*2+1];
                            LeftDis = (float) Math.sqrt(Math.pow(Math.abs(faceCenterx - leftRawx), 2) + Math.pow(Math.abs(faceCentery - leftRawy), 2));
                            RightDis = (float) Math.sqrt(Math.pow(Math.abs(faceCenterx - rightRawx), 2) + Math.pow(Math.abs(faceCentery - rightRawy), 2));
                            RLratio = LeftDis / RightDis;
                            Log.d("eeeemouthxxxxx",Float.toString(MouthRatio));


                            if (randomAction==0&& isstarted==false){ //Head Up
                                if (leftComparePointY < -9 && rightComparePointY < -9) {
                                    result_faceliveness_info="Head UP";
                                    updatetitleView();}
                            }else if (randomAction==1 && isstarted==false){
                                if (leftComparePointY > -4 && rightComparePointY > -4) {
                                    result_faceliveness_info="Head DOWN";
                                    updatetitleView();}
                            }else if(randomAction==2&& isstarted==false){
                                if (RLratio < 0.3) {
                                    result_faceliveness_info = "Move Left";
                                    updatetitleView();}
                            }else if(randomAction==3&& isstarted==false){

                                if (RLratio > 4) {
                                    result_faceliveness_info="Move Right";
                                    updatetitleView();}
                            }else if(randomAction==4&& isstarted==false){
                                if (EAR<0.2){
                                    result_faceliveness_info="EYE COLOSE";
                                    updatetitleView();}
                            }else if(randomAction==5&& isstarted==false){
                                if (MouthRatio > 0.6) {
                                    result_faceliveness_info="Mouth OPEN";
                                    updatetitleView();}
                            }

                            if(randomAction==7 && isstarted==false){

                                if (TAKEN==false){
                                    int width = CameraOverlap.PREVIEW_WIDTH;
                                    int height = 820;
                                    ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
                                    buf.order(ByteOrder.LITTLE_ENDIAN);
                                    GLES20.glReadPixels(0, 0, width, height,
                                            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
//                                    EGlUtil.checkGlError("glReadPixels");
                                    buf.rewind();
                                    mRGBframeBitmap.copyPixelsFromBuffer(buf);
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(1, -1, 320, 410);
                                    mRGBframeBitmap=Bitmap.createBitmap(mRGBframeBitmap, 0, 0, 640, 820, matrix, true);
                                    if (authtype=="register") {
                                        savedFacephoto = ImageUtils.saveBitmap(mRGBframeBitmap);
                                    }else{
                                        mTcpv.setProgressByAnimation(0, 1000);
                                        isSuccess = savedFacephoto ;
                                        savedFacephoto = ImageUtils.saveBitmap2(mRGBframeBitmap);
                                    }
                                    TAKEN = true;
                                }
                                updatetitleView();
                            }
                        }
                        if(actionFramecount < 200)
                        {
                            actionFramecount++;
                        }
                        else
                        {
                            if(step != 2){
                                String time_out = getResources().getString(R.string.act_timeout);
                                mTcpv.setText(time_out);
                                isstarted=true;
                                isSuccess=CHECK_RESULT_FAIL;
                                Intent new_intent=new Intent();
                                new_intent.putExtra("facecheckdata", isSuccess);
                                setResult(RESULT_OK, new_intent);
                                if(!LivenessDetectionActivity2.this.isFinishing()){
                                    finish();
                                }
                            }
                        }

                        int tid = 0;
                        mFrame.drawFrame(tid,mFramebuffer.drawFrameBuffer(),mFramebuffer.getMatrix());
                        mEglUtils.swap();


                    }
                });
            }
        });

        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, int format, final int width, final int height) {
                Log.d("=============", "surfaceChanged");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEglUtils != null) {
                            mEglUtils.release();
                        }
                        mEglUtils = new EGLUtils();
                        mEglUtils.initEGL(holder.getSurface());
                        mFramebuffer.initFramebuffer();
                        mFrame.initFrame();
                        mFrame.setSize(width, height, CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                        mPoints.initPoints();
//                        mBitmap.initFrame(CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                        cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                    }
                });
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraOverlap.release();
                        mFramebuffer.release();
                        mFrame.release();
                        mPoints.release();
//                        mBitmap.release();
                        if (mEglUtils != null) {
                            mEglUtils.release();
                            mEglUtils = null;
                        }
                    }
                });

            }
        });
        if (mSurfaceView.getHolder().getSurface() != null && mSurfaceView.getWidth() > 0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglUtils != null) {
                        mEglUtils.release();
                    }
                    mEglUtils = new EGLUtils();
                    mEglUtils.initEGL(mSurfaceView.getHolder().getSurface());
                    mFramebuffer.initFramebuffer();
                    mFrame.initFrame();
                    mFrame.setSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                    mPoints.initPoints();
//                    mBitmap.initFrame(CameraOverlap.PREVIEW_HEIGHT, CameraOverlap.PREVIEW_WIDTH);
                    cameraOverlap.openCamera(mFramebuffer.getSurfaceTexture());
                }
            });
        }
    }
    private float view2openglX(int x,int width){
        float centerX = width/2.0f;
        float t = x - centerX;
        return t/centerX;
    }
    private float view2openglY(int y,int height){
        float centerY = height/2.0f;
        float s = centerY - y;
        return s/centerY;
    }
    void InitModelFiles()
    {   String assetPath = "ZyfescoFaceTracking";
        String sdcardPath = getModelDirectoryPath();
        copyFilesFromAssets(this, assetPath, sdcardPath);
    }

    public void updatetitleView()
    {
        if(!init_face)
        {
            String act_load = getResources().getString(R.string.act_Loading);
            mTcpv.setProgress(0);
            mTcpv.setText(act_load);
            actionFramecount = 0;
//            return;
        }
        if(step == 2)
        {
//            mTcpv.setProgressByAnimation(0, 1000);
//            String success = "OK";
            isstarted=true;
            isSuccess = savedFacephoto ;
//            mTcpv.setText(success);

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                @Override
                public void run() {
                    Intent new_intent=new Intent();
                    new_intent.putExtra("facecheckdata", isSuccess);
                    setResult(RESULT_OK, new_intent);
                    finish();
                }
            }, 1100);
            actionFramecount = 0;
//            ((CameraActivity) activity).onReceivedData(isSuccess);
//            Toast.makeText(mContext, "successful", Toast.LENGTH_SHORT).show();
        }
        else if(step<2)
        {
            randomAction = 7;
            step=step+1;
        }
    }

}
