package com.dofinal.RG.controller;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.SeatDemo;
import com.dofinal.RG.entity.train.Train;
import com.dofinal.RG.reqs.entity.TrainReq;
import com.dofinal.RG.rsps.BaseRsp;
import com.dofinal.RG.service.SeatService;
import com.dofinal.RG.service.TrainService;
import org.apache.ibatis.annotations.Param;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * &#064;Classname TrainController
 * &#064;Description 这个类是涉及和Train本身相关的操作的前端接口。
 * &#064;Date 2024/5/14 19:22
 * &#064;Created MuJue
 */
@CrossOrigin
@RestController
public class TrainController {
    @Autowired
    TrainService trainService;
    @Autowired
    SeatService seatService;

    /**
     * &#064;description: 获得所有的火车信息
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:32
     * &#064;param: []
     * &#064;return: BaseRsp<List<Train>>
     **/
    @GetMapping("/train/getAll")
    @Trace
    public BaseRsp<List<Train>> getTrains() {
        BaseRsp<List<Train>> rsp = new BaseRsp<>();
        List<Train> trains = trainService.findTrains();
        if (trains != null) {
            for (Train train : trains) {
                List<SeatDemo> seatDemos = seatService.getSeatDemoByTid(train.getId());
                train.setSeats(seatDemos);
            }
            rsp.setSuccess(true);
            rsp.setMessage("get trains success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get trains fail!");
        }
        rsp.setContent(trains);
        return rsp;
    }

    /**
     * &#064;description: 通过主键tid来查询火车
     * &#064;author: MuJue
     * &#064;date: 2024/5/17 21:33
     * &#064;param: [tid]
     * &#064;return: BaseRsp<Train>
     **/
    @GetMapping("/train/getByTid/{tid}")
    @Trace
    public BaseRsp<Train> getTrainByTid(@PathVariable("tid") String tid) {
        BaseRsp<Train> rsp = new BaseRsp<>();
        Train train = trainService.findTrainByTid(tid);
        if (train != null) {
            List<SeatDemo> seatDemos = seatService.getSeatDemoByTid(tid);
            train.setSeats(seatDemos);
            rsp.setSuccess(true);
            rsp.setMessage("get train by tid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get train by tid fail!");
        }
        rsp.setContent(train);
        return rsp;
    }

    @GetMapping("/train/getTidByUid/{uid}")
    @Trace
    public BaseRsp<List<String>> getTidByUid(@PathVariable("uid") String uid) {
        BaseRsp<List<String>> rsp = new BaseRsp<>();
        List<String> tids = trainService.getTidByUid(uid);
        if (tids != null) {
            rsp.setSuccess(true);
            rsp.setMessage("get tid by uid success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("get tid by uid fail!");
        }
        rsp.setContent(tids);
        return rsp;
    }

    @PostMapping("/train/getByTimeAndLocation")
    @Trace
    public BaseRsp<List<Train>> getTrainByLocationsAndTimes(@RequestBody TrainReq req) {
        BaseRsp<List<Train>> rsp = new BaseRsp<>();
        if (req.getEndLocation() == null || req.getStartLocation() == null) {
            rsp.setSuccess(false);
            rsp.setMessage("the number of location is wrong!");
            rsp.setContent(null);
            return rsp;
        }
        Location startLocation = req.getStartLocation();
        Location endLocation = req.getEndLocation();
        Timestamp curTime = req.getCurTime();
        if (curTime == null) {
            curTime = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        }

        List<Train> trainList = trainService.findTrainByLocationAndTime(curTime, startLocation, endLocation);
        if (trainList != null) {
            for (Train train : trainList) {
                List<SeatDemo> seatDemos = seatService.getSeatDemoByTidAndLocation(train.getId(), startLocation,
                        endLocation);
                train.setSeats(seatDemos);
            }
            rsp.setSuccess(true);
            rsp.setMessage("find train by time and location success!");
        } else {
            rsp.setSuccess(false);
            rsp.setMessage("find train by time and location fail!");
        }

        rsp.setContent(trainList);
        return rsp;
    }

    // YTY
    @PutMapping("/train/update")
    @Trace
    public BaseRsp updateTrain(@RequestBody TrainReq Req) {
        return null;
    }

    // YTY
    @DeleteMapping("/train/deleteByTid/{tid}")
    @Trace
    public BaseRsp deleteTrainByTid(@PathVariable("tid") int tid) {
        return null;
    }
}
