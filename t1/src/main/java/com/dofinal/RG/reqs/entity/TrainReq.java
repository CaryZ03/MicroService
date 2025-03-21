package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * &#064;Classname TrainReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:23
 * &#064;Created MuJue
 */
public class TrainReq extends BaseReq {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp curTime;
    private Location startLocation;
    private Location endLocation;

    public TrainReq(String uid, Timestamp curTime, Location startLocation, Location endLocation) {
        super(uid);
        this.curTime = curTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
    public TrainReq(){;}
    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public Timestamp getCurTime() {
        return curTime;
    }

    public void setCurTime(Timestamp curTime) {
        this.curTime = curTime;
    }
}
