package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.PaymentRecordRepository;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private ProductService productService; // 调用商品服务

    @Autowired
    private UserService userService; // 调用用户服务

    @Trace
    public Order createOrder(Order order) {
        // 设置用户信息
        User user = userService.getUserById(order.getUser().getId());
        order.setUser(user);

        // 设置商品信息
        Product product = productService.getProductById(order.getProduct().getId());
        order.setProduct(product);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setOrder(savedOrder);
        paymentRecord.setPrice(order.getProduct().getPrice());
        paymentRecord.setStatus("Pending");
        paymentRecordRepository.save(paymentRecord);

        return savedOrder;
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