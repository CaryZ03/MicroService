package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.Rate;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface HotelRateMapper extends BaseMapper<Rate> {

    @Select("SELECT avg(rate) FROM hotel_rate where h_id = #{hid} group by h_id")
    Double getAverageRateByHid(int hid);

    @Insert("insert into hotel_rate(o_id, h_id, rate) values " + "(#{hr.oid},#{hr.hid},#{hr.rate})")
    Integer insertHotelRate(@Param("hr") Rate hr);

    @Delete("delete from hotel_rate where o_id = #{oid}")
    Integer deleteHotelRateByOid(int oid);
}
