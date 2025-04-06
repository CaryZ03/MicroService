package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Trace
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Trace
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
}