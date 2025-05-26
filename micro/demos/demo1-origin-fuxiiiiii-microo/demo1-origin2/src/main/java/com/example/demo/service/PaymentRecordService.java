package com.example.demo.service;

import com.example.demo.model.PaymentRecord;
import com.example.demo.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.RequestWrapper;

@Service
public class PaymentRecordService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Trace
    public PaymentRecord addPaymentRecord(PaymentRecord paymentRecord) {
        return paymentRecordRepository.save(paymentRecord);
    }

    @Trace
    public PaymentRecord getPaymentRecordByOrderId(Long orderId) {
        List<Object> params = new ArrayList();
        params.add(orderId);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.PaymentRecordService.getPaymentRecordByOrderId(java.lang.Long)", params);
        return restTemplate.postForObject("http://127.0.0.1:18503/mapper", wrapper, PaymentRecord.class);
    }
}
