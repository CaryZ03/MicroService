package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * &#064;Classname HotelReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/17 20:49
 * &#064;Created MuJue
 */
public class HotelReq extends BaseReq {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp endTime;
    private Location location;

    public HotelReq(String uid, Timestamp startTime, Timestamp endTime, Location location) {
        super(uid);
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
    public HotelReq(){;}
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
