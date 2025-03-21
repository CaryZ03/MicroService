package com.dofinal.RG.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname UserCustomer
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 15:19
 * &#064;Created MuJue
 */
@TableName("users_customers")
public class UserCustomer {
    @TableField("u_id")
    private String userId;
    @TableField("c_id")
    private int customerId;

    public UserCustomer(String userId, int customerId) {
        this.userId = userId;
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "UserCustomer{" +
                "uid='" + userId + '\'' +
                ", cid=" + customerId +
                '}';
    }
}
