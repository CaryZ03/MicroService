package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "order")
    @JsonManagedReference
    private OrderDetail orderDetail;

    @OneToOne(mappedBy = "order")
    @JsonManagedReference
    private PaymentRecord paymentRecord;

    @Trace
    public Long getId() {
        return id;
    }

    @Trace
    public void setId(Long id) {
        this.id = id;
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
    public User getUser() {
        return user;
    }

    @Trace
    public void setUser(User user) {
        this.user = user;
    }

    @Trace
    public Product getProduct() {
        return product;
    }

    @Trace
    public void setProduct(Product product) {
        this.product = product;
    }

    @Trace
    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    @Trace
    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    @Trace
    public PaymentRecord getPaymentRecord() {
        return paymentRecord;
    }

    @Trace
    public void setPaymentRecord(PaymentRecord paymentRecord) {
        this.paymentRecord = paymentRecord;
    }
}
