package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.reqs.BaseReq;

public class MealReq extends BaseReq {
    private String tid;
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
