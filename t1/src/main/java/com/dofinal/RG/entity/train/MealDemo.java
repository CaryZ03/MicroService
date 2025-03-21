package com.dofinal.RG.entity.train;

/**
 * &#064;Classname MealDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/2 16:29
 * &#064;Created MuJue
 */
public class MealDemo {
    private String tid;
    private int mid;
    private String name;
    private double price;
    private int count;
    private String detail;
    private String type;

    public MealDemo(String tid, int mid, String name, double price, int count, String detail, String type) {
        this.tid = tid;
        this.mid = mid;
        this.name = name;
        this.price = price;
        this.count = count;
        this.detail = detail;
        this.type = type;
    }

    public String getTid() {
        return tid;
    }
    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }
}
