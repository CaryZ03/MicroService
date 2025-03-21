package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.reqs.userOrder.CustomerOrderReq;

import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Classname TrainOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/26 21:30
 * &#064;Created MuJue
 */
public class TrainOrderReq extends CustomerOrderReq {
    private String tid;
    private String seatType;
    private String seatNumber;
    private Location beginLocation;
    private Location endLocation;

    public TrainOrderReq(){;}

    public TrainOrderReq(String uid, List<Integer> c_ids, String tid, String seatType, String seatNumber, Location beginLocation, Location endLocation) {
        super(uid, c_ids);
        this.tid = tid;
        this.seatType = seatType;
        this.seatNumber = seatNumber;
        this.beginLocation = beginLocation;
        this.endLocation = endLocation;
    }

    public List<TrainCustomerOrder> getInstance(){
        List<TrainCustomerOrder> orders = new ArrayList<>();
        for(Integer cid : cids){
            orders.add(new TrainCustomerOrder(-1, cid, tid,null, seatType,null,null,null,0.0,0,0));
        }
        return orders;
    }
    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public Location getBeginLocation() {
        return beginLocation;
    }

    public void setBeginLocation(Location beginLocation) {
        this.beginLocation = beginLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
