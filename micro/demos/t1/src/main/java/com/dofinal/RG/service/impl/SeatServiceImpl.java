package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.SeatDemo;
import com.dofinal.RG.entity.train.TrainSeat;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.service.SeatService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl extends ServiceImpl<TrainSeatMapper, TrainSeat> implements SeatService {
    @Autowired
    private TrainSeatMapper trainSeatMapper;
    @Autowired
    private SeatPriceMapper seatPriceMapper;
    @Autowired
    private TrainMapper trainMapper;
    @Autowired
    private TrainArrivalMapper trainArrivalMapper;
    @Autowired
    private LocationMapper locationMapper;

    static final int maxBitMapByte = 4;

    @Override
    @Trace
    public List<TrainSeat> getSeatsByTidType(String tid, String type) {
        if (tid == null) return null;
        return trainSeatMapper.getTrainSeatsByTidType(tid, type);

    }

    @Override
    @Trace
    public List<SeatDemo> getSeatDemoByTidAndLocation(String tid, Location sl, Location el) {
        List<TrainSeat> trainSeats = trainSeatMapper.getTrainSeatByTid(tid);
        if(trainSeats == null){
            return null;
        }
        List<String> types = new ArrayList<>();
        for(TrainSeat trainSeat : trainSeats){
            String type = trainSeat.getType();

            int flag = 0;
            for(String ttype : types){
                if(ttype.equals(type)){
                    flag = 1;
                    break;
                }
            }
            if(flag == 0){
                types.add(type);
            }
        }

        List<SeatDemo> seatDemos = new ArrayList<>();
        int slid = locationMapper.getLidByProvinceCity(sl.getProvince(), sl.getCity());
        int elid = locationMapper.getLidByProvinceCity(el.getProvince(), el.getCity());

        int boardingStation = trainArrivalMapper.getStationSequenceByTidAndLid(tid, slid);
        int alightingStation = trainArrivalMapper.getStationSequenceByTidAndLid(tid, elid);

        for(String type : types){
            int count = getSeatCountByTidAndStationIndex(tid, boardingStation, alightingStation, type);
            double price = seatPriceMapper.getPriceBySeatType(type);
            double price_rate = trainMapper.findTrainPriceRateByTid(tid);
            SeatDemo seatDemo = new SeatDemo(type, count, price * price_rate);
            seatDemos.add(seatDemo);
        }
        return seatDemos;
    }

    @Override
    @Trace
    public List<SeatDemo> getSeatDemoByTid(String tid) {
        if(tid == null){
            return null;
        }
        else{
            List<TrainSeat> trainSeats = trainSeatMapper.getTrainSeatByTid(tid);
            if(trainSeats == null){
                return null;
            }
            List<String> types = new ArrayList<>();

            for(TrainSeat trainSeat : trainSeats){
                String type = trainSeat.getType();

                int flag = 0;
                for(String ttype : types){
                    if(ttype.equals(type)){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    types.add(type);
                }
            }

            List<SeatDemo> seatDemos = new ArrayList<>();

            for(String type : types){
                int count = getSeatCountByTidType(tid, type);
                double price = seatPriceMapper.getPriceBySeatType(type);
                double rate = trainMapper.findTrainPriceRateByTid(tid);
                SeatDemo seatDemo = new SeatDemo(type, count, price * rate);
                seatDemos.add(seatDemo);
            }

            return seatDemos;
        }
    }
    private int getSeatCountByTidType(String tid, String type){
        List<TrainSeat> trainSeats = trainSeatMapper.getTrainSeatsByTidType(tid, type);
        return trainSeats.size();
    }
    @Override
    @Trace
    public int getSeatCountByTidAndStationIndex(String tid, int boardingStation, int alightingStation, String type) {
        if (tid == null || type == null) return -1;
        if (boardingStation < 0 || boardingStation >= alightingStation) return -1;

        List<TrainSeat> seats = trainSeatMapper.getTrainSeatsByTidType(tid, type);

        byte[] SearchMap = new byte[maxBitMapByte];
        for (int bit = boardingStation - 1; bit < alightingStation - 1; bit++) {
            int index = maxBitMapByte - 1 - bit / 8;
            int offset = bit % 8;
            SearchMap[index] |= (byte) (1 << offset);
        }
        int count = 0;
        boolean free;
        for (TrainSeat seat : seats) {
            byte[] bitmap = seat.getBitmap();
            free = true;
            for (int i = 0; i < maxBitMapByte; i++) {
                if ((bitmap[i] & SearchMap[i]) != SearchMap[i]) {
                    free = false;
                    break;
                }
            }
            if (free) count++;
        }
        return count;
    }

    @Override
    @Trace
    public String buySeatByTidAndStationIndex(String tid, int boardingStation, int alightingStation, String type) {
        String seatId;

        if (tid == null || type == null) return null;
        if (boardingStation < 0 || boardingStation >= alightingStation) return null;

        List<TrainSeat> seats = trainSeatMapper.getTrainSeatsByTidType(tid, type);

        byte[] BuyMap = new byte[maxBitMapByte];
        for (int bit = boardingStation - 1; bit < alightingStation - 1; bit++) {
            int index = maxBitMapByte - 1 - bit / 8;
            int offset = bit % 8;
            BuyMap[index] |= (byte) (1 << offset);
        }
        boolean free;
        for (TrainSeat seat : seats) {
            free = true;
            byte[] bitmap = seat.getBitmap();
            for (int i = 0; i < maxBitMapByte; i++) {
                if ((bitmap[i] & BuyMap[i]) != BuyMap[i]) {
                    free = false;
                    break;
                }
            }
            if (free) {
                seatId = seat.getSeatId();
                seat.updateBitmapByBuyMap(BuyMap);
                trainSeatMapper.updateTrainSeat(seat);
                return seatId;
            }
        }
        return null;
    }

    @Override
    @Trace
    public boolean cancelSeatByTidAndStationIndex(String tid, String sid, int boardingStation, int alightingStation) {

        if (tid == null || sid == null) return false;
        if (boardingStation < 0 || boardingStation >= alightingStation) return false;

        TrainSeat seat = trainSeatMapper.getTrainSeatByTidSid(tid, sid);

        byte[] CancelMap = new byte[maxBitMapByte];
        for (int bit = boardingStation - 1; bit < alightingStation - 1; bit++) {
            int index = maxBitMapByte - 1 - bit / 8;
            int offset = bit % 8;
            CancelMap[index] |= (byte) (1 << offset);
        }
        seat.updateBitmapByCancelMap(CancelMap);
        trainSeatMapper.updateTrainSeat(seat);
        return true;
    }
}
