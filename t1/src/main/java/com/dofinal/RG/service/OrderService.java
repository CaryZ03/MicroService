package com.dofinal.RG.service;

import com.dofinal.RG.entity.hotel.HotelComment;
import com.dofinal.RG.entity.order.UserOrderDemo;
import com.dofinal.RG.exceptions.PurchaseException;
import com.dofinal.RG.reqs.entity.HotelCommentReq;
import com.dofinal.RG.reqs.userOrder.AddUserOrderReq;
import com.dofinal.RG.reqs.userOrder.OtherUserOrderReq;
import com.dofinal.RG.rsps.Notice.NoticeRsp;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.List;

/**
 * &#064;Classname OrderService
 * &#064;Description TODO
 * &#064;Date 2024/6/2 17:47
 * &#064;Created MuJue
 */
public interface OrderService {
    @Trace
    int rateHotel(HotelCommentReq req);

    @Trace
    HotelComment getHotelComment(HotelCommentReq req);

    @Trace
    int deleteHotelComment(HotelCommentReq req);

    @Trace
    NoticeRsp generateUserOrder(AddUserOrderReq req);// 生成用户订单

    @Trace
    NoticeRsp payUserOrder(OtherUserOrderReq req) throws PurchaseException; // 支付用户订单

    @Trace
    NoticeRsp cancelUserOrder(OtherUserOrderReq req); // 取消用户订单

    @Trace
    NoticeRsp deleteUserOrder(OtherUserOrderReq req);// 删除用户订单

    @Trace
    List<UserOrderDemo> getUserOrdersByUid(String uid);
}
