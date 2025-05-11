package com.dofinal.RG.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * &#064;Classname User
 * &#064;Description TODO
 * &#064;Date 2024/4/21 15:04
 * &#064;Created MuJue
 */
@TableName("users")
public class User {
    @TableField("u_id")
    private String id; // 注册账号
    @TableField("u_name") // 数据库中和username对应的属性列的名字为name
    private String name;
    @TableField("u_password")
    private String password; // 注册密码
    @TableField("u_tel")
    private String phoneNumber; // 电话号码
    private double money = 2000.0;
    private String status;
    @TableField(exist = false)
    private List<Customer> customers;
    @TableField(exist = false)
    private List<UserOrderDemo> userOrders;

    public User(String id, String name, String password, String phoneNumber, double money, String status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.money = money;
        this.status = status;
    }

    /**
     * &#064;description: a User object is created ONLY when someone log in, and we
     * create a
     * User object BASED on data in database. Everytime we change user's
     * information, we change
     * the information in User object FIRST, and then write it back to database.
     * 人话就是，User实例只是一个缓冲，我们读取数据库的数据后，先放在
     * User示例中，发生了任何的改变，就先在User实例中改变，然后再写回到数据库中。
     * &#064;author: MuJue
     * &#064;date: 2024/4/22 20:05
     * &#064;param: [uid, password, telNumber, gender, pid, location]
     * &#064;return:
     **/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<UserOrderDemo> getUserOrders() {
        return userOrders;
    }

    public void setUserOrders(List<UserOrderDemo> userOrders) {
        this.userOrders = userOrders;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
