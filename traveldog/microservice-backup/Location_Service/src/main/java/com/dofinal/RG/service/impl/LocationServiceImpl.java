package com.dofinal.RG.service.impl;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.train.Meal;
import com.dofinal.RG.mapper.LocationMapper;
import com.dofinal.RG.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: LZX
 * @Date: 2024/8/21 15:39
 */
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    LocationMapper locationMapper;
    @Override
    public Location getLocationByLid(int lid) {
        return locationMapper.getLocationByLid(lid);
    }

    @Override
    public List<Location> getLocations() {
        return locationMapper.getLocations();
    }

    @Override
    public List<Integer> getLids() {
        return locationMapper.getLids();
    }

    @Override
    public List<Location> getLocationByProvince(String province) {
        return locationMapper.getLocationByProvince(province);
    }

    @Override
    public List<Integer> getLidByProvince(String province) {
        return locationMapper.getLidByProvince(province);
    }

    @Override
    public Integer getLidByProvinceCity(String province, String city) {
        return locationMapper.getLidByProvinceCity(province,city);
    }
}
