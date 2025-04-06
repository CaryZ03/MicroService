package com.dofinal.RG.entity.hotel;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * &#064;Classname HotelRoom
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:01
 * &#064;Created MuJue
 */
@TableName("hotel_room")
public class HotelRoom {
    @TableField("h_id")
    private int hotelId;
    @TableField("r_id")
    private int roomId;
    @TableField("r_number")
    private String roomNumber;
    @TableField("bitmap")
    private byte[] bitmap;


    public HotelRoom(int hotelId, int roomId, String roomNumber, byte[] bitmap) {
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.bitmap = bitmap;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }
}
