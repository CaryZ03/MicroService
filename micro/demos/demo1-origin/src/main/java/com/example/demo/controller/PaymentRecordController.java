package com.example.demo.controller;

import com.example.demo.model.PaymentRecord;
import com.example.demo.service.PaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@RestController
@RequestMapping("/payment-records")
public class PaymentRecordController {

    @Autowired
    private PaymentRecordService paymentRecordService;

    @PostMapping
    @Trace()
    public PaymentRecord addPaymentRecord(@RequestBody PaymentRecord paymentRecord) {
        return paymentRecordService.addPaymentRecord(paymentRecord);
    }

    @GetMapping("/{orderId}")
    @Trace()
    public PaymentRecord getPaymentRecordByOrderId(@PathVariable Long orderId) {
        return paymentRecordService.getPaymentRecordByOrderId(orderId);
    }
}
