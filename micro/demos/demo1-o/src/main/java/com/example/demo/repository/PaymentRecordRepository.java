package com.example.demo.repository;

import com.example.demo.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    @Trace
    PaymentRecord findByOrderId(Long orderId);
}
