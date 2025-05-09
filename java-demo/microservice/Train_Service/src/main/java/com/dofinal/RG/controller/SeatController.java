package com.dofinal.RG.controller;

import com.dofinal.RG.client.LocationClient;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.reqs.entity.SeatReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.SeatService;
import com.dofinal.RG.util.NoticeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class SeatController {
    @Autowired
    SeatService seatService;
    @Autowired
    LocationClient locationClient;
    @Autowired
    NoticeUtil noticeUtil;

    @GetMapping("seat/getCount")
    public BaseRsp<Integer> getSeatCount(@RequestBody SeatReq req) {
        Location beginLocation = req.getBeginLocation();
        Location endLocation = req.getEndLocation();
        int bs = locationClient.getLidByProvinceCity(beginLocation.getProvince(), beginLocation.getCity());
        int es = locationClient.getLidByProvinceCity(endLocation.getProvince(), endLocation.getCity());

        int seatCount = seatService.getSeatCountByTidAndStationIndex(req.getTid(), bs, es, req.getSeatType());
        BaseRsp<Integer> rsp = new BaseRsp<>();
        if (seatCount >= 0) {
            rsp.setSuccess(true);
            rsp.setMessage("get seat count success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get seat count fail!");
        }
        rsp.setContent(seatCount);
        return rsp;
    }

    @PostMapping("/seat/buy")
    public BaseRsp<String> buySeat(@RequestBody SeatReq req) {
        Location beginLocation = req.getBeginLocation();
        Location endLocation = req.getEndLocation();
        int bs = locationClient.getLidByProvinceCity(beginLocation.getProvince(), beginLocation.getCity());
        int es = locationClient.getLidByProvinceCity(endLocation.getProvince(), endLocation.getCity());

        String sid = seatService.buySeatByTidAndStationIndex(req.getTid(), bs, es, req.getSeatType());
        BaseRsp<String> rsp = new BaseRsp<>();
        if (sid != null) {
            rsp.setSuccess(true);
            rsp.setMessage("buy seat success!");

            String content = "您已成功预定" + req.getTid() + "火车的" + req.getSid() + "号座位！";
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("buy seat fail!");

            String content = "您预定" + req.getTid() + "火车的" + req.getSid() + "号座位订单预定失败，请重新预定！";
            noticeUtil.addNotice(req.getUid(), content);
        }
        rsp.setContent(sid);
        return rsp;
    }

    @PostMapping("/seat/cancel")
    public BaseRsp<Boolean> cancelSeat(@RequestBody SeatReq req) {
        Location beginLocation = req.getBeginLocation();
        Location endLocation = req.getEndLocation();
        int bs = locationClient.getLidByProvinceCity(beginLocation.getProvince(), beginLocation.getCity());
        int es = locationClient.getLidByProvinceCity(endLocation.getProvince(), endLocation.getCity());

        boolean res = seatService.cancelSeatByTidAndStationIndex(req.getTid(), req.getSid(), bs, es);
        BaseRsp<Boolean> rsp = new BaseRsp<>();
        if (res) {
            rsp.setMessage("cancel seat success!");

            String content = "您已成功取消" + req.getTid() + "火车的" + req.getSid() + "号座位订单！";
            noticeUtil.addNotice(req.getUid(), content);
        } else {
            rsp.setMessage("cancel seat fail!");

            String content = "您" + req.getTid() + "火车的" + req.getSid() + "号座位订单取消失败，请重新操作。";
            noticeUtil.addNotice(req.getUid(), content);
        }
        rsp.setSuccess(res);
        rsp.setContent(res);
        return rsp;
    }

    @GetMapping("/seat/getCountByTidIndexType/{tid}/{bs}/{as}/{type}")
    public int getSeatCountByTidAndStationIndex(@PathVariable String tid, @PathVariable int bs, @PathVariable int as, @PathVariable String type){
        return seatService.getSeatCountByTidAndStationIndex(tid, bs, as, type);
    }

    @GetMapping("/seat/getPriceByType/{type}")
    public double getPriceBySeatType(@PathVariable String type){
        return seatService.getPriceBySeatType(type);
    }

    @PostMapping("/seat/buyByTidIndexType/{tid}/{bs}/{as}/{type}")
    public String buySeatByTidAndStationIndex(@PathVariable String tid, @PathVariable int bs, @PathVariable int as, @PathVariable String type){
        return seatService.buySeatByTidAndStationIndex(tid, bs, as, type);
    }

    @PostMapping("/seat/cancelByTidSidIndex/{tid}/{sid}/{bs}/{as}")
    public boolean cancelSeatByTidAndStationIndex(@PathVariable String tid, @PathVariable String sid, @PathVariable int bs, @PathVariable int as){
        return seatService.cancelSeatByTidAndStationIndex(tid, sid, bs, as);
    }


}
