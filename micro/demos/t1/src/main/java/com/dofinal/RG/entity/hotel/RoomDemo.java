package com.dofinal.RG.entity.hotel;

/**
 * &#064;Classname RoomDemo
 * &#064;Description  TODO
 * &#064;Date 2024/5/28 12:13
 * &#064;Created MuJue
 */
public class RoomDemo {
    private int rid;
    private String type;
    private Double price;
    private Integer count;
    private String details;

    public RoomDemo(int rid, String type, Double price, Integer count, String details) {
        this.rid = rid;
        this.type = type;
        this.price = price;
        this.count = count;
        this.details = details;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
