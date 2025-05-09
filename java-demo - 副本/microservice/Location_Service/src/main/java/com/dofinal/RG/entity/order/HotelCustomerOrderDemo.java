package com.dofinal.RG.entity.order;

import java.sql.Timestamp;

/**
 * &#064;Classname HotelCustomerOrderDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/4 20:19
 * &#064;Created MuJue
 */
public class HotelCustomerOrderDemo extends CustomerOrderDemo{
    private String hname;
    private String roomNumber;
    private Timestamp fromTime;
    private Timestamp toTime;

    public HotelCustomerOrderDemo(int cid, String cname, double price, String hname, String roomNumber, Timestamp fromTime, Timestamp toTime) {
        super(cid, cname, price);
        this.hname = hname;
        this.roomNumber = roomNumber;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Timestamp getFromTime() {
        return fromTime;
    }

    public void setFromTime(Timestamp fromTime) {
        this.fromTime = fromTime;
    }

    public Timestamp getToTime() {
        return toTime;
    }

    public void setToTime(Timestamp toTime) {
        this.toTime = toTime;
    }
}
