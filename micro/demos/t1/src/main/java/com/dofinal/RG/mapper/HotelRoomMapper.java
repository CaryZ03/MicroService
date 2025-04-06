package com.dofinal.RG.mapper;

import com.dofinal.RG.entity.hotel.Hotel;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.HotelRoom;
import org.apache.ibatis.annotations.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname HotelRoomMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:57
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface HotelRoomMapper extends BaseMapper<HotelRoom> {
    @Update("update hotel_room set bitmap = #{hr.bitmap} where h_id = #{hr.hotelId} and r_id = #{hr.roomId} and r_number = #{hr.roomNumber}")
    @Trace
    Integer updateHotelRoom(@Param("hr") HotelRoom hr);

    @Select("select * from hotel_room")
    @Trace
    List<HotelRoom> getHotelRoom();

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from traveldog.hotel_room where h_id = #{hid}")
    @Trace
    List<HotelRoom> getHotelRoomByHid(int hid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from traveldog.hotel_room where r_id = #{rid}")
    @Trace
    List<HotelRoom> getHotelRoomByRid(int rid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from traveldog.hotel_room where h_id = #{hid} and r_id = #{rid}")
    @Trace
    List<HotelRoom> getHotelRoomByHidRid(@Param("hid") int hid, @Param("rid") int rid);

    @Select("select r_id from traveldog.hotel_room where h_id = #{hid}")
    @Trace
    List<Integer> getRidByHid(int hid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from traveldog.hotel_room where h_id = #{hid} and r_id = #{rid} and r_number = #{r_number}")
    @Trace
    HotelRoom getHotelRoomByHidRidRNumber(@Param("hid") int hid, @Param("rid") int rid, @Param("r_number") String r_number);

    @Delete("delete from traveldog.hotel_room where h_id = #{hid} and r_id = #{rid} and r_number = #{r_number}")
    @Trace
    public int deleteHotelRoomByHidRidRnum(@Param("hid") int hid, @Param("rid") int rid,  @Param("r_number") String r_number);
}
