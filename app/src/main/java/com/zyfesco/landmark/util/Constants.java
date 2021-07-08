package com.zyfesco.landmark.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by darrenl on 2016/4/22.
 * Modified by Gaurav on Feb 23, 2018
 */

public final class Constants {
    private Constants() {
        // Constants should be prive
//        String Phone_number="";
    }

    public static String Contact_info, User_name, User_dob;
    public static String bucketname="kyc-checkdata";
    public static String getModelDirectoryPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + ".Facecheck_data";
        return targetPath;
    }
    public static String getModelDataDirectoryPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + ".Facecheck_data"+ File.separator + "models";
        return targetPath;
    }

    public static String getModelImageDirectoryPath() {
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + ".Facecheck_data"+ File.separator + "image";
        return targetPath;
    }






}
