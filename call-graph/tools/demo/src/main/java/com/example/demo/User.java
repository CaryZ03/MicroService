package com.example.demo;

import org.apache.skywalking.apm.toolkit.trace.Trace;

public class User {

    private Long id;

    private String username;

    // Getters and Setters
    @Trace
    public Long getId() {
        return id;
    }

    @Trace
    public void setId(Long id) {
        this.id = id;
    }

    @Trace
    public String getUsername() {
        return username;
    }

    @Trace
    public void setUsername(String username) {
        this.username = username;
    }
}
