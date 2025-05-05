package com.micro.test;

import java.util.List;
import java.util.Map;

public class ServiceTemp {
    private String target_path;
    private Map<String, Integer> partition;
    private String port;
    private List<String> functions;
    private List<String> ins;
    private List<String> outs;

    public String getTarget_path() {
        return target_path;
    }

    public void setTarget_path(String target_path) {
        this.target_path = target_path;
    }

    public Map<String, Integer> getPartition() {
        return partition;
    }

    public void setPartition(Map<String, Integer> partition) {
        this.partition = partition;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public List<String> getIns() {
        return ins;
    }

    public void setIns(List<String> ins) {
        this.ins = ins;
    }

    public List<String> getOuts() {
        return outs;
    }

    public void setOuts(List<String> outs) {
        this.outs = outs;
    }
}
