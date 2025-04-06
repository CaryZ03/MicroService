package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.user.UserNotice;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname UserNoticeMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/20 21:08
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface UserNoticeMapper extends BaseMapper<UserNotice> {
    @Select("select * from user_notice where u_id = #{uid}")
    @Trace
    List<UserNotice> getUserNoticeByUid(String uid);
    @Insert("insert into user_notice(u_id, n_time, n_content, n_status) values " +
            "(#{us.userId},#{us.addTime},#{us.noticeContent},#{us.noticeStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "noticeId", keyColumn = "n_id")
    @Trace
    Integer insertUserNotice(@Param("us")UserNotice us);
    @Update("update user_notice set n_status = '已读' where n_id = #{nid}")
    @Trace
    Integer updateStatus(int nid);
    @Delete("delete from user_notice where n_id = #{nid}")
    @Trace
    Integer deleteNotice(int nid);
}
