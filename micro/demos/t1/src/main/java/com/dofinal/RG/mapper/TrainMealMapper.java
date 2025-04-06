package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.train.TrainMeal;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname TrainMealMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:59
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface TrainMealMapper extends BaseMapper<TrainMeal> {
    @Select("select * from train_meal where t_id = #{tid}")
    @Trace
    List<TrainMeal> getTrainMealsByTid(String tid);
    @Select("select * from train_meal where m_id = #{mid}")
    @Trace
    List<TrainMeal> getTrainMealsByMid(int mid);
    @Select("select * from train_meal where t_id = #{tid} and m_id = #{mid};")
    @Trace
    TrainMeal getTrainMealByTidAndMid(@Param("tid") String tid,@Param("mid") int mid);
    @Select("select m_count from train_meal where t_id = #{tid}")
    @Trace
    List<Integer> getTrainMealCountsByTid(String tid);
    @Select("select m_count from train_meal where t_id = #{tid} and m_id = #{mid};")
    @Trace
    Integer getTrainMealCountByTidAndMid(@Param("tid") String tid,@Param("mid") int mid);
    @Update("update train_meal set m_count = #{trainMeal.mealCount} where t_id = #{trainMeal.trainId} and m_id = #{trainMeal.mealId}")
    @Trace
    Integer updateMeal(@Param("trainMeal") TrainMeal trainMeal);
}
