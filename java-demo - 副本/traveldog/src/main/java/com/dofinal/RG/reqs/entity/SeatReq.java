package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.BaseReq;

/**
 * &#064;Classname SeatReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/25 22:37
 * &#064;Created MuJue
 */
public class SeatReq extends BaseReq {
    private String tid;
    private String sid;
    private String seatType;
    private Location beginLocation;
    private Location endLocation;

    public SeatReq(String uid, String tid, String sid, String seatType, Location beginLocation, Location endLocation) {
        super(uid);
        this.tid = tid;
        this.sid = sid;
        this.seatType = seatType;
        this.beginLocation = beginLocation;
        this.endLocation = endLocation;
    }

    public SeatReq(){;}

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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
}
