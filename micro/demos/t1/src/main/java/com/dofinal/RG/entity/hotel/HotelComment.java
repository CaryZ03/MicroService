package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("hotel_comment")
public class HotelComment {
    @TableField("o_id")
    private int oid;
    @TableField("h_id")
    private int hid;
    @TableField("rate")
    private int rate;
    private String comment;

    public HotelComment(int oid, int hid, int rate, String comment) {
        this.oid = oid;
        this.hid = hid;
        this.rate = rate;
        this.comment = comment;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
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
