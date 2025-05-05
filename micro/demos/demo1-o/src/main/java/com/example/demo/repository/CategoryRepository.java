package com.example.demo.repository;

import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Trace
    Category findByName(String name);
}
