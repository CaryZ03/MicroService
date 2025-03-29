package com.example.testProject.api;

import java.util.Map;
import java.util.HashMap;

public class ApiRequest {
    private String path;
    private final String method = "POST"; 
    private Map<String, Object> body;

    public ApiRequest(String path, Map<String, Object> body) {
        this.path = path;
        this.body = body != null ? body : new HashMap<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", body=" + body +
                '}';
    }
}
