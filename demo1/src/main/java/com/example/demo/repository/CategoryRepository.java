package com.example.demo.repository;

import com.example.demo.model.Category;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Trace
    Category findByName(String name);
}