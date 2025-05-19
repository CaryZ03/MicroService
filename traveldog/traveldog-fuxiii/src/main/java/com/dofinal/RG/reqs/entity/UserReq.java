package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.entity.user.User;
import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * &#064;Classname UserReq
 * &#064;Description TODO
 * &#064;Date 2024/5/17 20:50
 * &#064;Created MuJue
 */
public class UserReq extends BaseReq {
    private String password;
    private String phoneNumber;
    private String name;
    private double money;
    private String status;

    public UserReq(String uid, String password, String phoneNumber, String name, double money, String status) {
        super(uid);
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.money = money;
        this.status = status;
    }

    public UserReq() {
        ;
    }

    public User getUser() {
        return new User(uid, name, password, phoneNumber, money, status);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
