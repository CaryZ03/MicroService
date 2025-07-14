package com.dofinal.RG.client;


import com.dofinal.RG.entity.hotel.Hotel;
import com.dofinal.RG.entity.hotel.HotelRoom;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
@FeignClient(value = "hotel-service", url = "${backend.hotel.url}")
public interface HotelClient {
    @GetMapping("/hotel/getByHidMicro/{hid}")
    Hotel getHotelByHidMicro(@PathVariable int hid);

    @GetMapping("/hotel/getPriceRateByHid/{hid}")
    Double getHotelPriceRateByHid(@PathVariable int hid);

    @PostMapping("/hotel/update")
    Integer updateHotelRoom(@RequestBody  HotelRoom hr);

    @GetMapping("/room/getPriceByRid/{rid}")
    Double getRoomPriceByRid(@PathVariable int rid);

    @GetMapping("/hotel/getRoomByHidRidRNumber/{hid}/{rid}/{rNumber}")
     HotelRoom getHotelRoomByHidRidRNumber(@PathVariable int hid, @PathVariable  int rid, @PathVariable String rNumber);

    @GetMapping("/hotel/getRoomByHidRidTime/{hid}/{rid}/{curTime}/{startTime}/{endTime}/")
    List<HotelRoom> getHotelRoomByHidRidTime(@PathVariable int hid, @PathVariable  int rid, @PathVariable Timestamp curTime,
                                             @PathVariable Timestamp startTime, @PathVariable Timestamp endTime);
    @PostMapping("/orderRoom/{curTime}/{startTime}/{endTime}")
     byte[] orderRoom(@RequestParam Timestamp curTime, @RequestParam Timestamp startTime,
            @RequestParam Timestamp endTime, @RequestBody byte[] curBinary) ;
    @PostMapping("/cancelRoom/{curTime}/{startTime}/{endTime}")
     byte[] cancelRoom(@RequestParam Timestamp curTime, @RequestParam Timestamp startTime,
                             @RequestParam Timestamp endTime, @RequestBody byte[] curBinary) ;

}
