package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * Classname RoomReq
 * Description TODO
 * Date 2024/5/24 11:26
 * Created ZHW
 */
public class RoomReq extends BaseReq {
    private int hid;
    private int rid;
    private String rNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp fromTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp toTime;

    public RoomReq(String uid, int hid, int rid, String rNumber, Timestamp fromTime, Timestamp toTime) {
        super(uid);
        this.hid = hid;
        this.rid = rid;
        this.rNumber = rNumber;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public RoomReq(){
        ;
    }
    public void setFromTime(Timestamp fromTime){
        this.fromTime = fromTime;
    }

    public void setToTime(Timestamp toTime){
        this.toTime = toTime;
    }

    public Timestamp getFromTime() { return fromTime; }

    public Timestamp getToTime() { return toTime; }

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

    public String getrNumber() {
        return rNumber;
    }

    public void setrNumber(String rNumber) {
        this.rNumber = rNumber;
    }
}
