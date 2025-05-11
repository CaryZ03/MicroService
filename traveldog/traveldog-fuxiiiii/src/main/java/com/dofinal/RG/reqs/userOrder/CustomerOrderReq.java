package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.reqs.BaseReq;

import java.util.List;

/**
 * &#064;Classname CustomerOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/26 21:30
 * &#064;Created MuJue
 */
public class CustomerOrderReq extends BaseReq {
    protected List<Integer> cids;

    public CustomerOrderReq(String uid, List<Integer> cids) {
        super(uid);
        this.cids = cids;
    }
    public CustomerOrderReq(){;}

    public List<Integer> getCids() {
        return cids;
    }

    public void setCids(List<Integer> cids) {
        this.cids = cids;
    }
}
