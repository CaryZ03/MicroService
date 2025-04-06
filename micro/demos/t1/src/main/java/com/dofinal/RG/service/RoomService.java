package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.entity.hotel.RoomDemo;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.sql.Timestamp;
import java.util.List;

/**
 * Classname RoomService
 * Description TODO
 * Date 2024/5/24 9:59
 * Created ZHW
 */
public interface RoomService extends IService<HotelRoom> {
    @Trace
    public List<RoomDemo> findHotelRoomDemoByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    @Trace
    public List<RoomDemo> findHotelRoomDemoByHid(int hid);
    @Trace
    public List<HotelRoom> findHotelRoomByHid(int hid);
    @Trace
    public List<HotelRoom> findHotelRoomByTime(Timestamp curTime, Timestamp startTime, Timestamp endTime);
    @Trace
    public List<HotelRoom> findHotelRoomByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    @Trace
    public List<HotelRoom> findHotelRoomByRidAndTime(int rid, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    @Trace
    public List<HotelRoom> findHotelRoomByHidRidTime(int hid, int rid, Timestamp curTime,Timestamp startTime, Timestamp endTime);
    @Trace
    public HotelRoom findHotelRoomByHidRidRNumber(int hid, int rid, String rNumber);
    @Trace
    public boolean checkHotelRoom(HotelRoom hotelRoom, Timestamp curTime, Timestamp startTime, Timestamp endTime);
    @Trace
    public int updateHotelRoom(HotelRoom hotelRoom);
    @Trace
    public byte[] orderRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary);
    @Trace
    public byte[] cancelRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary);

}
