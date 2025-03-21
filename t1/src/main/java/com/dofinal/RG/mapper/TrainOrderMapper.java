package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.order.TrainCustomerOrder;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname TrainOrderMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 19:45
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface TrainOrderMapper extends BaseMapper<TrainCustomerOrder> {
    @Select("select * from traveldog.trains_orders where o_id = #{oid}")
    @Trace
    List<TrainCustomerOrder> findTrainOrderByOid(int oid);
    @Select("select * from trains_orders where t_id = #{tid}")
    @Trace
    List<TrainCustomerOrder> findTrainOrderByTid(String tid);
    @Select("select * from traveldog.trains_orders where c_id = #{cid}")
    @Trace
    List<TrainCustomerOrder> findTrainOrderByCid(int cid);
    @Select("select * from traveldog.trains_orders where o_id = #{oid} and c_id = #{cid}")
    @Trace
    TrainCustomerOrder findTrainOrderByOidCid(@Param("oid")int oid, @Param("cid")int cid);
    @Select("select o_id from trains_orders where t_id = #{tid}")
    @Trace
    List<Integer> getOrderIdsByTid(String tid);
    @Select("select c_id from trains_orders where t_id = #{tid}")
    @Trace
    List<Integer> getCidByTid(String tid);
    @Delete("delete from trains_orders where o_id = #{oid} and c_id = #{cid}")
    @Trace
    Integer deleteTrainOrderByOidCid(@Param("oid")int oid, @Param("cid")int cid);
    @Delete("delete from trains_orders where o_id = #{oid}")
    @Trace
    Integer deleteTrainOrderByOid(int oid);
}
