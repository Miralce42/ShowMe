package com.hanzhuang42.showme.db;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetectObject extends DataSupport {

    private int id = -1;

    private int type;

    private String name = "检测失败！";

    private String imgPath ;

    private Date date;

    @SerializedName(value = "probability", alternate = "score")
    private double probability;

    @SerializedName(value = "year", alternate = "calorie")
    private String yearOrCalorie;

    @SerializedName("color_result")
    private String car_color;

    public DetectObject() {
        date = new Date();
    }

    public String getDate(int flag){
        Date time = date;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }

    public String getDate(){
        Date time = date;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearOrCalorie() {
        return yearOrCalorie;
    }

    public void setYearOrCalorie(String yearOrCalorie) {
        this.yearOrCalorie = yearOrCalorie;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getProbability() {
        NumberFormat num = NumberFormat.getPercentInstance();
        num.setMinimumFractionDigits(2);//设置保留小数位
        num.setRoundingMode(RoundingMode.HALF_UP); //设置舍入模式
        return num.format(probability);
    }

    public void setProbability(double probability) {

        this.probability = probability;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
