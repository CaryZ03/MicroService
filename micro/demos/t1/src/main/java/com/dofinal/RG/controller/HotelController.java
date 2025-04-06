package com.dofinal.RG.controller;

import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.RoomDemo;
import com.dofinal.RG.reqs.entity.HotelReq;
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
 * Classname HotelController
 * Description 这个类是涉及酒店本身信息的操作和前端的接口。
 * Date 2024/5/15 10:48
 * Created ZHW
 */
@CrossOrigin
@RestController
public class HotelController {
    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;

    /**
     * &#064;description:通过目的地来查询酒店。这是很自然的。
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:23
     * &#064;param: [loc]
     * &#064;return: BaseRsp<List<Hotel>>
     **/
    @PostMapping("/hotel/getByLocation")
    @Trace
    public BaseRsp<List<Hotel>> getHotelByLocationTime(@RequestBody HotelReq req) {
        List<Hotel> hotelList = hotelService.findHotelByLocation(req.getLocation());
        BaseRsp<List<Hotel>> hotelListBaseRsp = new BaseRsp<>();

        if (hotelList != null) {

            for (Hotel hotel : hotelList) {
                int hid = hotel.getId();
                Timestamp curTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
                Timestamp startTime = req.getStartTime();
                Timestamp endTime = req.getEndTime();
                List<RoomDemo> roomDemos = roomService.findHotelRoomDemoByHidAndTime(hid, curTime, startTime, endTime);
                hotel.setRooms(roomDemos);
            }

            hotelListBaseRsp.setSuccess(true);
            hotelListBaseRsp.setMessage("get hotels by location success!");
        } else {
            hotelListBaseRsp.setSuccess(false);
            hotelListBaseRsp.setMessage("get hotels by location fail!");
        }
        hotelListBaseRsp.setContent(hotelList);
        return hotelListBaseRsp;
    }

    /**
     * &#064;description: 通过主键hid来查询酒店
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:37
     * &#064;param: [hid]
     * &#064;return: BaseRsp<Hotel>
     **/
    @GetMapping("/hotel/getByHid/{hid}")
    @Trace
    public BaseRsp<Hotel> getHotelByHid(@PathVariable("hid") int hid) {
        Hotel hotel = hotelService.findHotelById(hid);
        BaseRsp<Hotel> rsp = new BaseRsp<>();

        if (hotel != null) {
            List<RoomDemo> roomDemos = roomService.findHotelRoomDemoByHid(hid);
            hotel.setRooms(roomDemos);

            rsp.setSuccess(true);
            rsp.setMessage("get hotel by hid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get hotel by hid fail!");
        }
        rsp.setContent(hotel);
        return rsp;
    }

    // ZHW
    @DeleteMapping("hotel/deleteByHid/{hid}")
    @Trace
    public BaseRsp deleteHotelByHid(@PathVariable("hid") int hid) {
        int res = hotelService.deleteHotelByHid(hid);
        BaseRsp rsp = new BaseRsp();
        if (res != 0) {
            rsp.setSuccess(true);
            rsp.setMessage("delete hotel success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("delete hotel false!");
        }
        return rsp;
    }
}
