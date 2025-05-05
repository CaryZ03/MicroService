package com.micro.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class StaticParseNode {
    @SerializedName("func_name")
    private String funcName;
    @SerializedName("imports")
    private List<String> imports;
    @SerializedName("children")
    private List<StaticParseNode> children;
    @SerializedName("use_variables")
    private List<String> useVariables;
    @SerializedName("def_variables")
    private List<String> defVariables;
    @SerializedName("global_variables")
    private List<String> globalVariables;
    @SerializedName("node_type")
    private String nodeType;
    @SerializedName("node")
    private String node;
    @SerializedName("lineno")
    private Integer lineno;
    @SerializedName("end_lineno")
    private Integer endLineno;
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("return_variables")
    private List<String> returnVariables;
    @SerializedName("docstring")
    private String docstring;

    public StaticParseNode() {
        this.imports = new ArrayList<>();
        this.children = new ArrayList<>();
        this.useVariables = new ArrayList<>();
        this.defVariables = new ArrayList<>();
        this.globalVariables = new ArrayList<>();
        this.returnVariables = new ArrayList<>();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Getter 和 Setter 方法
    public String getFuncName() { return funcName; }
    public void setFuncName(String funcName) { this.funcName = funcName; }

    public List<String> getImports() { return imports; }
    public void setImports(List<String> imports) { this.imports = imports; }

    public List<StaticParseNode> getChildren() { return children; }
    public void setChildren(List<StaticParseNode> children) { this.children = children; }

    public List<String> getUseVariables() { return useVariables; }
    public void setUseVariables(List<String> useVariables) { this.useVariables = useVariables; }

    public List<String> getDefVariables() { return defVariables; }
    public void setDefVariables(List<String> defVariables) { this.defVariables = defVariables; }

    public List<String> getGlobalVariables() { return globalVariables; }
    public void setGlobalVariables(List<String> globalVariables) { this.globalVariables = globalVariables; }

    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }

    public String getNode() { return node; }
    public void setNode(String node) { this.node = node; }

    public Integer getLineno() { return lineno; }
    public void setLineno(Integer lineno) { this.lineno = lineno; }

    public Integer getEndLineno() { return endLineno; }
    public void setEndLineno(Integer endLineno) { this.endLineno = endLineno; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public List<String> getReturnVariables() { return returnVariables; }
    public void setReturnVariables(List<String> returnVariables) { this.returnVariables = returnVariables; }

    public String getDocstring() { return docstring; }
    public void setDocstring(String docstring) { this.docstring = docstring; }
}