package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    // Getters and Setters
    @Trace
    public Long getId() {
        return id;
    }

    @Trace
    public void setId(Long id) {
        this.id = id;
    }

    @Trace
    public String getName() {
        return name;
    }

    @Trace
    public void setName(String name) {
        this.name = name;
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
    public int getStock() {
        return stock;
    }

    @Trace
    public void setStock(int stock) {
        this.stock = stock;
    }

    @Trace
    public Category getCategory() {
        return category;
    }

    @Trace
    public void setCategory(Category category) {
        this.category = category;
    }
}
