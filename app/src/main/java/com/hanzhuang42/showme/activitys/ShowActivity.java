package com.hanzhuang42.showme.activitys;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hanzhuang42.showme.R;
import com.hanzhuang42.showme.db.DetectObject;
import com.hanzhuang42.showme.api.DetectAPI;
import com.hanzhuang42.showme.util.DbUtility;
import com.hanzhuang42.showme.util.MyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShowMe/ShowMe/";
    private static final String TAG = "ShowActivity";

    private TextView textView;
    private ProgressBar showProgressBar;
    private ImageView imageView;

    private DetectObject detectObject = null;
    private Bitmap bitmap = null;
    private Uri uri = null;
    private String str_uri = null;
    private int detectType;
    private String storeImgPath = null;
    private List<DetectObject> detectObjectList;
    private String picture_path = null;
    private int show_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        imageView = findViewById(R.id.show_image_view);
        textView = findViewById(R.id.show_content_text);
        showProgressBar = findViewById(R.id.show_progress_bar);
        showProgressBar.setVisibility(View.VISIBLE);

        FloatingActionButton fab_save = findViewById(R.id.fab_save);
        FloatingActionButton fab_share = findViewById(R.id.fab_share);
        FloatingActionButton fab_delete = findViewById(R.id.fab_delete);
        fab_save.setOnClickListener(this);
        fab_share.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        imageView.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        str_uri= intent.getStringExtra("uri");
        picture_path = intent.getStringExtra("picture_path");
        show_id = intent.getIntExtra("detectObject_id",-1);
        detectType = intent.getIntExtra("detect_type", -1);


        //从已有项（数据库）中初始化
        if(show_id != -1){
            List<DetectObject> detectObjectList = DbUtility.queryByid(show_id);
            if(detectObjectList.size() == 1){
                detectObject = detectObjectList.get(0);
                textView.setText(detectObject.getName());
                String tmp_path = detectObject.getImgPath();
                if (tmp_path != null) {
                    File file = new File(tmp_path);
                    if (file.exists()) {
                        Uri tmp_uri = Uri.fromFile(file);
                        Glide.with(ShowActivity.this).load(tmp_uri).into(imageView);
                        uri = tmp_uri;
                    }
                }
            }
            showProgressBar.setVisibility(View.GONE);
        }

        ByteArrayOutputStream baos = null;
        byte[] img_datas = {};

        @SuppressLint("HandlerLeak") final Handler net_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");
                detectObjectList = parseJSONWithGSON(val);
                if(detectObjectList == null){
                    MyToast.showToast(ShowActivity.this,"检测失败！", Toast.LENGTH_LONG);
                    showProgressBar.setVisibility(View.GONE);
                }
                else {
                    detectObject = detectObjectList.get(0);
                    textView.setText(val);
                    showProgressBar.setVisibility(View.GONE);
                    MyToast.showToast(ShowActivity.this, "检测完成，点击星星保存记录和图片(✧◡✧)", Toast.LENGTH_LONG);
                }
            }
        };

        //从拍摄或选择的照片进行初始化
        if(str_uri != null) {
            try {
                uri = Uri.parse(str_uri);
                if(picture_path == null) {
                    //拍摄的照片
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    Glide.with(this).load(bitmap).into(imageView);
                }else{
                    //从相册选取的照片
                    Glide.with(this).load(uri).into(imageView);
                    bitmap = BitmapFactory.decodeFile(picture_path);
                }

                if (bitmap == null) {
                    MyToast.showToast(ShowActivity.this, "无法读取照片,检测失败", Toast.LENGTH_SHORT);
                    Log.i(TAG, "----------------------------------------ShowActivity 无法读取图片");
                    finish();
                }
                else {
//                    Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                    img_datas = baos.toByteArray();
                    DetectAPI detectAPI = new DetectAPI();
                    final byte[] final_img_datas = img_datas;
                    detectAPI.start(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject res = null;
                            try {
                                res =detectByType(detectType,final_img_datas);
                            } catch (NetworkErrorException e) {
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value", res.toString());
                            msg.setData(data);
                            net_handler.sendMessage(msg);
                        }
                    });
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private List<DetectObject> parseJSONWithGSON(String jsonData){
        JSONArray jsonArray = null;
        if(jsonData.contains("error_code")){
            Log.d(TAG+"_error_code",jsonData);
            return null;
        }
        List<DetectObject> res = new ArrayList<>();
        try {
            Log.d(TAG+"Debug",jsonData);
            JSONObject jsonTmp = new JSONObject(jsonData);
            jsonArray = jsonTmp.getJSONArray("result");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DetectObject tmp_detectObject = new DetectObject();
                tmp_detectObject.setType(detectType);
                tmp_detectObject.setName(jsonObject.getString("name"));
                if(jsonObject.has("probability")) {
                    double probability = jsonObject.getDouble("probability");
                    tmp_detectObject.setProbability(probability);
                }
                if(jsonObject.has("score")){
                    double score = jsonObject.getDouble("score");
                    tmp_detectObject.setProbability(score);
                }
                if(jsonObject.has("year")){
                    String year = jsonObject.getString("year");
                    tmp_detectObject.setYearOrCalorie(year);
                }
                if(jsonObject.has("calorie")){
                    String calorie = jsonObject.getString("calorie");
                    tmp_detectObject.setYearOrCalorie(calorie);
                }
                if(jsonTmp.has("color_result")){
                    String color_result = jsonTmp.getString("color_result");
                    tmp_detectObject.setCar_color(color_result);
                }
                res.add(tmp_detectObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    private JSONObject detectByType(int type, byte[] final_img_datas) throws NetworkErrorException {
        HashMap<String,String> hashMap=new HashMap<String, String>();
        hashMap.put("top_num","3");
        JSONObject res = null;
         switch (type) {
             case 0:
                 res = DetectAPI.client.dishDetect(final_img_datas, hashMap);
                 break;
             case 1:
                 res = DetectAPI.client.carDetect(final_img_datas, hashMap);
                 break;
             case 2:
                 res = DetectAPI.client.logoSearch(final_img_datas, hashMap);
                 break;
             case 3:
                 res = DetectAPI.client.animalDetect(final_img_datas, hashMap);
                 break;
             case 4:
                 res = DetectAPI.client.plantDetect(final_img_datas, hashMap);
                 break;
         }
        return res;
    }

    @SuppressLint("HandlerLeak") final Handler img_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int ess = data.getInt("ExternalStorageState");
            if(ess == -1){
                storeImgPath = null;
                MyToast.showToast(ShowActivity.this,"图片保存失败!", Toast.LENGTH_SHORT);
            }
            else {
                storeImgPath = data.getString("imgPath");
                storeDetectObject();
            }
        }
    };

    private void storeData(){
        if(bitmap != null) {
            final Bitmap finalBitmap = bitmap;
            new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    String state = Environment.getExternalStorageState();
                    if (!state.equals(Environment.MEDIA_MOUNTED)) {
                        data.putInt("ExternalStorageState", -1);
                        msg.setData(data);
                    } else {
                        Calendar now = new GregorianCalendar();
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String fileName = simpleDate.format(now.getTime());
                        try {
                            File file = new File(dir);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            String filePath = dir + fileName + ".jpg";
                            file = new File(filePath);
                            if (file.exists()) {
                                Random random = new Random(1000);
                                file = new File(dir + fileName + "_" + random.toString() + ".jpg");
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            data.putInt("ExternalStorageState", 1);
                            data.putString("imgPath", filePath);
                            msg.setData(data);
                            Uri uri = Uri.fromFile(file);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    img_handler.sendMessage(msg);
                }
            }.run();
        }
    }

    public void storeDetectObject(){
        if(storeImgPath != null){
            detectObject.setImgPath(storeImgPath);
            detectObject.save();
            MyToast.showToast(ShowActivity.this,"保存成功!", Toast.LENGTH_SHORT);
        }
        else{
            Log.d(TAG,"图像路径为空，对象保存失败！");
            MyToast.showToast(ShowActivity.this,"保存失败!", Toast.LENGTH_SHORT);
        }
    }

    private boolean isSaved(){
        return detectObject.getImgPath() != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteObject(){
        boolean deleted = false;
        String tmp_path = detectObject.getImgPath();
        if(tmp_path != null){
            File tmp_file = new File(tmp_path);
            if (tmp_file.exists()) {
                if(tmp_file.delete()){
                    Uri uri = Uri.fromFile(tmp_file);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    DbUtility.deleteById(detectObject.getId());
                    deleted = true;
                    finish();
                    MyToast.showToast(ShowActivity.this,"删除成功!", Toast.LENGTH_SHORT);
                }
            }
        }
        if(!deleted){
            MyToast.showToast(ShowActivity.this,"删除失败!", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_save:
                if(detectObject == null){
                    Log.d(TAG,"保存未初始化对象！");
                    MyToast.showToast(ShowActivity.this,"检测未成功，不能保存!", Toast.LENGTH_SHORT);
                }
                else if(isSaved()){
                    MyToast.showToast(ShowActivity.this,"不能重复保存!", Toast.LENGTH_SHORT);
                }
                else{
                    storeData();
                }
                break;
            case R.id.fab_delete:
                if(!isSaved()){
                    Log.d(TAG,"删除未初始化对象！");
                    MyToast.showToast(ShowActivity.this,"没有保存，不能删除！", Toast.LENGTH_SHORT);
                }
                else{
                    AlertDialog.Builder builder= new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
                    builder.setTitle("删除");
                    builder.setMessage("图片也将被删除，且无法恢复！你确定要删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteObject();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                }
                break;
            case R.id.fab_share:
                break;
            case R.id.show_image_view:
                if(uri == null){
                    MyToast.showToast(ShowActivity.this,"浏览图片失败！", Toast.LENGTH_SHORT);
                }
                else {
                    Intent intent = new Intent(this, ViewImageActivity.class);
                    intent.putExtra("uri",uri.toString());
                    startActivity(intent);
                }
                break;
        }
    }
}
