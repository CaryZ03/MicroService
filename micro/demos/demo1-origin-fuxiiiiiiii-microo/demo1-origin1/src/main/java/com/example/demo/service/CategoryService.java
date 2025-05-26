package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.RequestWrapper;

@Service
public class CategoryService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Trace
    public Category addCategory(Category category) {
        List<Object> params = new ArrayList();
        params.add(category);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.CategoryService.addCategory(com.example.demo.model.Category)", params);
        return restTemplate.postForObject("http://127.0.0.1:18503/mapper", wrapper, Category.class);
    }

    @Trace
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
}
