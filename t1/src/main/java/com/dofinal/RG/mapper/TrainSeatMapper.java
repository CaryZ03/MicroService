package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.train.TrainSeat;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname TrainSeatMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 22:01
 * &#064;Created MuJue
 */

@Mapper
@Repository
public interface TrainSeatMapper extends BaseMapper<TrainSeat> {
    @Select("select * from train_seat where t_id = #{tid} and s_type = #{type}")
    @Trace
    List<TrainSeat> getTrainSeatsByTidType(@Param("tid")String tid, @Param("type")String type);
    @Select("select * from train_seat where t_id = #{tid} and s_id = #{sid}")
    @Trace
    TrainSeat getTrainSeatByTidSid(@Param("tid") String tid, @Param("sid")String sid);
    @Select("select * from train_seat where t_id = #{tid}")
    @Trace
    List<TrainSeat> getTrainSeatByTid(String tid);
    @Delete("delete from train_seat where t_id = #{tid} and s_id = #{sid}")
    @Trace
    Integer deleteTrainSeatByTidAndSid(@Param("tid") String tid, @Param("sid")String sid);

    @Delete("delete from train_seat where t_id = #{tid}")
    @Trace
    Integer deleteTrainSeatByTid(String tid);

    @Update("update train_seat set s_type = #{seat.type}, bitmap = #{seat.bitmap} where t_id = #{seat.trainId} and s_id = #{seat.seatId}")
    @Trace
    Integer updateTrainSeat(@Param("seat") TrainSeat trainSeat);
}
