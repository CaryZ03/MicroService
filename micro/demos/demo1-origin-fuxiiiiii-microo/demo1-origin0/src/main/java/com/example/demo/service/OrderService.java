package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.RequestWrapper;

@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private ProductService // 调用商品服务
    productService;

    @Autowired
    private UserService // 调用用户服务
    userService;

    @Trace
    public Order createOrder(Order order) {
        List<Object> params = new ArrayList();
        params.add(order);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.OrderService.createOrder(com.example.demo.model.Order)", params);
        return restTemplate.postForObject("http://127.0.0.1:18501/mapper", wrapper, Order.class);
    }

    @Trace
    public OrderDetail getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Trace
    public PaymentRecord getPaymentRecordByOrderId(Long orderId) {
        return paymentRecordRepository.findByOrderId(orderId);
    }

    @Trace
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            OrderDetail detail = getOrderDetailsByOrderId(orderId);
            PaymentRecord payment = getPaymentRecordByOrderId(orderId);
            order.setOrderDetail(detail);
            order.setPaymentRecord(payment);
        }
        return order;
    }
}
