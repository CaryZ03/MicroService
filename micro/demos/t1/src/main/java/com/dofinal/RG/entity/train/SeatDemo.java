package com.dofinal.RG.entity.train;

/**
 * &#064;Classname SeatDemo
 * &#064;Description  TODO
 * &#064;Date 2024/6/2 16:30
 * &#064;Created MuJue
 */
public class SeatDemo {
    private String type;
    private int count;
    private double price;

    public SeatDemo(String type, int count, double price) {
        this.type = type;
        this.count = count;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
