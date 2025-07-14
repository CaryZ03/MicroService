package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.location.Location;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: LZX
 * @Date: 2024/8/21 15:33
 */
public interface LocationService {
    Location getLocationByLid(int lid);

    List<Location> getLocations();

    List<Integer> getLids();

    List<Location> getLocationByProvince(String province);

    List<Integer> getLidByProvince(String province);

    Integer getLidByProvinceCity(String  province, String city);
}
