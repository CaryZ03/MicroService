package com.micro.test.utils;

import java.util.List;

public class RequestWrapper {
    private String path;
    private List<Object> params;

    public RequestWrapper(String path, List<Object> params) {
        this.path = path;
        this.params = params;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}