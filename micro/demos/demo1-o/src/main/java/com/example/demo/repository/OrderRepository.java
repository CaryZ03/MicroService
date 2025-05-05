package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Trace
    List<Order> findByUserId(Long userId);
}
