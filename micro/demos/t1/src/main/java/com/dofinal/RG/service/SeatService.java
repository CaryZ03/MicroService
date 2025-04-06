package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.SeatDemo;
import com.dofinal.RG.entity.train.TrainSeat;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

public interface SeatService extends IService<TrainSeat> {
    @Trace
    List<TrainSeat> getSeatsByTidType(String tid, String type);
    @Trace
    List<SeatDemo> getSeatDemoByTidAndLocation(String tid, Location startLocation, Location endLocation);
    @Trace
    List<SeatDemo> getSeatDemoByTid(String tid);

    @Trace
    int getSeatCountByTidAndStationIndex(String tid, int boardingStation, int alightingStation,String type);

    @Trace
    String buySeatByTidAndStationIndex(String tid, int boardingStation, int alightingStation,String type);
    @Trace
    boolean cancelSeatByTidAndStationIndex(String tid, String sid, int boardingStation, int alightingStation);
}
