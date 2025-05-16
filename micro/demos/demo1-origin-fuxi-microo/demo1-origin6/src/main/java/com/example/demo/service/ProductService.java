package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo1.RequestWrapper;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Trace
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Trace
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Trace
    public Product getProductById(Long id) {
        List<Object> params = new ArrayList();
        params.add(id);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.ProductService.getProductById(java.lang.Long)", params);
        return restTemplate.postForObject("http://127.0.0.1:18503/mapper", wrapper, Product.class);
    }
}
