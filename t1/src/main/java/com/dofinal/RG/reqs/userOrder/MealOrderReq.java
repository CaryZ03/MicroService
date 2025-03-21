package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.entity.order.MealCustomerOrder;
import com.dofinal.RG.reqs.userOrder.CustomerOrderReq;

import java.util.ArrayList;
import java.util.List;

/**
 * &#064;Classname MealOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/26 21:30
 * &#064;Created MuJue
 */
public class MealOrderReq extends CustomerOrderReq {
    private String tid;
    private int mid;
    public MealOrderReq(){;}
    public MealOrderReq(String uid, List<Integer> c_ids, String tid, int mid) {
        super(uid, c_ids);
        this.tid = tid;
        this.mid = mid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public List<MealCustomerOrder> getInstance(){
        List<MealCustomerOrder> orders = new ArrayList<>();
        for(Integer cid : cids){
            orders.add(new MealCustomerOrder(-1, cid, mid, tid, null, 0.0));
        }
        return orders;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}
