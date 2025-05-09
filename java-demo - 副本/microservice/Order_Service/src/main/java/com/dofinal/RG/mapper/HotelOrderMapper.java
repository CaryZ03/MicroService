package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.order.HotelCustomerOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname HotelOrderMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 19:45
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface HotelOrderMapper extends BaseMapper<HotelCustomerOrder> {
    @Select("select * from hotel_orders where o_id = #{oid}")
    List<HotelCustomerOrder> findHotelOrderByOid(int oid);
    @Select("select * from hotel_orders where c_id = #{cid}")
    List<HotelCustomerOrder> findHotelOrderByCid(int cid);
    @Select("select * from hotel_orders where h_id = #{hid}")
    List<HotelCustomerOrder> findHotelOrderByHid(int hid);
    @Select("select * from hotel_orders where o_id = #{oid} and c_id = #{cid}")
    HotelCustomerOrder findHotelOrderByOidCid(@Param("oid")int oid, @Param("cid")int cid);
    @Select("select c_id from hotel_orders where h_id = #{hid}")
    List<Integer> getCidByHid(int hid);
    @Delete("delete from hotel_orders where c_id = #{cid} and o_id = #{oid}")
    Integer deleteHotelOrderByOidCid(@Param("oid")int oid, @Param("cid")int cid);
    @Delete("delete from hotel_orders where o_id = #{oid}")
    Integer deleteHotelOderByOid(int oid);
}
