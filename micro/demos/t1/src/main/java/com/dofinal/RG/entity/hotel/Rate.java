package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("hotel_rate")
public class Rate {
    @TableField("o_id")
    private int oid;
    @TableField("h_id")
    private int hid;
    @TableField("rate")
    private double rate;

    public Rate(int oid, int hid, double rate) {
        this.oid = oid;
        this.hid = hid;
        this.rate = rate;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
