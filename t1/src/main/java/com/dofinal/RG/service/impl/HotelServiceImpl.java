package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.hotel.HotelRoom;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.mapper.HotelCommentMapper;
import com.dofinal.RG.mapper.HotelMapper;
import com.dofinal.RG.mapper.LocationMapper;
import com.dofinal.RG.service.HotelService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classname HotelServiceImpl
 * Description TODO
 * Date 2024/5/15 10:41
 * Created ZHW
 */
@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private HotelCommentMapper hotelCommentMapper;

    @Override

    @Trace
    public List<Hotel> findHotelByLocation(Location location) {
        List<Hotel> hotelList = new ArrayList<>();

        if (location == null) {
            List<Integer> lids = locationMapper.getLids();
            for (Integer lid : lids) {
                List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                hotelList.addAll(tmps);
            }
        } else {
            String province = location.getProvince();
            String city = location.getCity();
            if (province == null && city == null) {
                List<Integer> lids = locationMapper.getLids();
                for (Integer lid : lids) {
                    List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                    hotelList.addAll(tmps);
                }
            } else if (province != null && city == null) {
                List<Integer> lids = locationMapper.getLidByProvince(province);
                for (Integer lid : lids) {
                    List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                    hotelList.addAll(tmps);
                }
            } else if (province != null && city != null) {
                Integer lid = locationMapper.getLidByProvinceCity(province, city);
                hotelList = hotelMapper.findHotelByLid(lid);
            }
        }
        for (Hotel hotel : hotelList) {
            List<HotelComment> hotelComments = hotelCommentMapper.getHotelCommentByHid(hotel.getId());
            double rate = 0.0;
            List<String> comments = new ArrayList<>();
            for (HotelComment hotelComment1 : hotelComments) {
                rate += hotelComment1.getRate();
                comments.add(hotelComment1.getComment());
            }
            hotel.setComments(comments);
            hotel.setRate(rate);
        }
        return hotelList;
    }

    @Override
    @Trace
    public Hotel findHotelById(int hid) {
        return hotelMapper.getHotelByHid(hid);
    }

    @Override
    @Trace
    public int deleteHotelByHid(int hid) {
        return 0;
    }
}
