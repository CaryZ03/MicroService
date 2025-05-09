package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.reqs.BaseReq;

/**
 * &#064;Classname OtherUserOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/27 21:25
 * &#064;Created MuJue
 */
public class OtherUserOrderReq extends BaseReq {
    private int oid;

    public OtherUserOrderReq(String uid, int oid) {
        super(uid);
        this.oid = oid;
    }
    public OtherUserOrderReq(){;}
    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }
}
