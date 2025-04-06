package com.dofinal.RG.reqs;

/**
 * &#064;Classname BaseReq
 * &#064;Description  TODO
 * &#064;Date 2024/6/7 21:20
 * &#064;Created MuJue
 */
public class BaseReq {
    protected String uid;

    public BaseReq(String uid) {
        this.uid = uid;
    }
    public BaseReq(){;}
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
