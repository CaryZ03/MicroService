package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MealReq extends BaseReq {
    @JsonProperty("tid")
    private String tid;
    @JsonProperty("mid")
    private int mid;

    public MealReq(String uid, String tid, int mid) {
        super(uid);
        this.tid = tid;
        this.mid = mid;
    }
    public MealReq(){;}

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }
}
