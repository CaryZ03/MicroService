package com.dofinal.RG.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

/**
 * &#064;Classname SeatPriceMapper
 * &#064;Description  TODO
 * &#064;Date 2024/6/2 18:06
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface SeatPriceMapper {
    @Select("select s_price from seat_price where s_type = #{type}")
    @Trace
    double getPriceBySeatType(String type);
}
