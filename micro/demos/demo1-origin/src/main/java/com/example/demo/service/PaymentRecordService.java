package com.example.demo.service;

import com.example.demo.model.PaymentRecord;
import com.example.demo.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Service
public class PaymentRecordService {

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Trace()
    public PaymentRecord addPaymentRecord(PaymentRecord paymentRecord) {
        return paymentRecordRepository.save(paymentRecord);
    }

    @Trace()
    public PaymentRecord getPaymentRecordByOrderId(Long orderId) {
        return paymentRecordRepository.findByOrderId(orderId);
    }
}
