package com.dofinal.RG.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.entity.hotel.Hotel;

import java.util.List;

/**
 * Classname HotelService
 * Description TODO
 * Date 2024/5/15 10:39
 * Created ZHW
 */
public interface HotelService extends IService<Hotel> {
    List<Hotel> findHotelByLocation(Location location);
    Hotel findHotelById(int hid);
    int deleteHotelByHid(int hid);
}
