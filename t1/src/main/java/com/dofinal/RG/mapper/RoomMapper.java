package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.hotel.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname RoomMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:56
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface RoomMapper extends BaseMapper<Room> {
    @Select("select * from rooms ")
    @Trace
    List<Room> getRooms();
    @Select("select * from rooms where r_type = #{type}")
    @Trace
    Room getRoomByType(String type);
    @Select("select * from rooms where r_id = #{rid}")
    @Trace
    Room getRoomByRid(int rid);
    @Select("select r_price from rooms where r_id = #{rid}")
    @Trace
    Double getRoomPriceByRid(int rid);
    @Update("update rooms set r_type = #{room.type}, r_detail = #{room.detail}," +
            "r_name = #{room.name} where r_id = #{room.id}")
    @Trace
    Integer updateRoom(Room room);
}
