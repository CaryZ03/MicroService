package com.dofinal.RG.mapper;

import com.dofinal.RG.entity.hotel.Hotel;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.HotelRoom;
import org.apache.ibatis.annotations.*;
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
    Integer updateHotelRoom(@Param("hr") HotelRoom hr);

    @Select("select * from hotel_room")
    List<HotelRoom> getHotelRoom();

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from hotel_room where h_id = #{hid}")
    List<HotelRoom> getHotelRoomByHid(int hid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from hotel_room where r_id = #{rid}")
    List<HotelRoom> getHotelRoomByRid(int rid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from hotel_room where h_id = #{hid} and r_id = #{rid}")
    List<HotelRoom> getHotelRoomByHidRid(@Param("hid") int hid, @Param("rid") int rid);

    @Select("select r_id from hotel_room where h_id = #{hid}")
    List<Integer> getRidByHid(int hid);

    @Select("select h_id as hotelId, r_id as roomId, r_number as roomNumber, bitmap from hotel_room where h_id = #{hid} and r_id = #{rid} and r_number = #{r_number}")
    HotelRoom getHotelRoomByHidRidRNumber(@Param("hid") int hid, @Param("rid") int rid, @Param("r_number") String r_number);

    @Delete("delete from hotel_room where h_id = #{hid} and r_id = #{rid} and r_number = #{r_number}")
    public int deleteHotelRoomByHidRidRnum(@Param("hid") int hid, @Param("rid") int rid,  @Param("r_number") String r_number);
}
