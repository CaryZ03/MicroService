package com.dofinal.RG.entity.order;

import com.baomidou.mybatisplus.annotation.TableField;

import java.sql.Timestamp;

/**
 * &#064;Classname Order
 * &#064;Description  TODO
 * &#064;Date 2024/5/17 20:41
 * &#064;Created MuJue
 */
public class CustomerOrder {
    @TableField("o_id")
    protected int orderId;
    @TableField("c_id")
    protected int customerId;
    @TableField("add_time")
    protected Timestamp addTime;
    public CustomerOrder(int orderId, int customerId, Timestamp addTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.addTime = addTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }
}
