package com.dofinal.RG.controller;

import com.dofinal.RG.entity.location.Location;
import com.dofinal.RG.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/location/getLidByProvinceCity/{province}/{city}")
    int getLidByProvinceCity(@PathVariable String province, @PathVariable String city){
        return locationService.getLidByProvinceCity(province, city);
    }

    @GetMapping("/location/getLidByProvince/{province}")
    List<Integer> getLidByProvince(@PathVariable String province){
        return locationService.getLidByProvince(province);
    }

    @GetMapping("/location/getLids")
    List<Integer> getLids(){
        return  locationService.getLids();
    }

    @GetMapping("/location/getByLid/{lid}")
    Location getLocationByLid(@PathVariable int lid){
        return locationService.getLocationByLid(lid);
    }


}
