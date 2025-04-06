package com.dofinal.RG.rsps.Notice;

import java.util.List;

/**
 * &#064;Classname NoticeRsp
 * &#064;Description  TODO
 * &#064;Date 2024/6/9 11:45
 * &#064;Created MuJue
 */
public class NoticeRsp {
    int oid;
    double price;
    List<String> customerName;
    String mainName;
    String operationType;
    String orderType;
    int ret;
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    public NoticeRsp(){;}

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public List<String> getCustomerName() {
        return customerName;
    }

    public void setCustomerName(List<String> customerName) {
        this.customerName = customerName;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }
}
