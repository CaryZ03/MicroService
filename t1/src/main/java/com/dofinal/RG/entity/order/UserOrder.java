package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import java.util.List;

/**
 * &#064;Classname UserOrder
 * &#064;Description TODO
 * &#064;Date 2024/5/16 19:30
 * &#064;Created MuJue
 */
@TableName("users")
public class UserOrder implements Comparable<UserOrder> {
    @TableField("o_id")
    private int orderId;
    @TableField("u_id")
    private String userId;
    @TableField("price")
    private double price;
    @TableField("add_time")
    private Timestamp addTime;
    @TableField("uo_status")
    private String status;
    @TableField("service_type")
    private String type;
    @TableField("is_comment")
    private boolean isComment;

    public UserOrder(int orderId, String userId, double price, Timestamp addTime, String status, String type,
            boolean isComment) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.addTime = addTime;
        this.status = status;
        this.type = type;
        this.isComment = isComment;
    }

    public UserOrder() {
        ;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    @Override
    public int compareTo(UserOrder o) {
        if (o.getType().equals("mealOrder") && !this.getType().equals("mealOrder")) {
            return 1;
        } else if (!o.getType().equals("mealOrder") && this.getType().equals("mealOrder")) {
            return -1;
        }
        return o.getType().compareTo(this.getType());
    }
}
