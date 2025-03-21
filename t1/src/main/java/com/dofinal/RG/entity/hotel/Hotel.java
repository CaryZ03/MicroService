package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * &#064;Classname Hotel
 * &#064;Description TODO
 * &#064;Date 2024/5/5 17:03
 * &#064;Created MuJue
 */
@TableName("hotels")
public class Hotel {
    @TableField("h_id")
    private int id;
    @TableField("h_name")
    private String name;
    @TableField("l_id")
    private int locationId;
    @TableField("price_rate")
    private double priceRate;
    @TableField("h_tel")
    private String phoneNumber;
    @TableField("detail_location")
    private String detailLocation;
    private String description;
    @TableField(exist = false)
    private List<RoomDemo> rooms;
    @TableField(exist = false)
    private double rate;
    @TableField(exist = false)
    private List<String> comments;

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

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }

    public double getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(double priceRate) {
        this.priceRate = priceRate;
    }

    public List<RoomDemo> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDemo> rooms) {
        this.rooms = rooms;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
