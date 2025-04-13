package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    // Getters and Setters
    @Trace()
    public Long getId() {
        return id;
    }

    @Trace()
    public void setId(Long id) {
        this.id = id;
    }

    @Trace()
    public String getUsername() {
        return username;
    }

    @Trace()
    public void setUsername(String username) {
        this.username = username;
    }

    @Trace()
    public String getPassword() {
        return password;
    }

    @Trace()
    public void setPassword(String password) {
        this.password = password;
    }

    @Trace()
    public String getEmail() {
        return email;
    }

    @Trace()
    public void setEmail(String email) {
        this.email = email;
    }
}
