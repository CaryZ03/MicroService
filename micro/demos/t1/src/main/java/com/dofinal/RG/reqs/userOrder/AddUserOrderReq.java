package com.dofinal.RG.reqs.userOrder;

import com.dofinal.RG.reqs.BaseReq;

/**
 * &#064;Classname AddUserOrderReq
 * &#064;Description  TODO
 * &#064;Date 2024/5/26 21:55
 * &#064;Created MuJue
 */
public class AddUserOrderReq extends BaseReq {
    private String orderType;
    private TrainOrderReq tor;
    private MealOrderReq mor;
    private HotelOrderReq hor;

    public AddUserOrderReq(String uid, String orderType, TrainOrderReq tor, MealOrderReq mor, HotelOrderReq hor) {
        super(uid);
        this.orderType = orderType;
        this.tor = tor;
        this.mor = mor;
        this.hor = hor;
    }

    public AddUserOrderReq(){;}

    public TrainOrderReq getTor() {
        return tor;
    }

    public void setTor(TrainOrderReq tor) {
        this.tor = tor;
    }

    public MealOrderReq getMor() {
        return mor;
    }

    public void setMor(MealOrderReq mor) {
        this.mor = mor;
    }

    public HotelOrderReq getHor() {
        return hor;
    }

    public void setHor(HotelOrderReq hor) {
        this.hor = hor;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
