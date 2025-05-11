package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * &#064;Classname TrainOrder
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 17:32
 * &#064;Created MuJue
 */
@TableName("trains_orders")
public class TrainCustomerOrder extends CustomerOrder {
    @TableField("t_id")
    private String trainId;
    @TableField("seat_number")
    private String seatNumber;
    @TableField("seat_type")
    private String seatType;
    @TableField("departure_time")
    private Timestamp departureTime;
    @TableField("arrive_time")
    private Timestamp arriveTime;
    @TableField("departure_location")
    private int departLocationId;
    @TableField("arrive_location")
    private int arriveLocationId;
    @TableField("to_price")
    private double price;

    public TrainCustomerOrder(int oid, int cid, String trainId, String seatNumber, String seatType, Timestamp add_time, Timestamp departureTime, Timestamp arriveTime, double price, int departLocationId, int arriveLocationId) {
        super(oid, cid, add_time);
        this.trainId = trainId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.departureTime = departureTime;
        this.arriveTime = arriveTime;
        this.departLocationId = departLocationId;
        this.arriveLocationId = arriveLocationId;
        this.price = price;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDepartLocationId() {
        return departLocationId;
    }

    public void setDepartLocationId(int departLocationId) {
        this.departLocationId = departLocationId;
    }

    public int getArriveLocationId() {
        return arriveLocationId;
    }

    public void setArriveLocationId(int arriveLocationId) {
        this.arriveLocationId = arriveLocationId;
    }
}
