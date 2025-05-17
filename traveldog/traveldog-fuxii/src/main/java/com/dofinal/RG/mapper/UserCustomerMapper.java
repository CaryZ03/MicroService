package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.user.UserCustomer;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * &#064;Classname UserCustomerMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:46
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface UserCustomerMapper extends BaseMapper<UserCustomer> {

    @Insert("insert into traveldog.users_customers(u_id, c_id) " + "values(#{us.userId}, #{us.customerId})")
    Integer insertUserCustomer(@Param("us") UserCustomer us);

    @Select("select * from users_customers where c_id = #{cid}")
    List<UserCustomer> getUserCustomerByCid(int cid);

    @Select("select * from users_customers where u_id = #{uid}")
    List<UserCustomer> getUserCustomerByUid(String uid);

    @Select("select * from users_customers where u_id = #{uid} and c_id = #{cid}")
    UserCustomer getUserCustomerByUidCid(@Param("uid") String uid, @Param("cid") Integer cid);

    @Select("select c_id from users_customers where u_id = #{uid}")
    List<Integer> getCidByUid(String uid);

    @Delete("delete from users_customers where u_id = #{uid}")
    Integer deleteUserCustomerByUid(String uid);

    @Delete("delete from users_customers where c_id = #{cid}")
    Integer deleteUserCustomerByCid(int cid);

    @Delete("delete from users_customers where u_id = #{uid} and c_id = #{cid}")
    Integer deleteUserCustomerByUidCid(@Param("uid") String uid, @Param("cid") Integer cid);
}
