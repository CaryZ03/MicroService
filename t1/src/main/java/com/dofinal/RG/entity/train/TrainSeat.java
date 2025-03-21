package com.dofinal.RG.entity.train;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Arrays;

/**
 * &#064;Classname Seat
 * &#064;Description  TODO
 * &#064;Date 2024/5/5 17:15
 * &#064;Created MuJue
 */


@TableName("train_seat")
public class TrainSeat {
    @TableField("t_id")
    private String trainId;
    @TableField("s_id")
    private String seatId;
    @TableField("s_type")
    private String type;
    @TableField("bitmap")
    private byte[] bitmap;

    public TrainSeat(String trainId, String seatId, String type, byte[] bitmap) {
        this.trainId = trainId;
        this.seatId = seatId;
        this.type = type;
        this.bitmap = bitmap;
    }
    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public void updateBitmapByBuyMap(byte[] BuyMap) {
        for (int i = 0; i < BuyMap.length; i++)
            this.bitmap[i] &= (byte) ~BuyMap[i];
    }
    public void updateBitmapByCancelMap(byte[] CancelMap){
        for(int i = 0;i < CancelMap.length;i++){
            this.bitmap[i] |= CancelMap[i];
        }
    }
}