package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.user.Customer;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname CustomerMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/16 19:48
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface CustomerMapper extends BaseMapper<Customer> {
    @Select("select * from traveldog.customers")
    @Trace
    List<Customer> findCustomers();
    @Select("select * from traveldog.customers where c_id = #{cid}")
    @Trace
    Customer findCustomerByCid(int cid);
    @Select("select * from traveldog.customers where id_card = #{idCard}")
    @Trace
    Customer findCustomerByIdCard(String idCard);
    @Insert("insert into traveldog.customers(c_name, id_card, c_tel, c_age) values" +
            "(#{cus.name},#{cus.idCard},#{cus.phoneNumber},#{cus.age})")
    @Trace
    Integer insertCustomer(@Param("cus") Customer cus);
    @Update("update traveldog.customers set c_name = #{cus.name}, c_tel = #{cus.phoneNumber}, " +
            "c_age = #{cus.age}, c_name = #{cus.name}" + " where c_id = #{cus.id}")
    @Trace
    Integer updateCustomer(@Param("cus") Customer cus);
    @Delete("delete from traveldog.customers where c_id = #{cid}")
    @Trace
    Integer deleteCustomerByCid(int cid);
}
