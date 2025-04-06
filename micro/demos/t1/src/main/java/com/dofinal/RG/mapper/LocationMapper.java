package com.dofinal.RG.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dofinal.RG.entity.location.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * &#064;Classname LocationMapper
 * &#064;Description  TODO
 * &#064;Date 2024/5/18 21:57
 * &#064;Created MuJue
 */
@Mapper
@Repository
public interface LocationMapper extends BaseMapper<LocationMapper> {
    @Select("select l_id, province, city from locations where l_id = #{lid}")
    @Trace
    Location getLocationByLid(int lid);
    @Select("select l_id, province, city from locations")
    @Trace
    List<Location> getLocations();
    @Select("select l_id from locations")
    @Trace
    List<Integer> getLids();
    @Select("select l_id, province, city from locations where province like concat('%',#{province},'%')")//模糊查询
    @Trace
    List<Location> getLocationByProvince(String province);
    @Select("select distinct l_id from locations where province like concat('%',#{province},'%')")//模糊查询
    @Trace
    List<Integer> getLidByProvince(String province);
    @Select("select l_id from locations where province like concat('%',#{province},'%') and city like concat('%',#{city},'%');")
    @Trace
    Integer getLidByProvinceCity(@Param("province")String  province, @Param("city")String city);
}
