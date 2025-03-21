package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.entity.order.HotelCustomerOrder;
import com.dofinal.RG.reqs.userOrder.CustomerOrderReq;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Classname HotelOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/26 21:29
 * &#064;Created MuJue
 */
public class HotelOrderReq extends CustomerOrderReq {
    private int hid;
    private int rid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp fromTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp toTime;
    private double price;
    public HotelOrderReq(){;}

    public HotelOrderReq(String uid, List<Integer> c_ids, int hid, int rid, Timestamp fromTime, Timestamp toTime, double price) {
        super(uid, c_ids);
        this.hid = hid;
        this.rid = rid;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.price = price;
    }

    public List<HotelCustomerOrder> getInstance(){
        List<HotelCustomerOrder> orders = new ArrayList<>();
        for(Integer cid : cids){
            orders.add(new HotelCustomerOrder(-1, cid, hid, rid, null,null , fromTime, toTime, price));
        }
        return orders;
    }
    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
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
