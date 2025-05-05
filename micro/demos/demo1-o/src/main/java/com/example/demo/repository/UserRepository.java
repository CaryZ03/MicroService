package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Trace
    List<User> findByUsername(String username);
}
