package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HotelCommentReq extends BaseReq {
    @JsonProperty("oid")
    private int oid;
    @JsonProperty("rate")
    private int rate;
    private String comment;

    public HotelCommentReq(String uid, int oid, int rate, String comment) {
        super(uid);
        this.oid = oid;
        this.rate = rate;
        this.comment = comment;
    }

    public HotelCommentReq() {
        ;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
