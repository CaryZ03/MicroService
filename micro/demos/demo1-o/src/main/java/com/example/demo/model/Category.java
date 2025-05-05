package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Product> products;

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
    public List<Product> getProducts() {
        return products;
    }

    @Trace
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
