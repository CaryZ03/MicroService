package com.dofinal.RG.reqs.entity;

import com.dofinal.RG.reqs.BaseReq;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * &#064;Classname AuthenticationReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/10 21:53
 * &#064;Created MuJue
 */
public class AuthenticationReq extends BaseReq {
    private String password;
    public AuthenticationReq(String uid, String password) {
        super(uid);
        this.password = password;
    }
    public AuthenticationReq(){
        super();
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}
