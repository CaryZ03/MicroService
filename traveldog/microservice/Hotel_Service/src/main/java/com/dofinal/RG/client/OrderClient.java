package com.dofinal.RG.client;

import com.dofinal.RG.entity.hotel.HotelComment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * &#064;Classname OrderClient
 * &#064;Description  TODO
 * &#064;Date 2024/8/29 9:24
 * &#064;Created MuJue
 */
@FeignClient(name = "order-service", url = "${backend.order.url}")
public interface OrderClient {
    @GetMapping("/order/getHotelCommentByHid/{hid}")
    List<HotelComment> getHotelCommentByHid(@PathVariable int hid);
}
