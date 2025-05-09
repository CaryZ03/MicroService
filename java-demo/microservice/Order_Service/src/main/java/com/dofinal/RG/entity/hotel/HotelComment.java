package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("hotel_comment")
public class HotelComment {
    @TableField("hc_id")
    private int hcid;
    @TableField("o_id")
    private int oid;
    @TableField("h_id")
    private int hid;
    @TableField("rate")
    private int rate;
    @TableField("content")
    private String content;

    public HotelComment(int hcid,int oid, int hid, int rate, String content) {
        this.hcid = hcid;
        this.oid = oid;
        this.hid = hid;
        this.rate = rate;
        this.content = content;
    }

    public int getHcid() {
        return hcid;
    }

    public void setHcid(int hcid) {
        this.hcid = hcid;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
