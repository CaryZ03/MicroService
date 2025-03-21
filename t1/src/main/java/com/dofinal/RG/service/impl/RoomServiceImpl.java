package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.entity.hotel.Room;
import com.dofinal.RG.entity.hotel.RoomDemo;
import com.dofinal.RG.mapper.HotelMapper;
import com.dofinal.RG.mapper.HotelRoomMapper;
import com.dofinal.RG.mapper.RoomMapper;
import com.dofinal.RG.service.RoomService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classname RoomServiceImpl
 * Description TODO
 * Date 2024/5/24 10:01
 * Created ZHW
 */
@Service
public class RoomServiceImpl extends ServiceImpl<HotelRoomMapper, HotelRoom> implements RoomService {

    long getDay(Timestamp time) {
        Date date = new Date(time.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Autowired
    private HotelRoomMapper hotelRoomMapper;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private RoomMapper roomMapper;

    @Override
    @Trace
    public List<RoomDemo> findHotelRoomDemoByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        List<RoomDemo> roomDemos = new ArrayList<>();
        List<HotelRoom> hotelRooms = findHotelRoomByHidAndTime(hid, curTime, startTime, endTime);

        handleRoomDemo(hotelRooms, roomDemos);

        return roomDemos;
    }

    @Override
    @Trace
    public List<RoomDemo> findHotelRoomDemoByHid(int hid) {
        List<RoomDemo> roomDemos = new ArrayList<>();
        List<HotelRoom> hotelRooms = findHotelRoomByHid(hid);

        handleRoomDemo(hotelRooms, roomDemos);

        return roomDemos;
    }

    private void handleRoomDemo(List<HotelRoom> hotelRooms,List<RoomDemo> roomDemos){
        for(HotelRoom hotelRoom : hotelRooms){
            Room room = roomMapper.getRoomByRid(hotelRoom.getRoomId());
            String type = room.getType();

            int flag = 0;
            for(RoomDemo roomDemo : roomDemos){
                if(roomDemo.getType().equals(type)){
                    roomDemo.setCount(roomDemo.getCount() + 1);
                    flag = 1;
                    break;
                }
            }
            if(flag == 1){
                continue;
            }
            else{
                double price_rate = hotelMapper.findHotelPriceRateByHid(hotelRoom.getHotelId());
                double price = roomMapper.getRoomPriceByRid(room.getId()) * price_rate;
                RoomDemo roomDemo = new RoomDemo(room.getId(), type, price, 1, room.getDetail());
                roomDemos.add(roomDemo);
            }
        }
    }

    @Override
    @Trace
    public List<HotelRoom> findHotelRoomByHid(int hid) {
        return hotelRoomMapper.getHotelRoomByHid(hid);
    }

    @Override
    @Trace
    public List<HotelRoom> findHotelRoomByTime(Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        List<HotelRoom> hotelRooms = hotelRoomMapper.getHotelRoom();
        List<HotelRoom> availableRooms = new ArrayList<>();

        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }
        for (HotelRoom hotelRoom : hotelRooms) {
            byte[] curBinary = hotelRoom.getBitmap();
            boolean isAvailable = true;

            for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
                int index = 3 - (int) day / 8;
                int offset = (int) day % 8;

                if (index >= curBinary.length || (curBinary[index] & (1 << offset)) == 0) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableRooms.add(hotelRoom);
            }
        }

        return availableRooms;
    }

    @Override
    @Trace
    public List<HotelRoom> findHotelRoomByHidAndTime(int hid, Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        List<HotelRoom> hotelRooms = hotelRoomMapper.getHotelRoomByHid(hid);
        List<HotelRoom> availableRooms = new ArrayList<>();

        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }

        for (HotelRoom hotelRoom : hotelRooms) {
            byte[] curBinary = hotelRoom.getBitmap();
            boolean isAvailable = true;

            for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
                int index = 3 - (int) day / 8;
                int offset = (int) day % 8;

                if (index >= curBinary.length || (curBinary[index] & (1 << offset)) == 0) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableRooms.add(hotelRoom);
            }
        }

        return availableRooms;
    }

    @Override
    @Trace
    public List<HotelRoom> findHotelRoomByRidAndTime(int rid, Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        List<HotelRoom> hotelRooms = hotelRoomMapper.getHotelRoomByRid(rid);
        List<HotelRoom> availableRooms = new ArrayList<>();

        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }

        for (HotelRoom hotelRoom : hotelRooms) {
            byte[] curBinary = hotelRoom.getBitmap();
            boolean isAvailable = true;

            for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
                int index = 3 - (int) day / 8;
                int offset = (int) day % 8;

                if (index >= curBinary.length || (curBinary[index] & (1 << offset)) == 0) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableRooms.add(hotelRoom);
            }
        }
        return availableRooms;
    }

    @Override
    @Trace
    public List<HotelRoom> findHotelRoomByHidRidTime(int hid, int rid, Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        List<HotelRoom> hotelRooms = hotelRoomMapper.getHotelRoomByHidRid(hid, rid);
        List<HotelRoom> availableRooms = new ArrayList<>();

        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }

        for (HotelRoom hotelRoom : hotelRooms) {
            byte[] curBinary = hotelRoom.getBitmap();
            boolean isAvailable = true;

            for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
                int index = 3 - (int) day / 8;
                int offset = (int) day % 8;

                if (index >= curBinary.length || (curBinary[index] & (1 << offset)) == 0) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                availableRooms.add(hotelRoom);
            }
        }
        return availableRooms;
    }

    @Override
    @Trace
    public HotelRoom findHotelRoomByHidRidRNumber(int hid, int rid, String rNumber) {
        if (hid >= 0 && rid >= 0 && rNumber != null) {
            return hotelRoomMapper.getHotelRoomByHidRidRNumber(hid, rid, rNumber);
        }
        return null;
    }

    @Override
    @Trace
    public boolean checkHotelRoom(HotelRoom hotelRoom, Timestamp curTime, Timestamp startTime, Timestamp endTime) {
        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return false;
        }

        byte[] curBinary = hotelRoom.getBitmap();
        boolean isAvailable = true;

        for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
            int index = 3 - (int) day / 8;
            int offset = (int) day % 8;

            if (index >= curBinary.length || (curBinary[index] & (1 << offset)) == 0) {
                isAvailable = false;
                break;
            }
        }

        return isAvailable;
    }

    @Override
    @Trace
    public int updateHotelRoom(HotelRoom hotelRoom) {
        return hotelRoomMapper.updateHotelRoom(hotelRoom);
    }

    @Override
    @Trace
    public byte[] orderRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary) {
        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }
        if (curBinary == null) return null;
        for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
            int index = 3 - (int) day / 8;
            int offset = (int) day % 8;
            curBinary[index] = (byte) (curBinary[index] & (~(1 << offset)));
        }
        return curBinary;
    }

    @Override
    @Trace
    public byte[] cancelRoom(Timestamp curTime, Timestamp startTime, Timestamp endTime, byte[] curBinary) {
        long daysFromNowToStart = getDay(startTime) - getDay(curTime);
        long daysFromNowToEnd = getDay(endTime) - getDay(curTime);
        if (daysFromNowToStart < 0 || daysFromNowToEnd < daysFromNowToStart) {
            return null;
        }
        if (curBinary == null) return null;
        for (long day = daysFromNowToStart; day <= daysFromNowToEnd; day++) {
            int index = 3 - (int) day / 8;
            int offset = (int) day % 8;
            int mask = (1 << offset);
            curBinary[index] = (byte) (curBinary[index] | mask);
        }
        return curBinary;
    }
}
