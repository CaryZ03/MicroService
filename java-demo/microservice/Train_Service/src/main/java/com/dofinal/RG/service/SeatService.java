package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.SeatDemo;
import com.dofinal.RG.entity.train.TrainSeat;

import java.util.List;

public interface SeatService extends IService<TrainSeat> {
    List<TrainSeat> getSeatsByTidType(String tid, String type);
    List<SeatDemo> getSeatDemoByTidAndLocation(String tid, Location startLocation, Location endLocation);
    List<SeatDemo> getSeatDemoByTid(String tid);

    int getSeatCountByTidAndStationIndex(String tid, int boardingStation, int alightingStation,String type);

    String buySeatByTidAndStationIndex(String tid, int boardingStation, int alightingStation,String type);
    boolean cancelSeatByTidAndStationIndex(String tid, String sid, int boardingStation, int alightingStation);
    double getPriceBySeatType(String type);
}
