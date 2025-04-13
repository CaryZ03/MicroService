package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private double totalPrice;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @Trace()
    public Long getId() {
        return id;
    }

    @Trace()
    public void setId(Long id) {
        this.id = id;
    }

    @Trace()
    public int getQuantity() {
        return quantity;
    }

    @Trace()
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Trace()
    public double getTotalPrice() {
        return totalPrice;
    }

    @Trace()
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Trace()
    public Order getOrder() {
        return order;
    }

    @Trace()
    public void setOrder(Order order) {
        this.order = order;
    }
}
