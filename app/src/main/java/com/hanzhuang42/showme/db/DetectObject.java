package com.hanzhuang42.showme.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

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
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        sdf.format(date);
    }

    public String getDate(){
        Date time = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

    public double getProbability() {
        return probability;
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
