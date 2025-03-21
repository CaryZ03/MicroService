package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.train.Meal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname MealMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:56
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface MealMapper extends BaseMapper<Meal> {
    @Select("select * from traveldog.meals")
    @Trace
    List<Meal> getMeals();

    @Select("select m_id as id, m_name as name, m_type as type, m_price as price, m_detail as detail from traveldog.meals where m_id = #{mid}")
    @Trace
    Meal getMealByMid(int mid);
    @Select("select m_price from meals where m_id = #{mid}")
    @Trace
    Double getMealPriceByMid(int mid);
}
