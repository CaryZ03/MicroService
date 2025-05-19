package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.Train;

import java.sql.Timestamp;
import java.util.List;

/**
 * &#064;Classname TrainService
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:26
 * &#064;Created MuJue
 */
public interface TrainService extends IService<Train> {
    List<Train> findTrains();

    Train findTrainByTid(String tid);
    List<String> getTidByUid(String uid);

    List<Train> findTrainByLocationAndTime(Timestamp curTime, Location sl, Location el);
    int deleteTrainByTid(String tid);
}
