package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * &#064;Classname HotelOrder
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 17:30
 * &#064;Created MuJue
 */
@TableName("hotel_orders")
public class HotelCustomerOrder extends CustomerOrder {
    @TableField("h_id")
    private int hotelId;
    @TableField("r_id")
    private int roomId;
    @TableField("r_number")
    private String roomNumber;
    @TableField("from_time")
    private Timestamp fromTime;
    @TableField("to_time")
    private Timestamp toTime;
    @TableField("ho_price")
    private double price;

    public HotelCustomerOrder(int oid, int cid , int hotelId, int roomId, String roomNumber, Timestamp add_time,Timestamp fromTime, Timestamp toTime, double price) {
        super(oid, cid, add_time);
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.price = price;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
