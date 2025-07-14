package com.dofinal.RG.client;

import com.dofinal.RG.entity.location.Location;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Classname LocationClient
 * Description TODO
 * Date 2024/8/23 9:39
 * Created ZHW
 */

@FeignClient(value = "location-service",url="${backend.location.url}")
public interface LocationClient {
    @GetMapping("/location/getLidByProvinceCity/{province}/{city}")
    int getLidByProvinceCity(@PathVariable String province,@PathVariable String city);

    @GetMapping("/location/getByLid/{lid}")
    Location getLocationByLid(@PathVariable int lid);
}
