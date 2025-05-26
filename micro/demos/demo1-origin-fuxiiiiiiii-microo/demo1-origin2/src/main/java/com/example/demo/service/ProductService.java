package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.RequestWrapper;

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
        List<Object> params = new ArrayList();
        params.add(name);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.ProductService.getProductByName(java.lang.String)", params);
        return restTemplate.postForObject("http://127.0.0.1:18503/mapper", wrapper, Product.class);
    }

    @Trace
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
