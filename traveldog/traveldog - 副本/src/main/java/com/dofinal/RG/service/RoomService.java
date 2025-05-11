package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.entity.hotel.RoomDemo;

import java.sql.Timestamp;
import java.util.List;

/**
 * Classname RoomService
 * Description TODO
 * Date 2024/5/24 9:59
 * Created ZHW
 */
public interface RoomService extends IService<HotelRoom> {
    public List<RoomDemo> findHotelRoomDemoByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    public List<RoomDemo> findHotelRoomDemoByHid(int hid);
    public List<HotelRoom> findHotelRoomByHid(int hid);
    public List<HotelRoom> findHotelRoomByTime(Timestamp curTime, Timestamp startTime, Timestamp endTime);
    public List<HotelRoom> findHotelRoomByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    public List<HotelRoom> findHotelRoomByRidAndTime(int rid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    public List<HotelRoom> findHotelRoomByHidRidTime(int hid, int rid, Timestamp curTime,Timestamp startTime, Timestamp endTime);
    public HotelRoom findHotelRoomByHidRidRNumber(int hid, int rid, String rNumber);
    public boolean checkHotelRoom(HotelRoom hotelRoom, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    public int updateHotelRoom(HotelRoom hotelRoom);
    public byte[] orderRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary);
    public byte[] cancelRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary);

}
