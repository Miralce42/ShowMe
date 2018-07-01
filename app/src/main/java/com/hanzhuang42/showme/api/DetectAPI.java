package com.hanzhuang42.showme.api;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.imageclassify.AipImageClassify;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class DetectAPI {

    private static final String TAG = "DetectAPI";
    private static final String APP_ID = "11415436";
    private static final String API_KEY = "nKLMG3EfPeDttL5l3LPkLAB3";
    private static final String SECRET_KEY = "KFSd4r6b5bdy968bSQtpH5AmTA7GcdWr";
    public static AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);


    public DetectAPI(){
         // 可选：设置网络连接参数
         client.setConnectionTimeoutInMillis(2000);
         client.setSocketTimeoutInMillis(60000);
    }

    public void start(Runnable networkTask){
        new Thread(networkTask).start();
    }

}
