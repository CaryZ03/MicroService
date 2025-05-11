package com.dofinal.RG.entity.location;

import java.sql.Timestamp;

/**
 * &#064;Classname TrainLocationDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/3 10:21
 * &#064;Created MuJue
 */
public class TrainLocationDemo {
    private String province;
    private String city;
    private String arriveTime;

    public TrainLocationDemo(String province, String city, String arriveTime) {
        this.province = province;
        this.city = city;
        this.arriveTime = arriveTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
