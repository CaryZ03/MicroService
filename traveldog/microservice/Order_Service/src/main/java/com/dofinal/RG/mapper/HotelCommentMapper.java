package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.HotelComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HotelCommentMapper extends BaseMapper<HotelComment> {
    @Select("select * from hotel_comment where h_id = #{hid}")
    List<HotelComment> getHotelCommentByHid(int hid);
    @Select("select * from hotel_comment where o_id = #{oid}")
    HotelComment getHotelCommentByOid(int oid);
    @Insert("insert into hotel_comment(o_id, h_id, rating, content) values " +
            "(#{hr.oid},#{hr.hid},#{hr.rate}, #{hr.content})")
    Integer insertHotelRate(@Param("hr") HotelComment hr);
    @Delete("delete from hotel_comment where o_id = #{oid}")
    Integer deleteHotelRateByOid(int oid);
}
