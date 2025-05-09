package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.entity.user.Customer;
import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * &#064;Classname CustomerReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/17 21:53
 * &#064;Created MuJue
 */
public class CustomerReq extends BaseReq {
    @JsonProperty("cid")
    private int cid;
    @JsonProperty("cName")
    private String cName;
    @JsonProperty("cTel")
    private String cTel;
    @JsonProperty("idCard")
    private String idCard;
    @JsonProperty("cAge")
    private int cAge;

    public CustomerReq(String uid, int cid, String cName, String cTel, String idCard, int cAge) {
        super(uid);
        this.cid = cid;
        this.cName = cName;
        this.cTel = cTel;
        this.idCard = idCard;
        this.cAge = cAge;
    }

    public CustomerReq(){;}
    public Customer getCustomer(){
        return new Customer(-1, cName, cTel, idCard, cAge);
    }
    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getcTel() {
        return cTel;
    }

    public void setcTel(String cTel) {
        this.cTel = cTel;
    }

    public int getcAge() {
        return cAge;
    }

    public void setcAge(int cAge) {
        this.cAge = cAge;
    }
}
