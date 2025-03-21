package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname Room
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:49
 * &#064;Created MuJue
 */
@TableName("rooms")
public class Room {
    @TableField("r_id")
    private int id;
    @TableField("r_name")
    private String name;
    @TableField("r_type")
    private String type;
    @TableField("r_detail")
    private String detail;
    @TableField("r_price")
    private Double price;

    public Room(int id, String name, String type, String detail, Double price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.detail = detail;
        this.price = price;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
