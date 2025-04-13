package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Trace()
    Product findByName(String name);
}
