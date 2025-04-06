package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.train.Train;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname TrainMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/14 19:31
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface TrainMapper extends BaseMapper<Train> {
    @Select("select * from trains")
    @Trace
    List<Train> findTrains();
    @Select("select * from trains where t_id = #{tid}")
    @Trace
    Train findTrainByTid(String tid);
    @Select("select price_rate from trains where t_id = #{tid}")
    @Trace
    Double findTrainPriceRateByTid(String tid);
    @Delete("delete from trains where t_id = #{tid}")
    @Trace
    int deleteTrainByTid(String tid);
}
