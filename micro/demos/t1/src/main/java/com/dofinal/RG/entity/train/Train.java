package com.dofinal.RG.entity.train;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.location.TrainLocationDemo;

import java.util.List;


/**
 * &#064;Classname Train
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 17:13
 * &#064;Created MuJue
 */
@TableName("trains")
public class Train {
    @TableField("t_id")
    private String id;
    @TableField("type")
    private String type;
    @TableField(exist = false)
    private List<SeatDemo> seats;
    @TableField(exist = false)
    private List<TrainLocationDemo> locations;

    public Train(String id, String type) {
        this.id = id;
        this.type = type;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SeatDemo> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDemo> seats) {
        this.seats = seats;
    }

    public List<TrainLocationDemo> getLocations() {
        return locations;
    }

    public void setLocations(List<TrainLocationDemo> locations) {
        this.locations = locations;
    }
}
