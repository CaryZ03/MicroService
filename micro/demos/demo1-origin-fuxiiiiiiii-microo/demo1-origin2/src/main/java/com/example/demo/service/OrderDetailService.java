package com.example.demo.service;

import com.example.demo.model.OrderDetail;
import com.example.demo.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.RequestWrapper;

@Service
public class OrderDetailService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Trace
    public OrderDetail addOrderDetail(OrderDetail orderDetail) {
        List<Object> params = new ArrayList();
        params.add(orderDetail);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.OrderDetailService.addOrderDetail(com.example.demo.model.OrderDetail)", params);
        return restTemplate.postForObject("http://127.0.0.1:18503/mapper", wrapper, OrderDetail.class);
    }
}
