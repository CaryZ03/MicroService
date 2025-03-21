package com.dofinal.RG.entity.train;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname Meal
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 17:24
 * &#064;Created MuJue
 */
@TableName("meals")
public class Meal {
    @TableField("m_id")
    private int id;
    @TableField("m_name")
    private String name;
    @TableField("m_type")
    private String type;
    @TableField("m_price")
    double price;
    @TableField("m_detail")
    private String detail;//对火车餐的描述。

    public Meal(int id, String name, String type, double price, String detail) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.detail = detail;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
