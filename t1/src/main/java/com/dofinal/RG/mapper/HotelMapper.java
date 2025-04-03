package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.Hotel;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HotelMapper extends BaseMapper<Hotel> {
        @Select("select h_id as id, h_name as name, l_id as locationId, price_rate as priceRate, h_tel as phoneNumber, detail_location as detailLocation, h_description as description from traveldog.hotels where l_id = #{lid}")
        @Trace
        List<Hotel> findHotelByLid(int lid);

        @Select("select * from traveldog.hotels")
        @Trace
        List<Hotel> findHotels();

        @Select("select * from traveldog.hotels where h_id = #{hid}")
        @Trace
        Hotel getHotelByHid(int hid);

        @Select("select price_rate from hotels where h_id = #{hid}")
        @Trace
        Double findHotelPriceRateByHid(int hid);

        @Insert("insert into traveldog.hotels(h_id, h_name, l_id, price_rate, h_tel, detail_location, h_description) values" +
                        "(#{hot.id}, #{hot.name}, #{hot.locationId}, #{hot.priceRate}, #{hot.phoneNumber},#{hot.detailLocation}, #{hot.description})")
        @Trace
        Integer insertHotel(@Param("hot") Hotel hot);

        @Update("update traveldog.hotels set h_name = #{hot.name}, l_id = #{hot.locationId}, detail_location = #{hot.detailLocation}, "
                        +
                        " h_tel = #{hot.phoneNumber}, price_rate = #{hot.priceRate}, h_description = #{hot.description}" + " where h_id = #{hot.id}")
        @Trace
        Integer updateHotel(@Param("hot") Hotel hot);

        @Delete("delete from traveldog.hotels where h_id = #{hid}")
        @Trace
        Integer deleteHotelByHid(int hid);
}
