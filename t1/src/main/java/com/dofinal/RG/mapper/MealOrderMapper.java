package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.order.MealCustomerOrder;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname MealOrderMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:58
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface MealOrderMapper extends BaseMapper<MealCustomerOrder> {
    @Select("select o_id as orderId, c_id as customerId, m_id as mealId, t_id as trainId, add_time as addTime, mo_price as price from meal_orders where o_id = #{oid}")
    @Trace
    List<MealCustomerOrder> getMealOrderByOid(int oid);
    @Select("select * from meal_orders where t_id = #{tid} and c_id = #{cid}")
    @Trace
    List<MealCustomerOrder> getMealOrderByTidCid(@Param("tid")String tid, @Param("cid")int cid);
    @Select("select o_id as orderId, c_id as customerId, m_id as mealId, t_id as trainId, add_time as addTime, mo_price as price from meal_orders where t_id = #{tid}")
    @Trace
    List<MealCustomerOrder> getMealOrderByTid(String tid);
    @Delete("delete from meal_orders where m_id = #{mid}")
    @Trace
    Integer deleteMealOrderByMid(int mid);
    @Delete("delete from meal_orders where c_id = #{cid}")
    @Trace
    Integer deleteMealOrderByCid(int cid);
    @Delete("delete from meal_orders where o_id = #{oid}")
    @Trace
    Integer deleteMealOrderByOid(int oid);
    @Delete("delete from meal_orders where o_id = #{oid} and c_id = #{cid}")
    @Trace
    Integer deleteMealOrderByOidCid(@Param("oid") int oid,@Param("cid") int cid);
}
