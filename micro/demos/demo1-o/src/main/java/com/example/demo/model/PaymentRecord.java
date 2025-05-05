package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private String status;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @Trace
    public Long getId() {
        return id;
    }

    @Trace
    public void setId(Long id) {
        this.id = id;
    }

    @Trace
    public double getPrice() {
        return price;
    }

    @Trace
    public void setPrice(double price) {
        this.price = price;
    }

    @Trace
    public String getStatus() {
        return status;
    }

    @Trace
    public void setStatus(String status) {
        this.status = status;
    }

    @Trace
    public Order getOrder() {
        return order;
    }

    @Trace
    public void setOrder(Order order) {
        this.order = order;
    }
}
