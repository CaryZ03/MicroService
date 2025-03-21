package com.example.demo.repository;

import com.example.demo.model.OrderDetail;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Trace
    OrderDetail findByOrderId(Long orderId);
}