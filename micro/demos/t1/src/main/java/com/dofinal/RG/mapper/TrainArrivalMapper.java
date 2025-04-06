package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.train.TrainArrival;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * &#064;Classname TrainArrivalMapper
 * &#064;Description TODO
 * &#064;Date 2024/5/18 21:57
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface TrainArrivalMapper extends BaseMapper<TrainArrival> {
    @Select("select * from train_arrivals")
    @Trace
    List<TrainArrival> getTrainArrivals();

    @Select("select * from train_arrivals where t_id = #{tid}")
    @Trace
    List<TrainArrival> getTrainArrivalByTid(String tid);

    @Select("select * from train_arrivals where l_id = #{lid}")
    @Trace
    List<TrainArrival> getTrainArrivalByLid(int lid);

    @Select("select t_id from train_arrivals as ta1 where ta1.l_id = #{sl} and ta1.arrivalTime > #{curTime} and date(date_add(#{curTime}, interval 1 day)) > date(ta1.arrivalTime)  " +
            "and exists(select * from train_arrivals as ta2 where ta2.l_id = #{el} and ta2.t_id = ta1.t_id and ta1.station_sequence < ta2.station_sequence)")
    @Trace
    List<String> getTidByLidAndTimes(@Param(value = "curTime") String curTime, @Param(value = "sl") Integer sl,
            @Param(value = "el") Integer el);

    @Select("select count(l_id) from train_arrivals where t_id = #{tid}")
    @Trace
    Integer getStationCountByTid(String tid);

    @Select("select station_sequence from train_arrivals where t_id = #{tid} and l_id = #{lid}")
    @Trace
    Integer getStationSequenceByTidAndLid(@Param("tid") String tid, @Param("lid") int lid);

    @Select("select l_id from train_arrivals where t_id = #{tid} and station_sequence = #{sid}")
    @Trace
    Integer getLidByTidAndStationIndex(@Param("tid") String tid, @Param("sid") int sid);

    @Select("select arrivalTime from train_arrivals where t_id = #{tid} and l_id = #{lid}")
    @Trace
    Timestamp getTrainArrivalTimeByTidLid(@Param("tid") String tid, @Param("lid") int lid);
}
