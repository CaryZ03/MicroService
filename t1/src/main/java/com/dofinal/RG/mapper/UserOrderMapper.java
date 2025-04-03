package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.order.UserOrder;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * &#064;Classname UserOrderMapper
 * &#064;Description TODO
 * &#064;Date 2024/5/16 22:12
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface UserOrderMapper extends BaseMapper<UserOrder> {
        @Select("select o_id as orderId, u_id as userId, price, add_time as addTime, uo_status as status, " +
                        "service_type as type, is_comment as isComment from orders where u_id = #{uid}")
        @Trace
        List<UserOrder> getUserOrderByUid(String uid);

        @Select("select o_id as orderId, u_id as userId, price, add_time as addTime, uo_status as status, " +
                        "service_type as type from orders where o_id = #{oid}")
        @Trace
        UserOrder getUserOrderByOid(int oid);

        @Select("select o_id as orderId, u_id as userId, price, add_time as addTime, uo_status as status, + " +
                        "service_type as type from orders where u_id = #{uid} and add_time = #{addTime}")
        @Trace
        UserOrder getUserOrderByUidAddTime(@Param("uid") String uid, @Param("addTime") String addTime);

        @Select("select o_id as orderId, u_id as userId, price, add_time as addTime, uo_status as status, " +
                        " service_type as type from orders")
        @Trace
        List<UserOrder> findUserOrders();

        @Select("select o_id from orders where u_id = #{uid}")
        @Trace
        List<Integer> getOidByUid(String uid);

        @Select("select o_id from orders where u_id = #{u_id} and add_time = #{time}")
        @Trace
        Integer getUserOrderOidByUidTime(@Param("u_id") String u_id, @Param("time") String time);

        @Update("update orders set price = #{price} where o_id = #{oid}")
        @Trace
        Integer updateUserOrderPriceByOid(@Param("oid") int oid, @Param("price") double price);

        @Update("update orders set uo_status = #{status} where o_id = #{oid}")
        @Trace
        Integer updateUserOrderStatusByOid(@Param("oid") Integer oid, @Param("status") String status);

        @Update("update orders set is_comment = #{status} where o_id = #{oid}")
        @Trace
        Integer updateUserOrderCommentStatus(@Param("oid") int oid, @Param("status") boolean status);

        @Update("update orders set service_type = #{type} where o_id = #{oid}")
        @Trace
        Integer updateUserOrderTypeByOid(@Param("oid") Integer oid, @Param("type") String type);

        @Delete("delete from orders where o_id = #{oid}")
        @Trace
        Integer deleteUserOrderByOid(int oid);

        @Delete("delete from orders where u_id = #{uid}")
        @Trace
        Integer deleteUserOrderByUid(String uid);

        @Insert("insert into orders(u_id, price, add_time, uo_status, service_type) values" +
                        "(#{uo.userId},#{uo.price},#{uo.addTime},#{uo.status},#{uo.type})")
        @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "o_id")
        @Trace
        Integer insertUserOrder(@Param("uo") UserOrder uo);

}
