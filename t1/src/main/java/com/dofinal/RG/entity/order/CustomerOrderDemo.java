package com.dofinal.RG.entity.order;

/**
 * &#064;Classname CustomerOrderDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/4 20:37
 * &#064;Created MuJue
 */
public class CustomerOrderDemo {
    protected int cid;
    protected String cname;
    protected double price;

    public CustomerOrderDemo(int cid, String cname, double price) {
        this.cid = cid;
        this.cname = cname;
        this.price = price;
    }
    public CustomerOrderDemo(){;}

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
