package com.dofinal.RG.entity.order;

/**
 * &#064;Classname MealCustomerOrderDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/4 20:19
 * &#064;Created MuJue
 */
public class MealCustomerOrderDemo extends CustomerOrderDemo{
    private String tid;
    private String mname;

    public MealCustomerOrderDemo(String tid, int cid, String cname, double price, String mname) {
        super(cid, cname, price);
        this.tid = tid;
        this.mname = mname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
