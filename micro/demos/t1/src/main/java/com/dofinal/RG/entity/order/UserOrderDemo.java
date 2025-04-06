package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;

import java.sql.Timestamp;
import java.util.List;

/**
 * &#064;Classname UserOrderDemo
 * &#064;Description TODO
 * &#064;Date 2024/6/4 20:32
 * &#064;Created MuJue
 */
public class UserOrderDemo {
    private Integer oid;
    private String uid;
    private String type;
    private Timestamp addTime;
    private String status;
    private double price;
    private String tid;
    private String hname;
    private boolean isComment;
    private List<HotelCustomerOrderDemo> hotelCustomerOrderDemos = null;
    private List<MealCustomerOrderDemo> mealCustomerOrderDemos = null;
    private List<TrainCustomerOrderDemo> trainCustomerOrderDemos = null;

    public UserOrderDemo(Integer oid, String uid, String type, Timestamp addTime, String status, double price,
            boolean isComment) {
        this.oid = oid;
        this.uid = uid;
        this.type = type;
        this.addTime = addTime;
        this.status = status;
        this.price = price;
        this.isComment = isComment;
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<HotelCustomerOrderDemo> getHotelCustomerOrderDemos() {
        return hotelCustomerOrderDemos;
    }

    public void setHotelCustomerOrderDemos(List<HotelCustomerOrderDemo> hotelCustomerOrderDemos) {
        this.hotelCustomerOrderDemos = hotelCustomerOrderDemos;
    }

    public List<MealCustomerOrderDemo> getMealCustomerOrderDemos() {
        return mealCustomerOrderDemos;
    }

    public void setMealCustomerOrderDemos(List<MealCustomerOrderDemo> mealCustomerOrderDemos) {
        this.mealCustomerOrderDemos = mealCustomerOrderDemos;
    }

    public List<TrainCustomerOrderDemo> getTrainCustomerOrderDemos() {
        return trainCustomerOrderDemos;
    }

    public void setTrainCustomerOrderDemos(List<TrainCustomerOrderDemo> trainCustomerOrderDemos) {
        this.trainCustomerOrderDemos = trainCustomerOrderDemos;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }
}
