package com.dofinal.RG.entity.order;

import com.dofinal.RG.entity.location.Location;

import java.sql.Timestamp;

/**
 * &#064;Classname TrainCustomerOrderDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/4 20:21
 * &#064;Created MuJue
 */
public class TrainCustomerOrderDemo extends CustomerOrderDemo{
    private String tid;
    private String seatNumber;
    private String seatType;
    private Location departureLocation;
    private Location arriveLocation;
    private Timestamp departureTime;
    private Timestamp arriveTime;

    public TrainCustomerOrderDemo(int cid, String cname, double price, String tid, String seatNumber, String seatType, Location departureLocation, Location arriveLocation, Timestamp departureTime, Timestamp arriveTime) {
        super(cid, cname, price);
        this.tid = tid;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.departureLocation = departureLocation;
        this.arriveLocation = arriveLocation;
        this.departureTime = departureTime;
        this.arriveTime = arriveTime;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(Location departureLocation) {
        this.departureLocation = departureLocation;
    }

    public Location getArriveLocation() {
        return arriveLocation;
    }

    public void setArriveLocation(Location arriveLocation) {
        this.arriveLocation = arriveLocation;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public Timestamp getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Timestamp arriveTime) {
        this.arriveTime = arriveTime;
    }
}
