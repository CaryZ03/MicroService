package com.dofinal.RG.controller;

import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.reqs.entity.RoomReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.HotelService;
import com.dofinal.RG.service.RoomService;
import com.dofinal.RG.util.NoticeUtil;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Classname RoomController
 * Description TODO
 * Date 2024/5/25 10:05
 * Created ZHW
 */
@CrossOrigin
@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private NoticeUtil noticeUtil;
    @Autowired
    private HotelService hotelService;

    @GetMapping("/room/getByHid/{hid}")
    @Trace
    public BaseRsp<List<HotelRoom>> findHotelRoomByHid(@PathVariable("hid") int hid) {
        List<HotelRoom> hotelRooms = roomService.findHotelRoomByHid(hid);
        BaseRsp<List<HotelRoom>> rsp = new BaseRsp<>();
        if (hotelRooms != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get rooms by hid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get rooms by hid fail!");
        }
        rsp.setContent(hotelRooms);
        return rsp;
    }

    @GetMapping("/room/getByTime")
    @Trace
    public BaseRsp<List<HotelRoom>> findHotelRoomByTime(@RequestBody RoomReq req) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        BaseRsp<List<HotelRoom>> rsp = new BaseRsp<>();
        List<HotelRoom> hotelRooms = roomService.findHotelRoomByTime(curTime, req.getFromTime(), req.getToTime());
        if (hotelRooms != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get rooms by time success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get rooms by time fail!");
        }
        rsp.setContent(hotelRooms);
        return rsp;
    }

    @GetMapping("/room/getByHidAndTime")
    @Trace
    public BaseRsp<List<HotelRoom>> findHotelRoomByHidAndTime(@RequestBody RoomReq req) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        BaseRsp<List<HotelRoom>> rsp = new BaseRsp<>();
        List<HotelRoom> hotelRooms = roomService.findHotelRoomByHidAndTime(req.getHid(), curTime, req.getFromTime(),
                req.getToTime());
        if (hotelRooms != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get rooms by hid and time success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get rooms by hid and time fail!");
        }
        rsp.setContent(hotelRooms);
        return rsp;
    }

    @GetMapping("/room/getByRidAndTime")
    @Trace
    public BaseRsp<List<HotelRoom>> findHotelRoomByRidAndTime(@RequestBody RoomReq req) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        BaseRsp<List<HotelRoom>> rsp = new BaseRsp<>();
        List<HotelRoom> hotelRooms = roomService.findHotelRoomByRidAndTime(req.getRid(), curTime, req.getFromTime(),
                req.getToTime());
        if (hotelRooms != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get rooms by rid and time success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get rooms by rid and time fail!");
        }
        rsp.setContent(hotelRooms);
        return rsp;
    }

    @PostMapping("/room/orderRoom")
    @Trace
    public BaseRsp orderRoom(@RequestBody RoomReq req) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        Timestamp startTime = req.getFromTime();
        Timestamp endTime = req.getToTime();
        HotelRoom hotelRoom = roomService.findHotelRoomByHidRidRNumber(req.getHid(), req.getRid(), req.getrNumber());
        BaseRsp rsp = new BaseRsp();

        if (roomService.checkHotelRoom(hotelRoom, curTime, startTime, endTime)) {

            byte[] curBinary = hotelRoom.getBitmap();
            curBinary = roomService.orderRoom(curTime, startTime, endTime, curBinary);

            hotelRoom.setBitmap(curBinary);
            roomService.updateHotelRoom(hotelRoom);
            rsp.setSuccess(true);
            rsp.setMessage("order room success!");

            String content = "您已成功预定" + hotelService.findHotelById(req.getHid()).getName() + "酒店的" + req.getrNumber()
                    + "号房间";
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("order room false!");

            String content = hotelService.findHotelById(req.getHid()).getName() + "酒店的房间预定失败，请重新预定。";
            noticeUtil.addNotice(req.getUid(), content);
        }
        return rsp;
    }

    @PostMapping("/room/cancelRoom")
    @Trace
    public BaseRsp cancelRoom(@RequestBody RoomReq req) {
        Timestamp curTime = new Timestamp(System.currentTimeMillis());
        Timestamp startTime = req.getFromTime();
        Timestamp endTime = req.getToTime();
        HotelRoom hotelRoom = roomService.findHotelRoomByHidRidRNumber(req.getHid(), req.getRid(), req.getrNumber());
        BaseRsp rsp = new BaseRsp();

        byte[] curBinary = hotelRoom.getBitmap();
        curBinary = roomService.cancelRoom(curTime, startTime, endTime, curBinary);

        hotelRoom.setBitmap(curBinary);
        if (roomService.updateHotelRoom(hotelRoom) != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("cancel room success!");

            String content = "您已成功取消" + hotelService.findHotelById(req.getHid()).getName() + "酒店的" + req.getrNumber()
                    + "号房间的订单";
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("cancel room false!");

            String content = hotelService.findHotelById(req.getHid()).getName() + "酒店的" + req.getrNumber()
                    + "号房间订单取消失败。";
            noticeUtil.addNotice(req.getUid(), content);
        }
        return rsp;
    }
}
