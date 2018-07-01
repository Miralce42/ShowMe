package com.hanzhuang42.showme.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.hanzhuang42.showme.activitys.ShowActivity;
import com.hanzhuang42.showme.db.DetectObject;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.util.List;

public class DbUtility {

    public static void main(String args[]){
//        create();
//        insert();
    }

    public static void create(){
        Connector.getDatabase();
    }

    public static void insert(){
        DetectObject detectObject = new DetectObject();
        detectObject.setId(1);
        detectObject.setName("红烧肉");
        detectObject.setType(0);
        detectObject.setProbability(100.0);
        detectObject.setYearOrCalorie("300");
        detectObject.save();
    }

    public static List<DetectObject> query(int type){
        List<DetectObject> detectObjectList = DataSupport.where("type = ? and imgPath <> ?", String.valueOf(type), "null")
                .order("id desc")
                .find(DetectObject.class);
        return detectObjectList;
    }

    public static List<DetectObject> queryByid(int id){
        List<DetectObject> detectObjectList = DataSupport.where("id = ?", String.valueOf(id))
                .find(DetectObject.class);
        return detectObjectList;
    }

    public static void deleteById(int id){
        DataSupport.delete(DetectObject.class,id);
    }

    public static void deleteByType(int type){
        DataSupport.deleteAll(DetectObject.class, "type = ?", String.valueOf(type));
    }

    public static void deleteAllByType(Context context, int type){
        List<DetectObject> detectObjectList = query(type);
        for(DetectObject detectObject : detectObjectList){
            String imgPath = detectObject.getImgPath();
            int id = detectObject.getId();
            if(imgPath != null){
                File tmp_file = new File(imgPath);
                if (tmp_file.exists()) {
                    if(tmp_file.delete()){
                        Uri uri = Uri.fromFile(tmp_file);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }
                }
            }
            deleteById(id);
        }
    }
}
