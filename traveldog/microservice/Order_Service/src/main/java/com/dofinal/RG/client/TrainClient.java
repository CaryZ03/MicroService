package com.dofinal.RG.client;

import com.dofinal.RG.entity.train.Meal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Timestamp;

/**
 * Classname TrainClient
 * Description TODO
 * Date 2024/8/23 14:50
 * Created ZHW
 */
@FeignClient(value = "train-service", url="${backend.train.url}")
public interface TrainClient {

    @GetMapping("/train/getPriceRateByTid/{tid}")
    double findTrainPriceRateByTid(@PathVariable String tid);

    @GetMapping("/train/getSSByTidLid/{tid}/{lid}")
    int getStationSequenceByTidAndLid(@PathVariable String tid, @PathVariable int lid);

    @GetMapping("/train/getATByTidLid/{tid}/{lid}")
    Timestamp getTrainArrivalTimeByTidLid(@PathVariable String tid, @PathVariable int lid);

    @GetMapping("/meal/getCountByTidMid/{tid}/{mid}")
    int getTrainMealCountByTidAndMid(@PathVariable String tid, @PathVariable int mid);

    @GetMapping("/meal/getByMid/{mid}")
    Meal getMealByMid(@PathVariable int mid);

    @GetMapping("/meal/getPriceByMid/{mid}")
    double getMealPriceByMid(@PathVariable int mid);

    @PostMapping("/meal/buyByTidMid/{tid}/{mid}")
    boolean buyMeal(@PathVariable String tid, @PathVariable int mid);

    @PostMapping("meal/cancelByTidMid/{tid}/{mid}")
    boolean cancelMeal(@PathVariable String tid, @PathVariable int mid);

    @GetMapping("/seat/getCountByTidIndexType/{tid}/{bs}/{as}/{type}")
    int getSeatCountByTidAndStationIndex(@PathVariable String tid, @PathVariable int bs, @PathVariable int as, @PathVariable String type);

    @PostMapping("/seat/buyByTidIndexType/{tid}/{bs}/{as}/{type}")
    String buySeatByTidAndStationIndex(@PathVariable String tid, @PathVariable int bs, @PathVariable int as, @PathVariable String type);

    @PostMapping("/seat/cancelByTidSidIndex/{tid}/{sid}/{bs}/{as}")
    boolean cancelSeatByTidAndStationIndex(@PathVariable String tid, @PathVariable String sid, @PathVariable int bs, @PathVariable int as);

    @GetMapping("/seat/getPriceByType/{type}")
    double getPriceBySeatType(@PathVariable String type);
}
