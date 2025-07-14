package com.dofinal.RG.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname Customer
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:59
 * &#064;Created MuJue
 */
@TableName("customers")
public class Customer {
    @TableField("c_id")
    private int id;
    @TableField("c_name")
    private String name;
    @TableField("c_tel")
    private String phoneNumber;
    @TableField("id_card")
    private String idCard;
    @TableField("cage")
    private int age;

    public Customer(int id, String name, String phoneNumber, String idCard, int age) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.idCard = idCard;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCard='" + idCard + '\'' +
                ", age=" + age +
                '}';
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
