package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Trace()
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Trace()
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Trace()
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
