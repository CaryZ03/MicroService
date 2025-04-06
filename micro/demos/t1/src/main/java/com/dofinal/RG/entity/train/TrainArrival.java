package com.dofinal.RG.entity.train;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * &#064;Classname TrainArrival
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 14:11
 * &#064;Created MuJue
 */

@TableName("train_arrivals")
public class TrainArrival {
    @TableField("t_id")
    private int trainId;
    @TableField("l_id")
    private int locationId;
    @TableField("arrival_time")
    private Date arrivalTime;
    @TableField("station_sequence")
    private int stationIndex;
    @TableField("detail_location")
    private String detailLocation;

    public TrainArrival(int trainId, int locationId, Date arrivalTime, int stationIndex, String detailLocation) {
        this.trainId = trainId;
        this.locationId = locationId;
        this.arrivalTime = arrivalTime;
        this.stationIndex = stationIndex;
        this.detailLocation = detailLocation;
    }

    @Override
    public String toString() {
        return "TrainArrival{" +
                "tid=" + trainId +
                ", lid=" + locationId +
                ", arrivalTime=" + arrivalTime +
                ", stationIndex=" + stationIndex +
                '}';
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setStationIndex(int stationIndex) {
        this.stationIndex = stationIndex;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public int getStationIndex() {
        return stationIndex;
    }

    public String getDetailLocation() {
        return detailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        this.detailLocation = detailLocation;
    }
}
