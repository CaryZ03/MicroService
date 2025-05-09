package com.dofinal.RG.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Classname LocationClient
 * Description TODO
 * Date 2024/8/23 9:39
 * Created ZHW
 */

@FeignClient(value = "location-service", url = "${backend.location.url}")
public interface LocationClient {

    @GetMapping("/location/getLidByProvinceCity/{province}/{city}")
    int getLidByProvinceCity(@PathVariable String province,@PathVariable String city);

    @GetMapping("/location/getLidByProvince/{province}")
    List<Integer> getLidByProvince(@PathVariable String province);

    @GetMapping("/location/getLids")
    List<Integer> getLids();
}
