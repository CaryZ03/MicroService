package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.client.LocationClient;
import com.dofinal.RG.client.OrderClient;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.location.TrainLocationDemo;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import com.dofinal.RG.entity.order.UserOrder;
import com.dofinal.RG.entity.train.Train;
import com.dofinal.RG.mapper.*;
import com.dofinal.RG.service.TrainService;
import com.dofinal.RG.util.NoticeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Classname TrainServiceImpl
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:31
 * &#064;Created MuJue
 */
@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {
    @Autowired
    TrainMapper trainMapper;
    @Autowired
    TrainArrivalMapper trainArrivalMapper;
    @Autowired
    LocationClient locationClient;
    @Autowired
    OrderClient orderClient;

    @Override
    public List<Train> findTrains() {
        return trainMapper.findTrains();
    }

    @Override
    public Train findTrainByTid(String tid) {
        if (tid != null) {
            return trainMapper.findTrainByTid(tid);
        }
        return null;
    }

    @Override
    public List<String> getTidByUid(String uid) {
        if(uid == null) return null;
        List<String> tids = new ArrayList<>();
        List<UserOrder> userOrders = orderClient.getUserOrderByUid(uid);

        List<Integer> oids = new ArrayList<>();
        for(UserOrder userOrder : userOrders){
            if(userOrder.getType().equals("trainOrder") && userOrder.getStatus().equals("paid")){
                oids.add(userOrder.getOrderId());
            }
        }

        for(Integer oid : oids){
            List<TrainCustomerOrder> subTcos = orderClient.findTrainOrderByOid(oid);
            tids.add(subTcos.get(0).getTrainId());
        }
        return tids;
    }

    @Override
    public List<Train> findTrainByLocationAndTime(Timestamp curTime, Location startLocation, Location endLocation) {
        if (curTime == null || startLocation == null || endLocation == null) return null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTime = df.format(curTime);

        Integer sl = locationClient.getLidByProvinceCity(startLocation.getProvince(), startLocation.getCity());
        Integer el = locationClient.getLidByProvinceCity(endLocation.getProvince(), endLocation.getCity());
        List<String> tids = trainArrivalMapper.getTidByLidAndTimes(strTime, sl, el);

        List<Train> trains = new ArrayList<>();
        for (String tid : tids) {
            int stationCount = trainArrivalMapper.getStationCountByTid(tid);
            List<TrainLocationDemo> locations = new ArrayList<>();
            for(int i = 1;i <= stationCount;++i){
                int lid = trainArrivalMapper.getLidByTidAndStationIndex(tid, i);
                Location location = locationClient.getLocationByLid(lid);
                Timestamp arriveTime = trainArrivalMapper.getTrainArrivalTimeByTidLid(tid, lid);
                String arrTime = df.format(arriveTime);
                TrainLocationDemo locationDemo = new TrainLocationDemo(location.getProvince(),location.getCity(), arrTime);
                locations.add(locationDemo);
            }
            Train train = trainMapper.findTrainByTid(tid);
            train.setLocations(locations);
            trains.add(train);
        }
        return trains;
    }

    @Override
    public int deleteTrainByTid(String tid) {
        List<Integer> orderIds = orderClient.getOrderIdsByTid(tid);
        if (orderIds == null) return -1;
        for (Integer orderId : orderIds) {
            UserOrder userOrder = orderClient.getUserOrderByOid(orderId);
            userOrder.setStatus("取消");
            String uid = userOrder.getUserId();

            //NoticeUtil.getInstance().addNotice(uid, "您的订单已被取消");

            new NoticeUtil().addNotice(uid, "id为"+tid+"的火车被取消!");

        }
        return trainMapper.deleteTrainByTid(tid);
    }

    @Override
    public double findTrainPriceRateByTid(String tid){
        return trainMapper.findTrainPriceRateByTid(tid);
    }

    @Override
    public int getStationSequenceByTidAndLid(String tid, int lid){
        return trainArrivalMapper.getStationSequenceByTidAndLid(tid, lid);
    }

    @Override
    public Timestamp getTrainArrivalTimeByTidLid(String tid, int lid){
        return trainArrivalMapper.getTrainArrivalTimeByTidLid(tid, lid);
    }
}
