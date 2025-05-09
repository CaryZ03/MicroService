package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.user.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname UserMapper
 * &#064;Description TODO
 * &#064;Date 2024/5/4 21:11
 * &#064;Created MuJue
 */
@Mapper
@Repository // 将UserMapper给spring容器管理
public interface UserMapper extends BaseMapper<User> {
        @Select("select u_id as id, u_name as name, u_password as password, u_tel as phoneNumber, u_money as money, u_status as status from users")
        List<User> findUsers();

        @Select("select u_id as id, u_name as name, u_password as password, u_tel as phoneNumber, u_money as money, u_status as status from users where u_id = #{id}")
        User findUserByUid(String uid);

        @Delete("delete from users where u_id = #{uid}")
        Integer deleteUserByUid(String uid);

        @Update("update users set u_name = #{user.name}, u_password = #{user.password}, u_tel = " +
                        "#{user.phoneNumber}, u_money = #{user.money}, u_status = #{user.status} where u_id = #{user.id}")
        Integer updateUser(@Param("user") User user);

        @Insert("insert into users(u_id, u_name, u_password, u_tel, u_money, u_status) " +
                        " values (#{user.id}, #{user.name}, #{user.password}, #{user.phoneNumber}, #{user.money}, #{user.status})")
        Integer insertUser(@Param("user") User user);
}
