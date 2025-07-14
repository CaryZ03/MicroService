package com.dofinal.RG.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dofinal.RG.client.LocationClient;
import com.dofinal.RG.client.OrderClient;
import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.mapper.HotelMapper;
import com.dofinal.RG.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private LocationClient locationClient;

    @Override
    public List<Hotel> findHotelByLocation(Location location) {
        List<Hotel> hotelList = new ArrayList<>();

        if (location == null) {
            List<Integer> lids = locationClient.getLids();
            for (Integer lid : lids) {
                List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                hotelList.addAll(tmps);
            }
        } else {
            String province = location.getProvince();
            String city = location.getCity();
            if (province == null && city == null) {
                List<Integer> lids = locationClient.getLids();
                for (Integer lid : lids) {
                    List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                    hotelList.addAll(tmps);
                }
            } else if (province != null && city == null) {
                List<Integer> lids = locationClient.getLidByProvince(province);
                for (Integer lid : lids) {
                    List<Hotel> tmps = hotelMapper.findHotelByLid(lid);
                    hotelList.addAll(tmps);
                }
            } else if (province != null && city != null) {
                Integer lid = locationClient.getLidByProvinceCity(province, city);
                hotelList = hotelMapper.findHotelByLid(lid);
            }
        }
        for (Hotel hotel : hotelList) {
            List<HotelComment> hotelComments = orderClient.getHotelCommentByHid(hotel.getId());
            double rate = 0.0;
            List<String> comments = new ArrayList<>();
            for (HotelComment hotelComment1 : hotelComments) {
                rate += hotelComment1.getRate();
                comments.add(hotelComment1.getContent());
            }
            if(!hotelComments.isEmpty()){
                rate /= hotelComments.size();
            }
            hotel.setComments(comments);
            hotel.setRate(rate);
        }
        return hotelList;
    }

    @Override
    public Hotel findHotelById(int hid) {
        return hotelMapper.getHotelByHid(hid);
    }

    @Override
    public int deleteHotelByHid(int hid) {
        return 0;
    }

    @Override
    public Hotel getHotelByHid(int hid){
        return hotelMapper.getHotelByHid(hid);
    }
    @Override
    public Double findHotelPriceRateByHid(int hid){
        return hotelMapper.findHotelPriceRateByHid(hid);
    }

}
