package com.example.demo.repository;

import com.example.demo.model.PaymentRecord;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    @Trace
    PaymentRecord findByOrderId(Long orderId);
}